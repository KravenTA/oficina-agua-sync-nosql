# sync-service — Replicación asíncrona (Spring Boot)

Servicio que replica el resumen de la instancia MySQL (solo lectura) hacia Amazon DynamoDB.

| HU | Qué se construye aquí |
|---|---|
| HU-51 | Contrato OpenAPI (`openapi/`), generación de interfaces y DTOs, Swagger UI |
| HU-53 | `POST /api/sync/resumen` (Generate) y creación del GenerarJob, respuesta 202 + `jobId` |
| HU-54 | Thread en segundo plano y estados del job |
| HU-55 | Diff entre la instancia MySQL y DynamoDB (nuevos, modificados, iguales) |
| HU-56 | Upsert sin borrar en DynamoDB |
| HU-57 | `GET /api/sync/jobs/{jobId}` (GetJob) |

Estructura prevista:

```
sync-service/
├── openapi/            Contrato YAML (contract-first)
├── src/                Código Spring Boot
├── pom.xml
└── Dockerfile
```
