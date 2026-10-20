package gt.edu.miumg.oficinaagua.sync;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import gt.edu.miumg.oficinaagua.sync.api.model.EstadoJob;

class ProcesadorReplicacionTest {

    private static final OffsetDateTime AHORA = OffsetDateTime.parse("2026-10-20T10:00:00Z");

    private final Clock reloj = Clock.fixed(Instant.parse("2026-10-20T10:00:00Z"), ZoneOffset.UTC);

    private Job nuevoJob() {
        return new Job(UUID.randomUUID(), OffsetDateTime.parse("2026-10-20T09:59:00Z"));
    }

    private ProcesadorReplicacion con(Replicador replicador) {
        return new ProcesadorReplicacion(replicador, reloj);
    }

    @Test
    void unaReplicacionExitosaDejaElJobCompletado() {
        Job job = nuevoJob();
        AtomicReference<EstadoJob> estadoDuranteLaReplicacion = new AtomicReference<>();
        Replicador replicador = () -> estadoDuranteLaReplicacion.set(job.getEstado());

        con(replicador).procesar(job);

        assertThat(estadoDuranteLaReplicacion.get()).isEqualTo(EstadoJob.EN_PROGRESO);
        assertThat(job.getEstado()).isEqualTo(EstadoJob.COMPLETADO);
        assertThat(job.getIniciadoEn()).isEqualTo(AHORA);
        assertThat(job.getFinalizadoEn()).isEqualTo(AHORA);
        assertThat(job.getMotivoFallo()).isNull();
    }

    @Test
    void unFalloEsperadoDejaElJobFallidoConSuMotivo() {
        Job job = nuevoJob();
        Replicador replicador = () -> {
            throw new ReplicacionException("No se pudo conectar con DynamoDB.");
        };

        con(replicador).procesar(job);

        assertThat(job.getEstado()).isEqualTo(EstadoJob.FALLIDO);
        assertThat(job.getMotivoFallo()).isEqualTo("No se pudo conectar con DynamoDB.");
        assertThat(job.getIniciadoEn()).isEqualTo(AHORA);
        assertThat(job.getFinalizadoEn()).isEqualTo(AHORA);
    }

    @Test
    void unErrorInesperadoDejaElJobFallidoSinFiltrarDetallesInternos() {
        Job job = nuevoJob();
        Replicador replicador = () -> {
            throw new IOException("jdbc:mysql://servidor-interno:3306/oficina_agua usuario=sync_ro clave=abc123");
        };

        con(replicador).procesar(job);

        assertThat(job.getEstado()).isEqualTo(EstadoJob.FALLIDO);
        assertThat(job.getMotivoFallo()).isEqualTo(ProcesadorReplicacion.MOTIVO_INESPERADO);
        assertThat(job.getMotivoFallo()).doesNotContain("jdbc").doesNotContain("abc123");
    }

    @Test
    void unaExcepcionNoControladaTambienDejaElJobFallido() {
        Job job = nuevoJob();
        Replicador replicador = () -> {
            throw new NullPointerException("detalle interno");
        };

        con(replicador).procesar(job);

        assertThat(job.getEstado()).isEqualTo(EstadoJob.FALLIDO);
        assertThat(job.getMotivoFallo()).isEqualTo(ProcesadorReplicacion.MOTIVO_INESPERADO);
    }

    @Test
    void elProcesadorNuncaPropagaLaExcepcionDeLaReplicacion() {
        Replicador replicador = () -> {
            throw new IllegalStateException("fallo");
        };

        assertThatCode(() -> con(replicador).procesar(nuevoJob())).doesNotThrowAnyException();
    }

    @Test
    void siElHiloEsInterrumpidoElJobQuedaFallidoYSeConservaLaInterrupcion() {
        Job job = nuevoJob();
        Replicador replicador = () -> {
            throw new InterruptedException();
        };

        con(replicador).procesar(job);

        assertThat(job.getEstado()).isEqualTo(EstadoJob.FALLIDO);
        assertThat(job.getMotivoFallo()).isEqualTo(ProcesadorReplicacion.MOTIVO_INTERRUMPIDO);
        // Thread.interrupted() devuelve la señal y la limpia, para no afectar a otras pruebas.
        assertThat(Thread.interrupted()).isTrue();
    }
}
