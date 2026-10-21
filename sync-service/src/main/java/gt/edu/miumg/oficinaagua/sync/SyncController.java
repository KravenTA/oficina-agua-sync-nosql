package gt.edu.miumg.oficinaagua.sync;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import gt.edu.miumg.oficinaagua.sync.api.SincronizacionApi;
import gt.edu.miumg.oficinaagua.sync.api.model.ConteoRegistros;
import gt.edu.miumg.oficinaagua.sync.api.model.DetalleRegistros;
import gt.edu.miumg.oficinaagua.sync.api.model.EstadoJob;
import gt.edu.miumg.oficinaagua.sync.api.model.JobCreado;
import gt.edu.miumg.oficinaagua.sync.api.model.JobEstado;

/** Implementa la interfaz generada a partir del contrato OpenAPI. */
@RestController
public class SyncController implements SincronizacionApi {

    private final GenerarJob generarJob;
    private final JobRepositorio repositorio;

    public SyncController(GenerarJob generarJob, JobRepositorio repositorio) {
        this.generarJob = generarJob;
        this.repositorio = repositorio;
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

    /**
     * GetJob (HU-57): el estado del job y, según en qué punto va, sus fechas, sus conteos o el
     * motivo del fallo. Los campos que todavía no aplican no vienen en la respuesta.
     */
    @Override
    public ResponseEntity<JobEstado> obtenerEstadoJob(UUID jobId) {
        Job job = repositorio.buscar(jobId).orElseThrow(() -> new JobNoEncontradoException(jobId));

        // El estado se lee primero: el Thread lo cambia al final de cada transición, así que
        // si ya es final, sus fechas, conteos y motivo también están listos.
        EstadoJob estado = job.getEstado();

        JobEstado cuerpo = new JobEstado();
        cuerpo.setJobId(job.getId());
        cuerpo.setEstado(estado);
        cuerpo.setCreadoEn(job.getCreadoEn());
        if (estado != EstadoJob.PENDIENTE) {
            cuerpo.setIniciadoEn(job.getIniciadoEn());
        }
        if (estado == EstadoJob.COMPLETADO) {
            cuerpo.setFinalizadoEn(job.getFinalizadoEn());
            ResultadoReplicacion resultado = job.getResultado();
            if (resultado != null) {
                cuerpo.setRegistros(conteo(resultado.total()));
                DetalleRegistros detalle = new DetalleRegistros();
                detalle.setClientes(conteo(resultado.de(Entidad.CLIENTE)));
                detalle.setConsumos(conteo(resultado.de(Entidad.CONSUMO)));
                detalle.setPagos(conteo(resultado.de(Entidad.PAGO)));
                cuerpo.setDetalle(detalle);
            }
        }
        if (estado == EstadoJob.FALLIDO) {
            cuerpo.setFinalizadoEn(job.getFinalizadoEn());
            cuerpo.setMotivoFallo(job.getMotivoFallo());
        }

        // El estado cambia mientras el job avanza: nadie debe guardar esta respuesta en caché.
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(cuerpo);
    }

    private static ConteoRegistros conteo(Conteo conteo) {
        ConteoRegistros registros = new ConteoRegistros();
        registros.setInsertados(conteo.insertados());
        registros.setActualizados(conteo.actualizados());
        registros.setSinCambios(conteo.sinCambios());
        return registros;
    }
}
