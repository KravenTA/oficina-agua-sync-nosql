package gt.edu.miumg.oficinaagua.sync;

import java.time.OffsetDateTime;
import java.util.UUID;

import gt.edu.miumg.oficinaagua.sync.api.model.EstadoJob;

/**
 * Un trabajo de replicación. Nace PENDIENTE; el Thread de segundo plano cambiará su estado
 * (HU-54), por eso el estado es volatile: lo escribe un hilo y lo lee el de la petición.
 */
public class Job {

    private final UUID id;
    private final OffsetDateTime creadoEn;
    private volatile EstadoJob estado = EstadoJob.PENDIENTE;

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

    public void setEstado(EstadoJob estado) {
        this.estado = estado;
    }
}
