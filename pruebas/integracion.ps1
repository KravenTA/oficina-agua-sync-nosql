<#
HU-58: pruebas de integración de la replicación, de punta a punta con Docker Compose.

Levanta la feature desde cero (BORRA los datos locales de MySQL y DynamoDB), ejecuta los casos y
guarda la evidencia en docs/evidencias/. Solo necesita Docker Desktop: no usa Java ni Maven.

Casos:
  1. Destino vacío: se replica todo el resumen.
  2. Repetición: una segunda ejecución no escribe nada (todo sin cambios).
  3. Cambios parciales: solo se escriben los registros nuevos y modificados.
  4. Nunca borra: un registro eliminado del origen se conserva en DynamoDB.
  5. Fallo del destino: con DynamoDB caído el job queda FALLIDO con un motivo seguro, sin afectar
     a MySQL, y la siguiente ejecución se recupera.
  6. Sin datos sensibles: ningún registro de DynamoDB trae DPI, NIT, teléfono, correo ni documentos.

Uso, desde la raíz del repositorio:
  powershell -ExecutionPolicy Bypass -File .\pruebas\integracion.ps1
#>
param(
    [string]$Url = "http://localhost:8083"
)

# "Continue" a propósito: docker escribe su progreso en stderr y Windows PowerShell 5.1 lo trataría
# como error. Cada comando se comprueba con su código de salida.
$ErrorActionPreference = "Continue"
Set-Location (Join-Path $PSScriptRoot "..")

# --- Configuración tomada del .env ------------------------------------------------------------
function Leer-Env {
    $valores = @{}
    if (Test-Path ".env") {
        foreach ($linea in Get-Content ".env" -Encoding UTF8) {
            if ($linea -match '^\s*([A-Z_]+)\s*=\s*(.*)$') { $valores[$Matches[1]] = $Matches[2].Trim() }
        }
    }
    return $valores
}
$envVars = Leer-Env
function Valor($nombre, $defecto) { if ($envVars[$nombre]) { $envVars[$nombre] } else { $defecto } }

$apiKey = Valor "SYNC_API_KEY" ""
$rootPassword = Valor "ORIGEN_DB_ROOT_PASSWORD" "cambiar_esta_password_root"
$roUser = Valor "ORIGEN_DB_USER" "sync_ro"
$roPassword = Valor "ORIGEN_DB_PASSWORD" "cambiar_esta_password"
$baseDatos = Valor "ORIGEN_DB_NAME" "oficina_agua"
$tabla = Valor "DYNAMODB_TABLE_RESUMEN" "oficina_agua_resumen"

if (-not $apiKey) {
    Write-Host "Falta SYNC_API_KEY en el archivo .env (copiar .env.example a .env)." -ForegroundColor Red
    exit 1
}

# --- Utilidades -------------------------------------------------------------------------------
$resultados = New-Object System.Collections.ArrayList

function Paso([string]$texto) { Write-Host ""; Write-Host "== $texto" -ForegroundColor Cyan }

function Verificar([string]$caso, [string]$comprobacion, [bool]$cumple, [string]$obtenido) {
    [void]$resultados.Add([pscustomobject]@{ Caso = $caso; Comprobacion = $comprobacion; Cumple = $cumple; Obtenido = $obtenido })
    $color = if ($cumple) { "Green" } else { "Red" }
    $marca = if ($cumple) { "OK   " } else { "FALLA" }
    Write-Host ("  [{0}] {1} -> {2}" -f $marca, $comprobacion, $obtenido) -ForegroundColor $color
}

function Ejecutar-Docker([string[]]$argumentos) {
    $salida = & docker @argumentos 2>&1 | ForEach-Object { "$_" }
    return [pscustomobject]@{ Codigo = $LASTEXITCODE; Salida = ($salida -join "`n") }
}

function Sql-Origen([string]$sql) {
    # Como root: simula los cambios que hace el monolito en su base de datos.
    $salida = & docker exec sync-mysql-origen mysql -uroot "-p$rootPassword" -N -B $baseDatos -e $sql 2>$null
    return ($salida -join "`n").Trim()
}

