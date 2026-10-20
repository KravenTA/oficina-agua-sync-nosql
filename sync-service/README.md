# sync-service — Replicación asíncrona (Spring Boot)

Servicio que replica el resumen de la instancia MySQL (solo lectura) hacia Amazon DynamoDB.

| HU | Qué se construye aquí |
|---|---|
| HU-51 | Contrato OpenAPI (`openapi/`), generación de interfaces y DTOs, Swagger UI (implementado) |
| HU-53 | `POST /api/sync/resumen` (Generate) y creación del GenerarJob, respuesta 202 + `jobId` |
| HU-54 | Thread en segundo plano y estados del job |
| HU-55 | Diff entre la instancia MySQL y DynamoDB (nuevos, modificados, iguales) |
| HU-56 | Upsert sin borrar en DynamoDB |
| HU-57 | `GET /api/sync/jobs/{jobId}` (GetJob) |

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
`http://localhost:8083/openapi/sync-api.yaml`. Todavía no hay controladores: Swagger UI muestra
el contrato, pero probar los endpoints desde ahí dará 404 hasta las HU-53 y HU-57.

Estructura:

```
sync-service/
├── openapi/            Contrato YAML (contract-first)
├── src/                Código Spring Boot
├── pom.xml
└── Dockerfile          (se agrega al armar el docker-compose)
```
