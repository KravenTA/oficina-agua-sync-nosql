# sync-service — Replicación asíncrona (Spring Boot)

Servicio que replica el resumen de la instancia MySQL (solo lectura) hacia Amazon DynamoDB.

| HU | Qué se construye aquí |
|---|---|
| HU-51 | Contrato OpenAPI (`openapi/`), generación de interfaces y DTOs, Swagger UI (implementado) |
| HU-53 | `POST /api/sync/resumen` (Generate) y creación del GenerarJob, respuesta 202 + `jobId`, API key (implementado) |
| HU-54 | Thread en segundo plano y estados del job (implementado) |
| HU-55 | Diff entre la instancia MySQL y DynamoDB (nuevos, modificados, iguales) (implementado) |
| HU-56 | Upsert sin borrar en DynamoDB (implementado) |
| HU-57 | `GET /api/sync/jobs/{jobId}` (GetJob) (implementado) |

## Requisitos

- **JDK 21.** El proyecto compila con `release 21`. Con un JDK más nuevo (por ejemplo el 25) la
  compilación funciona, pero las pruebas que usan Mockito fallan: la versión de Byte Buddy que trae
  Spring Boot 3.3.5 no conoce Java 24 ni 25 (`Java 25 (69) is not supported by the current version
  of Byte Buddy`).
- Maven. Comprobar con `mvn -v` que la línea `Java version` diga 21; si no, apuntar `JAVA_HOME` al
  JDK 21.
- En IntelliJ: File → Project Structure → Project → SDK 21.

## Contrato (contract-first)

El contrato vive en [`openapi/sync-api.yaml`](openapi/sync-api.yaml) y es la fuente de verdad.
**Primero se cambia el YAML, después el código.**

| Método | Ruta | Qué hace | Respuestas |
|---|---|---|---|
| POST | `/api/sync/resumen` | Generate: crea el job y responde de inmediato | 202, 401, 500 |
| GET | `/api/sync/jobs/{jobId}` | GetJob: estado del job | 200, 400, 401, 404, 500 |

Estados del job: `PENDIENTE` → `EN_PROGRESO` → `COMPLETADO` o `FALLIDO`.

Los endpoints llevan la cabecera `X-API-Key`. Los errores usan el mismo formato
(`codigo`, `mensaje`, `fecha`): `JOB_ID_INVALIDO`, `JOB_NO_ENCONTRADO`, `NO_AUTORIZADO` y
`ERROR_INTERNO`.

## Generar las interfaces y los DTOs

`openapi-generator-maven-plugin` los genera en cada compilación, en `target/generated-sources/`
(no se versionan):

```powershell
cd sync-service
mvn clean package
```

| Qué se genera | Paquete |
|---|---|
| Interfaz `SincronizacionApi` (`iniciarSincronizacion`, `obtenerEstadoJob`) | `gt.edu.miumg.oficinaagua.sync.api` |
| DTOs (`JobCreado`, `JobEstado`, `EstadoJob`, `ConteoRegistros`, `DetalleRegistros`, `ErrorRespuesta`...) | `gt.edu.miumg.oficinaagua.sync.api.model` |

Los controladores de las HU-53 y HU-57 **implementan** `SincronizacionApi`; no se escriben las rutas a mano.

## Ver la documentación (Swagger UI)

Con el servicio en marcha (`SyncApplication` desde IntelliJ, o `mvn spring-boot:run`):

```
http://localhost:8083/swagger-ui.html
```

El puerto sale de `SYNC_PORT` (8083 por defecto). El contrato en bruto está en
`http://localhost:8083/openapi/sync-api.yaml`. Desde Swagger UI se puede probar
`POST /api/sync/resumen` (HU-53) y `GET /api/sync/jobs/{jobId}` (HU-57): pulsa **Authorize**,
escribe la API key y luego **Try it out**.

## Probar Generate (HU-53)

Los endpoints exigen la cabecera `X-API-Key`. El servicio la compara con la variable de entorno
`SYNC_API_KEY`; **si no está definida, rechaza todo con 401**.

```powershell
cd sync-service
$env:SYNC_API_KEY="mi-clave-local"
java -jar target\sync-service-0.0.1-SNAPSHOT.jar
```

(En IntelliJ: Run → Edit Configurations → `SyncApplication` → Environment variables → `SYNC_API_KEY=mi-clave-local`.)

En otra terminal:

```powershell
# Con la clave: 202 Accepted, cabecera Location y el jobId
curl.exe -i -X POST http://localhost:8083/api/sync/resumen -H "X-API-Key: mi-clave-local"

# Sin la clave o con otra: 401 NO_AUTORIZADO
curl.exe -i -X POST http://localhost:8083/api/sync/resumen

# Cuánto tarda en responder (debe ser menos de un segundo)
Measure-Command { curl.exe -s -o NUL -X POST http://localhost:8083/api/sync/resumen -H "X-API-Key: mi-clave-local" }
```

Cómo funciona:

- `SyncController` implementa `SincronizacionApi` y llama a `GenerarJob`.
- `GenerarJob` crea el `Job` en estado `PENDIENTE`, lo guarda en `JobRepositorio` (en memoria) y lo
  entrega al **Thread de segundo plano** (un solo hilo, `sync-thread`, así las replicaciones se
  hacen una tras otra). Responde sin esperar.