function Conteos-Origen {
    $fila = Sql-Origen "SELECT (SELECT COUNT(*) FROM clientes), (SELECT COUNT(*) FROM lecturas), (SELECT COUNT(*) FROM pagos)"
    $partes = $fila -split "\s+"
    return [pscustomobject]@{ Clientes = [int]$partes[0]; Consumos = [int]$partes[1]; Pagos = [int]$partes[2] }
}

function Items-Destino {
    $salida = & docker compose run --rm --entrypoint aws dynamodb-init dynamodb scan `
        --table-name $tabla --endpoint-url http://dynamodb:8000 --output json 2>$null
    if ($LASTEXITCODE -ne 0) { return $null }
    return @((($salida -join "`n") | ConvertFrom-Json).Items)
}

function Pedir([string]$metodo, [string]$ruta) {
    try {
        $r = Invoke-WebRequest -Uri ($Url + $ruta) -Method $metodo -Headers @{ "X-API-Key" = $apiKey } -UseBasicParsing -TimeoutSec 30
        $texto = [Text.Encoding]::UTF8.GetString($r.RawContentStream.ToArray())
        return [pscustomobject]@{ Estado = [int]$r.StatusCode; Json = ($texto | ConvertFrom-Json) }
    } catch {
        $codigo = if ($_.Exception.Response) { [int]$_.Exception.Response.StatusCode } else { 0 }
        return [pscustomobject]@{ Estado = $codigo; Json = $null }
    }
}

# Lanza Generate y espera a que el job termine (COMPLETADO o FALLIDO) consultando GetJob.
function Replicar {
    $creado = Pedir "POST" "/api/sync/resumen"
    if ($creado.Estado -ne 202) { return [pscustomobject]@{ estado = "SIN_RESPUESTA ($($creado.Estado))" } }
    $limite = (Get-Date).AddSeconds(90)
    do {
        Start-Sleep -Milliseconds 700
        $job = (Pedir "GET" "/api/sync/jobs/$($creado.Json.jobId)").Json
    } while ($job -and $job.estado -in @("PENDIENTE", "EN_PROGRESO") -and (Get-Date) -lt $limite)
    return $job
}

function Conteo($c) { if ($c) { "$($c.insertados) insertados, $($c.actualizados) actualizados, $($c.sinCambios) sin cambios" } else { "sin conteos" } }
function Igual($c, [int]$ins, [int]$act, [int]$sin) { $c -and $c.insertados -eq $ins -and $c.actualizados -eq $act -and $c.sinCambios -eq $sin }

function Esperar([string]$que, [scriptblock]$listo, [int]$segundos) {
    $limite = (Get-Date).AddSeconds($segundos)
    while (-not (& $listo)) {
        if ((Get-Date) -gt $limite) { Write-Host "No estuvo listo a tiempo: $que" -ForegroundColor Red; exit 1 }
        Start-Sleep -Seconds 2
    }
}

# --- Preparación: la feature desde cero --------------------------------------------------------
Paso "Preparación: docker compose down -v y up -d --build (se borran los datos locales)"
[void](Ejecutar-Docker @("compose", "down", "-v"))
$up = Ejecutar-Docker @("compose", "--progress", "plain", "up", "-d", "--build")
if ($up.Codigo -ne 0) { Write-Host (($up.Salida -split "`n" | Select-Object -Last 40) -join "`n"); Write-Host "docker compose up falló." -ForegroundColor Red; exit 1 }

# MySQL termina de cargar sus scripts antes de aceptar conexiones por TCP; se prueba con el
# usuario de solo lectura, igual que el sync-service.
Esperar "MySQL de origen" { (& docker exec sync-mysql-origen mysql -h 127.0.0.1 "-u$roUser" "-p$roPassword" -N -e "SELECT COUNT(*) FROM $baseDatos.pagos" 2>$null) -match '^\d+$' } 180
Esperar "sync-service" { try { (Invoke-WebRequest "$Url/openapi/sync-api.yaml" -UseBasicParsing -TimeoutSec 5).StatusCode -eq 200 } catch { $false } } 180
$origen = Conteos-Origen
$total = $origen.Clientes + $origen.Consumos + $origen.Pagos
Write-Host "  Origen: $($origen.Clientes) clientes, $($origen.Consumos) consumos y $($origen.Pagos) pagos."

