# db/origen/ — Instancia MySQL de origen

Scripts que carga el contenedor `mysql-origen` la primera vez que se crea (HU-55). La imagen de
MySQL los ejecuta en orden alfabético:

| Archivo | Qué hace |
|---|---|
| `01-esquema-y-datos.sql` | Estructura y datos del monolito. Se generó ejecutando sus migraciones y seeders (`php spark migrate` y `php spark db:seed DatabaseSeeder`) y exportando con `mysqldump`. Las contraseñas de los usuarios se reemplazaron por `no-aplica`. |
| `02-datos-ejemplo-recibos-pagos.sql` | Recibos y pagos de ejemplo: los seeders del monolito no crean ninguno y sin ellos no se podría probar la replicación de pagos. Solo existen en el entorno local. |
| `03-usuario-solo-lectura.sh` | Crea el usuario `ORIGEN_DB_USER` con permiso **SELECT únicamente**. |

El monolito original no se toca: aquí solo hay una copia de sus datos para el entorno local.

## Probarlo

```powershell
docker compose up -d mysql-origen
docker compose logs mysql-origen
```

Debe aparecer `Usuario de solo lectura sync_ro creado con permiso SELECT`. Para comprobar que el
usuario no puede escribir:

```powershell
docker exec -it sync-mysql-origen mysql -usync_ro -p oficina_agua -e "INSERT INTO clientes (nombre) VALUES ('prueba');"
```

Debe responder `INSERT command denied`.

Los scripts solo corren cuando el volumen está vacío. Para volver a cargarlos:

```powershell
docker compose down -v
docker compose up -d mysql-origen
```