- El Thread usa `ProcesadorReplicacion`, que lleva el job por sus estados (ver abajo) y ejecuta la
  replicación a través de la interfaz `Replicador`.
### Estados del job (HU-54)

`ProcesadorReplicacion` es lo que hace el Thread con cada job:

| Paso | Qué pasa | Estado del job |
|---|---|---|
| 1 | El Thread toma el job y anota `iniciadoEn` | `PENDIENTE` → `EN_PROGRESO` |
| 2 | Ejecuta `Replicador.replicar()` | `EN_PROGRESO` |
| 3a | Terminó bien: anota `finalizadoEn` | `COMPLETADO` |
| 3b | Falló: anota `finalizadoEn` y `motivoFallo` | `FALLIDO` |

- Un job solo puede avanzar en ese orden; cualquier otro cambio lanza `IllegalStateException`.
- **Un fallo no tumba al Thread:** el procesador atrapa todo, así el siguiente job se procesa
  normalmente. Tampoco afecta a la instancia MySQL: la replicación solo lee de ella.
- **El motivo no revela detalles internos.** Si el `Replicador` lanza `ReplicacionException`, su
  mensaje es el motivo (debe ser legible y sin URLs, usuarios ni SQL). Cualquier otra excepción deja
  el motivo genérico "Error inesperado durante la replicación." y el detalle solo va al log.
- Un solo Thread: los jobs se procesan uno tras otro, nunca dos a la vez.
- El `Replicador` es `ReplicadorResumen` (HU-55 y HU-56, ver más abajo). Los cambios de estado se
  ven en el log del servicio y con `GET /api/sync/jobs/{jobId}` (HU-57).

- Los jobs viven en memoria: se pierden al reiniciar el servicio.

Estructura:

```
sync-service/
├── openapi/            Contrato YAML (contract-first)
├── src/                Código Spring Boot
├── pom.xml
└── Dockerfile          (se agrega al armar el docker-compose)
```

## Replicación (HU-55 y HU-56)

`ReplicadorResumen` implementa la interfaz `Replicador`. Por cada tipo de dato (clientes, consumos
y pagos):

1. **Lee el origen** (`OrigenMySql`): consulta la instancia MySQL con el usuario de solo lectura.
   Solo trae los campos del resumen; nunca DPI, NIT, teléfono, correo ni documentos del cliente.
   Además, las conexiones se marcan como de solo lectura (`spring.datasource.hikari.read-only`).
2. **Lee las huellas del destino** (`DestinoDynamoDb.hashes`): un `Query` por `entidad`.
3. **Calcula la diferencia** (`Diferencia`): cada registro tiene una huella SHA-256 de sus datos.
   Si el id no está en el destino es **nuevo**; si la huella cambió, **modificado**; si es la misma,
   **igual** y no se escribe.
4. **Aplica los upserts** (`DestinoDynamoDb.upsert`): escribe los registros nuevos y modificados
   con `BatchWriteItem` (`PutRequest`), en lotes de hasta 25. Si DynamoDB deja registros sin
   escribir (`UnprocessedItems`), los reintenta con espera progresiva, hasta 5 intentos por lote.
   No existe ninguna operación para borrar: un registro que desapareció del origen se queda en
   DynamoDB.

Devuelve los conteos (insertados, actualizados y sin cambios por tipo de dato), que el job guarda
para GetJob (HU-57). Ejecutar la replicación dos veces seguidas da el mismo resultado: la segunda
encuentra todo igual y no escribe nada.

Si falla, el job queda `FALLIDO` con un motivo sin detalles internos: *No se pudo leer la
instancia MySQL de origen.*, *No se pudo consultar el destino en DynamoDB.* o *No se pudo escribir
en el destino en DynamoDB.*

### Probarla en local

```powershell
docker compose up -d            # DynamoDB Local, su tabla y la instancia MySQL de origen
cd sync-service
mvn clean package
$env:SYNC_API_KEY="mi-clave-local"
$env:ORIGEN_DB_PASSWORD="cambiar_esta_password"   # la de ORIGEN_DB_PASSWORD en tu .env
java -jar target\sync-service-0.0.1-SNAPSHOT.jar
```

En otra ventana:

```powershell
curl.exe -i -X POST http://localhost:8083/api/sync/resumen -H "X-API-Key: mi-clave-local"
```

En el log debe aparecer, por ejemplo, `CLIENTE: 25 insertados, 0 actualizados, 0 sin cambios` y
el job `COMPLETADO`. Si se repite, todo queda en `sin cambios`.

## Consultar un job (HU-57)

`GET /api/sync/jobs/{jobId}` devuelve el estado del job. Según en qué punto va, agrega
`iniciadoEn` (desde `EN_PROGRESO`), `finalizadoEn`, `registros` y `detalle` (en `COMPLETADO`) o
`motivoFallo` (en `FALLIDO`); los campos que todavía no aplican no vienen en la respuesta.

```powershell
curl.exe -s http://localhost:8083/api/sync/jobs/<jobId> -H "X-API-Key: mi-clave-local"
```

La ruta exacta viene en la cabecera `Location` de la respuesta de Generate. Errores: `400
JOB_ID_INVALIDO` si el jobId no es un UUID y `404 JOB_NO_ENCONTRADO` si no existe (los jobs viven
en memoria: se pierden al reiniciar el servicio).