# --- Caso 1: destino vacío ---------------------------------------------------------------------
$caso = "1. Destino vacío"
Paso $caso
$job = Replicar
Verificar $caso "El job termina COMPLETADO" ($job.estado -eq "COMPLETADO") $job.estado
Verificar $caso "Clientes: todos insertados" (Igual $job.detalle.clientes $origen.Clientes 0 0) (Conteo $job.detalle.clientes)
Verificar $caso "Consumos: todos insertados" (Igual $job.detalle.consumos $origen.Consumos 0 0) (Conteo $job.detalle.consumos)
Verificar $caso "Pagos: todos insertados" (Igual $job.detalle.pagos $origen.Pagos 0 0) (Conteo $job.detalle.pagos)
$items = Items-Destino
Verificar $caso "DynamoDB tiene un registro por cada registro del origen" (@($items).Count -eq $total) "$(@($items).Count) de $total"

# --- Caso 2: repetición ------------------------------------------------------------------------
$caso = "2. Repetición sin cambios"
Paso $caso
$job = Replicar
Verificar $caso "El job termina COMPLETADO" ($job.estado -eq "COMPLETADO") $job.estado
Verificar $caso "No escribe nada: todo sin cambios" (Igual $job.registros 0 0 $total) (Conteo $job.registros)

# --- Caso 3: cambios parciales -----------------------------------------------------------------
$caso = "3. Cambios parciales"
Paso $caso
[void](Sql-Origen "UPDATE clientes SET direccion = CONCAT(direccion, ' (actualizada)') WHERE id = 2; UPDATE pagos SET metodo = 'transferencia' WHERE id = 1; INSERT INTO clientes (nombre, direccion, activo, created_at, updated_at) VALUES ('Cliente de prueba HU-58', 'Aldea Nueva, Jutiapa', 1, NOW(), NOW());")
$idNuevo = Sql-Origen "SELECT id FROM clientes WHERE nombre = 'Cliente de prueba HU-58'"
$job = Replicar
Verificar $caso "El job termina COMPLETADO" ($job.estado -eq "COMPLETADO") $job.estado
Verificar $caso "Clientes: 1 insertado, 1 actualizado" (Igual $job.detalle.clientes 1 1 ($origen.Clientes - 1)) (Conteo $job.detalle.clientes)
Verificar $caso "Consumos: sin cambios" (Igual $job.detalle.consumos 0 0 $origen.Consumos) (Conteo $job.detalle.consumos)
Verificar $caso "Pagos: 1 actualizado" (Igual $job.detalle.pagos 0 1 ($origen.Pagos - 1)) (Conteo $job.detalle.pagos)
$items = Items-Destino
$cliente2 = @($items | Where-Object { $_.entidad.S -eq "CLIENTE" -and $_.id.S -eq "2" })[0]
Verificar $caso "DynamoDB tiene la dirección actualizada del cliente 2" ($cliente2.direccion.S -like "*(actualizada)") $cliente2.direccion.S

# --- Caso 4: nunca borra -----------------------------------------------------------------------
$caso = "4. Nunca borra"
Paso $caso
[void](Sql-Origen "DELETE FROM clientes WHERE id = $idNuevo")
$job = Replicar
Verificar $caso "El job termina COMPLETADO" ($job.estado -eq "COMPLETADO") $job.estado
Verificar $caso "Clientes: nada que escribir" (Igual $job.detalle.clientes 0 0 $origen.Clientes) (Conteo $job.detalle.clientes)
$items = Items-Destino
$conservado = @($items | Where-Object { $_.entidad.S -eq "CLIENTE" -and $_.id.S -eq "$idNuevo" }).Count -eq 1
Verificar $caso "El cliente $idNuevo, borrado del origen, sigue en DynamoDB" $conservado "$(@($items).Count) registros en DynamoDB"

