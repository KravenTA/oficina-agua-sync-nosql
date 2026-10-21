# Oficina del Agua — Feature 4: Replicación Asíncrona a NoSQL

Fase 2 · Desarrollo Web 2026 · Universidad Mariano Gálvez

Servicio que copia un **resumen** de la Oficina del Agua (clientes, consumos y pagos) desde la
instancia MySQL hacia **Amazon DynamoDB**, sin hacer esperar a quien lo pide. `POST /api/sync/resumen`
responde de inmediato con `202 Accepted` y un `jobId`; la replicación corre en segundo plano y
`GET /api/sync/jobs/{jobId}` informa en qué estado va. La replicación calcula la diferencia y aplica
**solo upserts: nunca borra** registros del destino.

> **Este repositorio es independiente del monolito.** El monolito
> ([proyecto-agua-keko](https://github.com/KravenTA/proyecto-agua-keko)) **no se toca**:
> aquí solo se construye lo nuevo, al lado. El servicio lee de la instancia MySQL únicamente con un
> usuario de **solo lectura**.

## Equipo

| Nombre completo | Carnet | Correo electrónico | Rol dentro del equipo |
|---|---|---|---|
| Yourgen Kraven Thommel Arevalo | 0905-23-14003 | ythommela@miumg.edu.gt | Coordinador |
| Karen Yamileth Jiménez Galicia | 0905-23-7626 | kjimenezg6@miumg.edu.gt | Desarrolladora |
| Enner Osvaldo Godoy Ramirez | 0905-23-15908 | egodoyr2@miumg.edu.gt | Desarrollador |
| Oliver Isaac Godoy Salguero | 0905-23-10816 | ogodoys@miumg.edu.gt | Desarrollador |

**Responsable de la feature:** Yourgen Kraven Thommel Arevalo
**Proyecto en Jira:** SDGODA · Épica 14 — Feature 4

## Arquitectura

```
 Administrador
     │ POST /api/sync/resumen                      GET /api/sync/jobs/{jobId}
     ▼                                                       │
  Generate ──► GenerarJob ──► Thread (segundo plano)         ▼
  (202 + jobId)  (PENDIENTE)      │                       GetJob
                                  │ lee (solo lectura)       ▲ estado, fechas y conteos
                                  ▼                          │
                         Instancia MySQL ──► diff ──► upsert ──► Amazon DynamoDB
                                           (nuevos / modificados / iguales)
```

Estados del job: `PENDIENTE` → `EN_PROGRESO` → `COMPLETADO` o `FALLIDO`.

El diagrama completo de la Fase 2 está en [`docs/arquitectura-fase2.png`](docs/arquitectura-fase2.png)
(esta feature corresponde al bloque **4 — Replicación asíncrona a NoSQL**).

## Estructura del repositorio

```
.
├── sync-service/         Servicio Spring Boot: Generate, GenerarJob, Thread, GetJob — HU-51, 53 a 57
├── db/origen/            Instancia MySQL de origen: usuario de solo lectura y datos — HU-55
├── dynamodb/             Amazon DynamoDB Local y creación de la tabla de resumen — HU-52
├── docs/                 Diagramas y evidencia de pruebas — HU-58
├── .github/              Plantilla de pull request
├── docker-compose.yml    Orquestación de la feature
├── .env.example          Variables de entorno (copiar a .env)
└── README.md
```

## Cómo levantarla

> **Estado:** el `docker compose up` levanta el destino DynamoDB Local con su tabla de resumen
> (HU-52) y la instancia MySQL de origen con su usuario de solo lectura (HU-55). El sync-service
> se ejecuta aparte; ver [`sync-service/README.md`](sync-service/README.md).

Requisitos: Docker y Docker Compose, Git.

```bash
git clone https://github.com/KravenTA/oficina-agua-sync-nosql.git
cd oficina-agua-sync-nosql
cp .env.example .env        # ajustar valores (nunca subir el .env)
docker compose up --build   # levanta DynamoDB Local y crea la tabla (HU-52)
```

Swagger UI del sync-service (disponible desde HU-51): `http://localhost:<SYNC_PORT>/swagger-ui.html`

## Flujo de trabajo del equipo

Se trabaja con ramas y pull requests. **Nunca directo sobre `main`.**

```bash
git checkout main
git pull
git checkout -b feature/SDGODA-XX-descripcion-corta
```

Prefijos de commit: `feat:` funcionalidad nueva · `fix:` correcciones ·
`chore:` configuración y mantenimiento · `refactor:` reorganizar sin cambiar
comportamiento · `style:` formato · `docs:` documentación.

Antes de abrir el PR, traer `main` a la rama para resolver conflictos:

```bash
git fetch origin
git merge origin/main
```

## Reglas de esta fase

- El monolito heredado no se toca, ni para agregar un endpoint de lectura.
- El sync-service solo lee de la instancia MySQL, con un usuario de solo lectura.
- La replicación solo aplica upserts: ningún registro existente en DynamoDB se elimina.
- Contract-first: primero el YAML de OpenAPI, después el código.
- Claves y credenciales solo en variables de entorno, documentadas en `.env.example`.
