package gt.edu.miumg.oficinaagua.sync;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class JobRepositorioTest {

    private final JobRepositorio repositorio = new JobRepositorio();

    @Test
    void encuentraUnJobGuardado() {
        Job job = new Job(UUID.randomUUID(), OffsetDateTime.parse("2026-10-19T21:40:00Z"));
        repositorio.guardar(job);

        assertThat(repositorio.buscar(job.getId())).containsSame(job);
    }

    @Test
    void devuelveVacioSiElJobNoExiste() {
        assertThat(repositorio.buscar(UUID.randomUUID())).isEmpty();
    }
}
