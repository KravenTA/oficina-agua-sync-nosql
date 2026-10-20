package gt.edu.miumg.oficinaagua.sync;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import gt.edu.miumg.oficinaagua.sync.api.model.EstadoJob;

class GenerarJobTest {

    private final Clock reloj = Clock.fixed(Instant.parse("2026-10-19T21:40:00Z"), ZoneOffset.UTC);
    private final JobRepositorio repositorio = new JobRepositorio();
    private final ExecutorService hilo = Executors.newSingleThreadExecutor();

    @AfterEach
    void cerrarHilo() {
        hilo.shutdownNow();
    }

    @Test
    void registraElJobComoPendienteYLoGuarda() {
        GenerarJob generarJob = new GenerarJob(repositorio, job -> { }, hilo, reloj);

        Job job = generarJob.generar();

        assertThat(job.getEstado()).isEqualTo(EstadoJob.PENDIENTE);
        assertThat(job.getCreadoEn()).isEqualTo(OffsetDateTime.parse("2026-10-19T21:40:00Z"));
        assertThat(repositorio.buscar(job.getId())).containsSame(job);
    }

    @Test
    void entregaElJobAlThreadDeSegundoPlano() throws Exception {
        CountDownLatch recibido = new CountDownLatch(1);
        AtomicReference<Job> entregado = new AtomicReference<>();
        AtomicReference<String> hiloQueLoProceso = new AtomicReference<>();
        ProcesadorJob procesador = job -> {
            entregado.set(job);
            hiloQueLoProceso.set(Thread.currentThread().getName());
            recibido.countDown();
        };
        GenerarJob generarJob = new GenerarJob(repositorio, procesador, hilo, reloj);

        Job job = generarJob.generar();

        assertThat(recibido.await(2, TimeUnit.SECONDS)).isTrue();
        assertThat(entregado.get()).isSameAs(job);
        assertThat(hiloQueLoProceso.get()).isNotEqualTo(Thread.currentThread().getName());
    }

    @Test
    void noEsperaAQueTermineLaReplicacion() {
        CountDownLatch liberar = new CountDownLatch(1);
        ProcesadorJob lento = job -> {
            try {
                liberar.await();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        };
        GenerarJob generarJob = new GenerarJob(repositorio, lento, hilo, reloj);

        try {
            // Si generar() esperara al procesador, esto se pasaría del límite y la prueba fallaría.
            assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
                generarJob.generar();
            });
        } finally {
            liberar.countDown();
        }
    }

    @Test
    void cadaLlamadaCreaUnJobNuevo() {
        GenerarJob generarJob = new GenerarJob(repositorio, job -> { }, hilo, reloj);

        Job primero = generarJob.generar();
        Job segundo = generarJob.generar();

        assertThat(primero.getId()).isNotEqualTo(segundo.getId());
    }
}
