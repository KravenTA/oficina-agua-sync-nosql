#!/bin/sh
# Crea la tabla de resumen en DynamoDB Local si todavía no existe (HU-52).
# Es seguro ejecutarlo muchas veces: si la tabla ya está, no hace nada.
set -eu

ENDPOINT="${DYNAMODB_ENDPOINT:-http://dynamodb:8000}"
TABLA="${DYNAMODB_TABLE_RESUMEN:-oficina_agua_resumen}"

echo "Esperando a DynamoDB Local en ${ENDPOINT} ..."
intentos=0
until aws dynamodb list-tables --endpoint-url "${ENDPOINT}" >/dev/null 2>&1; do
  intentos=$((intentos + 1))
  if [ "${intentos}" -ge 30 ]; then
    echo "ERROR: DynamoDB Local no respondió después de 30 intentos." >&2
    exit 1
  fi
  sleep 2
done

if aws dynamodb describe-table --table-name "${TABLA}" --endpoint-url "${ENDPOINT}" >/dev/null 2>&1; then
  echo "La tabla ${TABLA} ya existe. No se hace nada."
  exit 0
fi

echo "Creando la tabla ${TABLA} ..."
aws dynamodb create-table \
  --table-name "${TABLA}" \
  --attribute-definitions \
      AttributeName=entidad,AttributeType=S \
      AttributeName=id,AttributeType=S \
  --key-schema \
      AttributeName=entidad,KeyType=HASH \
      AttributeName=id,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST \
  --endpoint-url "${ENDPOINT}" >/dev/null

aws dynamodb wait table-exists --table-name "${TABLA}" --endpoint-url "${ENDPOINT}"
echo "Tabla ${TABLA} creada."
