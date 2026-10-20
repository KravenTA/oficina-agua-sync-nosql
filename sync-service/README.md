# sync-service — Replicación asíncrona (Spring Boot)

Servicio que replica el resumen de la instancia MySQL (solo lectura) hacia Amazon DynamoDB.

| HU | Qué se construye aquí |
|---|---|
| HU-51 | Contrato OpenAPI (`openapi/`), generación de interfaces y DTOs, Swagger UI (implementado) |
| HU-53 | `POST /api/sync/resumen` (Generate) y creación del GenerarJob, respuesta 202 + `jobId`, API key (implementado) |
| HU-54 | Thread en segundo plano y estados del job |
| HU-55 | Diff entre la instancia MySQL y DynamoDB (nuevos, modificados, iguales) |
| HU-56 | Upsert sin borrar en DynamoDB |
| HU-57 | `GET /api/sync/jobs/{jobId}` (GetJob) |

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
`POST /api/sync/resumen` (HU-53): pulsa **Authorize**, escribe la API key y luego **Try it out**.
`GET /api/sync/jobs/{jobId}` responde `501` hasta la HU-57.

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
- Por ahora el Thread usa `ProcesadorJobProvisional`, que solo deja un mensaje en el log y el job
  queda `PENDIENTE`. La HU-54 lo reemplaza por el que cambia los estados.
- `GET /api/sync/jobs/{jobId}` todavía responde `501`; se implementa en la HU-57.
- Los jobs viven en memoria: se pierden al reiniciar el servicio.

Estructura:

```
sync-service/
├── openapi/            Contrato YAML (contract-first)
├── src/                Código Spring Boot
├── pom.xml
└── Dockerfile          (se agrega al armar el docker-compose)
```
