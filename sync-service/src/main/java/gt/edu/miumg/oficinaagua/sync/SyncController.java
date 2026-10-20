package gt.edu.miumg.oficinaagua.sync;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import gt.edu.miumg.oficinaagua.sync.api.SincronizacionApi;
import gt.edu.miumg.oficinaagua.sync.api.model.EstadoJob;
import gt.edu.miumg.oficinaagua.sync.api.model.JobCreado;
import gt.edu.miumg.oficinaagua.sync.api.model.JobEstado;

/** Implementa la interfaz generada a partir del contrato OpenAPI. */
@RestController
public class SyncController implements SincronizacionApi {

    private final GenerarJob generarJob;

    public SyncController(GenerarJob generarJob) {
        this.generarJob = generarJob;
    }

    /** Generate: registra el job y responde 202 de inmediato, sin esperar la replicación. */
    @Override
    public ResponseEntity<JobCreado> iniciarSincronizacion() {
        Job job = generarJob.generar();

        JobCreado cuerpo = new JobCreado();
        cuerpo.setJobId(job.getId());
        // Un job recién creado siempre es PENDIENTE; no se lee del job porque el Thread
        // podría haberlo cambiado ya.
        cuerpo.setEstado(EstadoJob.PENDIENTE);
        cuerpo.setCreadoEn(job.getCreadoEn());

        return ResponseEntity.accepted()
                .cacheControl(CacheControl.noStore())
                .location(URI.create("/api/sync/jobs/" + job.getId()))
                .body(cuerpo);
    }

    /** Se implementa en la HU-57. */
    @Override
    public ResponseEntity<JobEstado> obtenerEstadoJob(UUID jobId) {
        throw new UnsupportedOperationException("GetJob se implementa en la HU-57");
    }
}
