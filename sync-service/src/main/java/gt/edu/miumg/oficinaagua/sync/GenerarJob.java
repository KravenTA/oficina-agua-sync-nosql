package gt.edu.miumg.oficinaagua.sync;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * GenerarJob: registra el job como PENDIENTE y lo entrega al Thread de segundo plano.
 * Vuelve de inmediato; no espera a que la replicación termine.
 */
@Service
public class GenerarJob {

    private static final Logger log = LoggerFactory.getLogger(GenerarJob.class);

    private final JobRepositorio repositorio;
    private final ProcesadorJob procesador;
    private final ExecutorService hilo;
    private final Clock reloj;

    public GenerarJob(JobRepositorio repositorio, ProcesadorJob procesador,
                      @Qualifier("hiloSync") ExecutorService hilo, Clock reloj) {
        this.repositorio = repositorio;
        this.procesador = procesador;
        this.hilo = hilo;
        this.reloj = reloj;
    }

    public Job generar() {
        Job job = new Job(UUID.randomUUID(), OffsetDateTime.now(reloj));
        repositorio.guardar(job);
        hilo.execute(() -> entregar(job));
        log.info("Job {} registrado como PENDIENTE y entregado al Thread", job.getId());
        return job;
    }

    private void entregar(Job job) {
        try {
            procesador.procesar(job);
        } catch (RuntimeException ex) {
            // La HU-54 marca el job como FALLIDO; aquí solo se evita que el error se pierda.
            log.error("El procesamiento del job {} terminó con un error inesperado", job.getId(), ex);
        }
    }
}