# --- Caso 5: fallo del destino -----------------------------------------------------------------
$caso = "5. Fallo del destino"
Paso $caso
$antes = Conteos-Origen
[void](Ejecutar-Docker @("compose", "stop", "dynamodb"))
$job = Replicar
Verificar $caso "Con DynamoDB caído el job queda FALLIDO" ($job.estado -eq "FALLIDO") $job.estado
Verificar $caso "El motivo es claro y sin detalles internos" ($job.motivoFallo -and $job.motivoFallo -notmatch "http|:8000|dynamodb:") "$($job.motivoFallo)"
$despues = Conteos-Origen
Verificar $caso "MySQL de origen no cambió" (($antes | ConvertTo-Json) -eq ($despues | ConvertTo-Json)) "$($despues.Clientes) clientes, $($despues.Consumos) consumos, $($despues.Pagos) pagos"
[void](Ejecutar-Docker @("compose", "start", "dynamodb"))
Esperar "DynamoDB" { $null -ne (Items-Destino) } 60
$job = Replicar
Verificar $caso "Con DynamoDB de vuelta, la siguiente ejecución se recupera" ($job.estado -eq "COMPLETADO" -and $job.registros.insertados -eq 0 -and $job.registros.actualizados -eq 0) "$($job.estado): $(Conteo $job.registros)"

# --- Caso 6: sin datos sensibles ---------------------------------------------------------------
$caso = "6. Sin datos sensibles"
Paso $caso
$sensibles = @("dpi", "nit", "telefono", "email", "foto_vivienda", "recibo_luz", "fotoVivienda", "reciboLuz")
$encontrados = @($items | ForEach-Object { $_.PSObject.Properties.Name } | Where-Object { $_ -in $sensibles } | Select-Object -Unique)
Verificar $caso "Ningún registro trae DPI, NIT, teléfono, correo ni documentos" ($encontrados.Count -eq 0) $(if ($encontrados.Count) { $encontrados -join ", " } else { "ninguno en $(@($items).Count) registros" })

# --- Evidencia ---------------------------------------------------------------------------------
$aprobadas = @($resultados | Where-Object { $_.Cumple }).Count
$fecha = Get-Date
$md = New-Object Text.StringBuilder
[void]$md.AppendLine("# Pruebas de integración de la replicación (HU-58)")
[void]$md.AppendLine("")
[void]$md.AppendLine("- Fecha: $($fecha.ToString('yyyy-MM-dd HH:mm'))")
[void]$md.AppendLine("- Entorno: docker compose (MySQL de origen, DynamoDB Local y sync-service), levantado desde cero")
[void]$md.AppendLine("- Origen inicial: $($origen.Clientes) clientes, $($origen.Consumos) consumos y $($origen.Pagos) pagos")
[void]$md.AppendLine("- Resultado: **$aprobadas de $($resultados.Count) comprobaciones aprobadas**")
[void]$md.AppendLine("")
[void]$md.AppendLine("| Caso | Comprobación | Obtenido | Resultado |")
[void]$md.AppendLine("|---|---|---|---|")
foreach ($r in $resultados) {
    $estado = if ($r.Cumple) { "Aprobado" } else { "Falla" }
    [void]$md.AppendLine("| $($r.Caso) | $($r.Comprobacion) | $($r.Obtenido -replace '\|', '\|') | $estado |")
}
[void]$md.AppendLine("")
[void]$md.AppendLine("Cómo reproducirlo: ``powershell -ExecutionPolicy Bypass -File .\pruebas\integracion.ps1`` desde la raíz del repositorio.")

$carpeta = "docs\evidencias"
New-Item -ItemType Directory -Force $carpeta | Out-Null
$archivo = Join-Path $carpeta ("pruebas-integracion-" + $fecha.ToString("yyyy-MM-dd_HHmm") + ".md")
[IO.File]::WriteAllText((Join-Path (Get-Location) $archivo), $md.ToString(), (New-Object Text.UTF8Encoding($false)))

Write-Host ""
$colorTotal = if ($aprobadas -eq $resultados.Count) { "Green" } else { "Yellow" }
Write-Host "Aprobadas: $aprobadas de $($resultados.Count)" -ForegroundColor $colorTotal
Write-Host "Evidencia guardada en: $archivo"
Write-Host "Nota: los datos de origen quedaron modificados por las pruebas. Para restaurarlos: docker compose down -v; docker compose up -d"
if ($aprobadas -ne $resultados.Count) { exit 1 }
