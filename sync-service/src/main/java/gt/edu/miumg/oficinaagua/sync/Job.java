package gt.edu.miumg.oficinaagua.sync;

import java.time.OffsetDateTime;
import java.util.UUID;

import gt.edu.miumg.oficinaagua.sync.api.model.EstadoJob;

/**
 * Un trabajo de replicación. Nace PENDIENTE y solo puede avanzar así:
 * PENDIENTE → EN_PROGRESO → COMPLETADO o FALLIDO. Cualquier otro cambio es un error de programación
 * y lanza IllegalStateException.
 *
 * Lo escribe el Thread de segundo plano y lo lee el hilo de la petición (GetJob, HU-57). Por eso
 * los campos son volatile, y en cada transición el estado se cambia al final: quien lee un estado
 * final ya ve también sus fechas y su motivo.
 */
public class Job {

    private final UUID id;
    private final OffsetDateTime creadoEn;
    private volatile EstadoJob estado = EstadoJob.PENDIENTE;
    private volatile OffsetDateTime iniciadoEn;
    private volatile OffsetDateTime finalizadoEn;
    private volatile String motivoFallo;

    public Job(UUID id, OffsetDateTime creadoEn) {
        this.id = id;
        this.creadoEn = creadoEn;
    }

    public UUID getId() {
        return id;
    }

    public OffsetDateTime getCreadoEn() {
        return creadoEn;
    }

    public EstadoJob getEstado() {
        return estado;
    }

    /** Cuándo el Thread empezó a replicar; null mientras esté PENDIENTE. */
    public OffsetDateTime getIniciadoEn() {
        return iniciadoEn;
    }

    /** Cuándo terminó, con éxito o con error; null mientras no haya terminado. */
    public OffsetDateTime getFinalizadoEn() {
        return finalizadoEn;
    }

    /** Por qué falló; null si no falló. Nunca trae detalles técnicos internos. */
    public String getMotivoFallo() {
        return motivoFallo;
    }

    /** PENDIENTE → EN_PROGRESO. */
    public synchronized void iniciar(OffsetDateTime instante) {
        exigirEstado(EstadoJob.PENDIENTE, "iniciar");
        this.iniciadoEn = instante;
        this.estado = EstadoJob.EN_PROGRESO;
    }

    /** EN_PROGRESO → COMPLETADO. */
    public synchronized void completar(OffsetDateTime instante) {
        exigirEstado(EstadoJob.EN_PROGRESO, "completar");
        this.finalizadoEn = instante;
        this.estado = EstadoJob.COMPLETADO;
    }

    /** EN_PROGRESO → FALLIDO, con el motivo que verá quien consulte el job. */
    public synchronized void fallar(OffsetDateTime instante, String motivo) {
        exigirEstado(EstadoJob.EN_PROGRESO, "marcar como fallido");
        this.motivoFallo = motivo;
        this.finalizadoEn = instante;
        this.estado = EstadoJob.FALLIDO;
    }

    private void exigirEstado(EstadoJob esperado, String accion) {
        if (estado != esperado) {
            throw new IllegalStateException(
                    "No se puede " + accion + " un job en estado " + estado + " (se esperaba " + esperado + ")");
        }
    }
}
