package gt.edu.miumg.oficinaagua.sync;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Clock;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import gt.edu.miumg.oficinaagua.sync.api.model.EstadoJob;

/**
 * El recorrido completo con un Thread de verdad: GenerarJob registra el job, el Thread lo toma y
 * lo lleva de PENDIENTE a su estado final. Sin Spring: rápido y sin dependencias externas.
 */
class FlujoJobTest {

    private final Clock reloj = Clock.systemUTC();
    private final JobRepositorio repositorio = new JobRepositorio();
    private final ExecutorService hilo = Executors.newSingleThreadExecutor();

    @AfterEach
    void cerrarHilo() {
        hilo.shutdownNow();
    }

    private GenerarJob generarCon(Replicador replicador) {
        return new GenerarJob(repositorio, new ProcesadorReplicacion(replicador, reloj), hilo, reloj);
    }

    private void esperarEstado(Job job, EstadoJob esperado) {
        await().atMost(3, TimeUnit.SECONDS).until(() -> job.getEstado() == esperado);
    }

    @Test
    void elJobPasaPorEnProgresoYTerminaCompletado() throws Exception {
        CountDownLatch liberar = new CountDownLatch(1);
        GenerarJob generarJob = generarCon(() -> {
            liberar.await();
            return ResultadoReplicacion.VACIO;
        });

        Job job = generarJob.generar();

        esperarEstado(job, EstadoJob.EN_PROGRESO);
        assertThat(job.getIniciadoEn()).isNotNull();
        assertThat(job.getFinalizadoEn()).isNull();

        liberar.countDown();

        esperarEstado(job, EstadoJob.COMPLETADO);
        assertThat(job.getFinalizadoEn()).isNotNull();
        assertThat(job.getMotivoFallo()).isNull();
    }

    @Test
    void siLaReplicacionFallaElJobQuedaFallidoConSuMotivo() {
        GenerarJob generarJob = generarCon(() -> {
            throw new ReplicacionException("No se pudo conectar con DynamoDB.");
        });

        Job job = generarJob.generar();

        esperarEstado(job, EstadoJob.FALLIDO);
        assertThat(job.getMotivoFallo()).isEqualTo("No se pudo conectar con DynamoDB.");
        assertThat(job.getFinalizadoEn()).isNotNull();
    }

    @Test
    void unJobFallidoNoTumbaAlThreadYElSiguienteSeProcesa() {
        AtomicInteger llamadas = new AtomicInteger();
        GenerarJob generarJob = generarCon(() -> {
            if (llamadas.incrementAndGet() == 1) {
                throw new IllegalStateException("fallo simulado");
            }
            return ResultadoReplicacion.VACIO;
        });

        Job primero = generarJob.generar();
        Job segundo = generarJob.generar();

        esperarEstado(primero, EstadoJob.FALLIDO);
        esperarEstado(segundo, EstadoJob.COMPLETADO);
    }

    @Test
    void losJobsSeProcesanUnoTrasOtroNuncaDosALaVez() throws Exception {
        CountDownLatch liberarPrimero = new CountDownLatch(1);
        AtomicInteger llamadas = new AtomicInteger();
        GenerarJob generarJob = generarCon(() -> {
            if (llamadas.incrementAndGet() == 1) {
                liberarPrimero.await();
            }
            return ResultadoReplicacion.VACIO;
        });

        Job primero = generarJob.generar();
        Job segundo = generarJob.generar();

        esperarEstado(primero, EstadoJob.EN_PROGRESO);
        // Mientras el primero sigue en curso, el segundo espera su turno.
        assertThat(segundo.getEstado()).isEqualTo(EstadoJob.PENDIENTE);

        liberarPrimero.countDown();

        esperarEstado(primero, EstadoJob.COMPLETADO);
        esperarEstado(segundo, EstadoJob.COMPLETADO);
    }
}
