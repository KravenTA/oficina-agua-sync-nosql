#!/bin/sh
# HU-55: usuario de solo lectura para el sync-service. Solo tiene permiso SELECT:
# no puede insertar, modificar ni borrar nada en la instancia MySQL de origen.
# La imagen de MySQL lo ejecuta una sola vez, al crear el contenedor. Sin "set -e" ni "exit"
# a propósito: la imagen puede cargar este archivo dentro de su propio script.

mysql --protocol=socket -uroot -p"${MYSQL_ROOT_PASSWORD}" <<SQL
CREATE USER IF NOT EXISTS '${ORIGEN_DB_USER}'@'%' IDENTIFIED BY '${ORIGEN_DB_PASSWORD}';
GRANT SELECT ON \`${MYSQL_DATABASE}\`.* TO '${ORIGEN_DB_USER}'@'%';
FLUSH PRIVILEGES;
SQL

echo "Usuario de solo lectura ${ORIGEN_DB_USER} creado con permiso SELECT sobre ${MYSQL_DATABASE}."
