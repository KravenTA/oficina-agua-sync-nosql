# db/origen/ — Instancia MySQL de origen

Scripts de inicialización de la instancia MySQL de la que lee el sync-service (HU-55):

- Carga de los datos de la Oficina del Agua.
- Usuario de **solo lectura** (permiso SELECT únicamente) para el sync-service.

El monolito original no se toca: aquí solo hay una copia de sus datos para el entorno local.
