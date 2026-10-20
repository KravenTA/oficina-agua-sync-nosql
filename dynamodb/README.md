# dynamodb/ — Amazon DynamoDB Local

Destino de la replicación (HU-52). `docker compose up` levanta dos servicios:

| Servicio | Qué hace |
|---|---|
| `dynamodb` | Amazon DynamoDB Local en el puerto `DYNAMODB_HOST_PORT` (8000 por defecto). Guarda sus datos en el volumen `dynamodb-data`, así que sobreviven a un reinicio. |
| `dynamodb-init` | Espera a que DynamoDB responda, crea la tabla de resumen con [`crear-tabla.sh`](crear-tabla.sh) y termina. Si la tabla ya existe, no hace nada. |

## Tabla de resumen

Nombre: `DYNAMODB_TABLE_RESUMEN` (`oficina_agua_resumen` por defecto). Una sola tabla guarda los tres
tipos de dato que se replican.

| Clave | Atributo | Tipo | Ejemplo |
|---|---|---|---|
| Partición (HASH) | `entidad` | S | `CLIENTE`, `CONSUMO` o `PAGO` |
| Ordenamiento (RANGE) | `id` | S | el id del registro en MySQL, como texto |

- `CLIENTE` sale de la tabla `clientes`, `CONSUMO` de `lecturas` y `PAGO` de `pagos`.
- Con esta clave, traer todos los registros de un tipo es un solo `Query` por `entidad`, que es lo
  que necesita el diff de la HU-55. Un upsert (HU-56) es un `PutItem` con la misma clave.
- Los demás atributos (los datos del registro, un `hash` para detectar cambios y la fecha de
  replicación) no se declaran: DynamoDB no los exige. Se definen cuando se implementen las HU-55 y 56.
- Modo de facturación `PAY_PER_REQUEST`, sin capacidad que configurar.

## Probarlo

```powershell
docker compose up -d
docker compose logs dynamodb-init
```

Debe terminar con `Tabla oficina_agua_resumen creada.` Para verla desde tu máquina, con la
AWS CLI instalada:

```powershell
aws dynamodb list-tables --endpoint-url http://localhost:8000 --region us-east-1
```

(Con DynamoDB Local las credenciales pueden ser cualquiera; si la CLI las pide, usa `local`.)

Sin la AWS CLI, se puede usar el mismo contenedor:

```powershell
docker compose run --rm --entrypoint aws dynamodb-init dynamodb list-tables --endpoint-url http://dynamodb:8000
```

Para empezar de cero (borra los datos replicados):

```powershell
docker compose down -v
```

## Conexión

| Desde dónde | Endpoint |
|---|---|
| Otro contenedor del compose (el `sync-service`) | `http://dynamodb:8000` |
| Tu máquina (IntelliJ, `aws` CLI) | `http://localhost:8000` |

Las credenciales y la región de DynamoDB Local no son reales de AWS; en `.env.example` valen `local`.

## Alternativa: MongoDB

La HU-52 permite MongoDB como destino. **Este proyecto usa DynamoDB**; esto queda documentado por
si se cambia. Con MongoDB el servicio equivalente sería:

```yaml
  mongodb:
    image: mongo:7
    ports: ["27017:27017"]
    volumes: ["mongodb-data:/data/db"]
```

y el modelo se traduce así: colección `resumen`, índice único sobre `{ entidad: 1, id: 1 }` y el
upsert con `replaceOne({ entidad, id }, documento, { upsert: true })`. El resto del servicio
(contrato, jobs, diff) no cambia; solo la clase que escribe en el destino.
