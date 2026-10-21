# Pruebas de integración de la replicación (HU-58)

- Fecha: 2026-10-21 01:16
- Entorno: docker compose (MySQL de origen, DynamoDB Local y sync-service), levantado desde cero
- Origen inicial: 25 clientes, 32 consumos y 4 pagos
- Resultado: **20 de 20 comprobaciones aprobadas**

| Caso | Comprobación | Obtenido | Resultado |
|---|---|---|---|
| 1. Destino vacío | El job termina COMPLETADO | COMPLETADO | Aprobado |
| 1. Destino vacío | Clientes: todos insertados | 25 insertados, 0 actualizados, 0 sin cambios | Aprobado |
| 1. Destino vacío | Consumos: todos insertados | 32 insertados, 0 actualizados, 0 sin cambios | Aprobado |
| 1. Destino vacío | Pagos: todos insertados | 4 insertados, 0 actualizados, 0 sin cambios | Aprobado |
| 1. Destino vacío | DynamoDB tiene un registro por cada registro del origen | 61 de 61 | Aprobado |
| 2. Repetición sin cambios | El job termina COMPLETADO | COMPLETADO | Aprobado |
| 2. Repetición sin cambios | No escribe nada: todo sin cambios | 0 insertados, 0 actualizados, 61 sin cambios | Aprobado |
| 3. Cambios parciales | El job termina COMPLETADO | COMPLETADO | Aprobado |
| 3. Cambios parciales | Clientes: 1 insertado, 1 actualizado | 1 insertados, 1 actualizados, 24 sin cambios | Aprobado |
| 3. Cambios parciales | Consumos: sin cambios | 0 insertados, 0 actualizados, 32 sin cambios | Aprobado |
| 3. Cambios parciales | Pagos: 1 actualizado | 0 insertados, 1 actualizados, 3 sin cambios | Aprobado |
| 3. Cambios parciales | DynamoDB tiene la dirección actualizada del cliente 2 | Aldea El Zapote, Jutiapa (actualizada) | Aprobado |
| 4. Nunca borra | El job termina COMPLETADO | COMPLETADO | Aprobado |
| 4. Nunca borra | Clientes: nada que escribir | 0 insertados, 0 actualizados, 25 sin cambios | Aprobado |
| 4. Nunca borra | El cliente 26, borrado del origen, sigue en DynamoDB | 62 registros en DynamoDB | Aprobado |
| 5. Fallo del destino | Con DynamoDB caído el job queda FALLIDO | FALLIDO | Aprobado |
| 5. Fallo del destino | El motivo es claro y sin detalles internos | No se pudo consultar el destino en DynamoDB. | Aprobado |
| 5. Fallo del destino | MySQL de origen no cambió | 25 clientes, 32 consumos, 4 pagos | Aprobado |
| 5. Fallo del destino | Con DynamoDB de vuelta, la siguiente ejecución se recupera | COMPLETADO: 0 insertados, 0 actualizados, 61 sin cambios | Aprobado |
| 6. Sin datos sensibles | Ningún registro trae DPI, NIT, teléfono, correo ni documentos | ninguno en 62 registros | Aprobado |

Cómo reproducirlo: `powershell -ExecutionPolicy Bypass -File .\pruebas\integracion.ps1` desde la raíz del repositorio.
