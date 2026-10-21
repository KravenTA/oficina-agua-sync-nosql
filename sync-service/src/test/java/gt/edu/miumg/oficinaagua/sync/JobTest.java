package gt.edu.miumg.oficinaagua.sync;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import gt.edu.miumg.oficinaagua.sync.api.model.EstadoJob;

class JobTest {

    private static final OffsetDateTime CREADO = OffsetDateTime.parse("2026-10-20T10:00:00Z");
    private static final OffsetDateTime INICIO = OffsetDateTime.parse("2026-10-20T10:00:01Z");
    private static final OffsetDateTime FIN = OffsetDateTime.parse("2026-10-20T10:00:04Z");

    private Job nuevo() {
        return new Job(UUID.randomUUID(), CREADO);
    }

    @Test
    void nacePendienteYSinFechasDeEjecucion() {
        Job job = nuevo();

        assertThat(job.getEstado()).isEqualTo(EstadoJob.PENDIENTE);
        assertThat(job.getCreadoEn()).isEqualTo(CREADO);
        assertThat(job.getIniciadoEn()).isNull();
        assertThat(job.getFinalizadoEn()).isNull();
        assertThat(job.getMotivoFallo()).isNull();
    }

    @Test
    void iniciarLoPasaAEnProgresoYGuardaCuandoEmpezo() {
        Job job = nuevo();

        job.iniciar(INICIO);

        assertThat(job.getEstado()).isEqualTo(EstadoJob.EN_PROGRESO);
        assertThat(job.getIniciadoEn()).isEqualTo(INICIO);
        assertThat(job.getFinalizadoEn()).isNull();
    }

    @Test
    void completarLoPasaACompletadoYGuardaCuandoTermino() {
        Job job = nuevo();
        job.iniciar(INICIO);

        job.completar(FIN);

        assertThat(job.getEstado()).isEqualTo(EstadoJob.COMPLETADO);
        assertThat(job.getFinalizadoEn()).isEqualTo(FIN);
        assertThat(job.getMotivoFallo()).isNull();
    }

    @Test
    void fallarLoPasaAFallidoConSuMotivo() {
        Job job = nuevo();
        job.iniciar(INICIO);

        job.fallar(FIN, "No se pudo conectar con DynamoDB.");

        assertThat(job.getEstado()).isEqualTo(EstadoJob.FALLIDO);
        assertThat(job.getFinalizadoEn()).isEqualTo(FIN);
        assertThat(job.getMotivoFallo()).isEqualTo("No se pudo conectar con DynamoDB.");
    }

    @Test
    void noSePuedeCompletarUnJobQueNoEmpezo() {
        Job job = nuevo();

        assertThatThrownBy(() -> job.completar(FIN)).isInstanceOf(IllegalStateException.class);
        assertThat(job.getEstado()).isEqualTo(EstadoJob.PENDIENTE);
    }

    @Test
    void noSePuedeMarcarFallidoUnJobQueNoEmpezo() {
        Job job = nuevo();

        assertThatThrownBy(() -> job.fallar(FIN, "x")).isInstanceOf(IllegalStateException.class);
        assertThat(job.getEstado()).isEqualTo(EstadoJob.PENDIENTE);
    }

    @Test
    void noSePuedeIniciarDosVeces() {
        Job job = nuevo();
        job.iniciar(INICIO);

        assertThatThrownBy(() -> job.iniciar(FIN)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void unJobTerminadoNoCambiaDeEstado() {
        Job completado = nuevo();
        completado.iniciar(INICIO);
        completado.completar(FIN);
        Job fallido = nuevo();
        fallido.iniciar(INICIO);
        fallido.fallar(FIN, "motivo");

        assertThatThrownBy(() -> completado.fallar(FIN, "x")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> completado.completar(FIN)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> fallido.completar(FIN)).isInstanceOf(IllegalStateException.class);
        assertThat(completado.getEstado()).isEqualTo(EstadoJob.COMPLETADO);
        assertThat(fallido.getEstado()).isEqualTo(EstadoJob.FALLIDO);
    }
}
