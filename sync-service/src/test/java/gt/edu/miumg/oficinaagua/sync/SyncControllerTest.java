package gt.edu.miumg.oficinaagua.sync;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SyncController.class)
class SyncControllerTest {

    private static final String RUTA = "/api/sync/resumen";
    private static final UUID JOB_ID = UUID.fromString("3f2b8c1e-5d4a-4b7e-9c61-0a1d2e3f4a5b");

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GenerarJob generarJob;

    @Test
    void respondeAcceptedConElJobIdYLaRutaParaConsultarlo() throws Exception {
        when(generarJob.generar()).thenReturn(new Job(JOB_ID, OffsetDateTime.parse("2026-10-19T21:40:00Z")));

        mvc.perform(post(RUTA))
                .andExpect(status().isAccepted())
                .andExpect(header().string("Location", "/api/sync/jobs/" + JOB_ID))
                .andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(jsonPath("$.jobId").value(JOB_ID.toString()))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.creadoEn").exists());
    }

    @Test
    void unErrorInesperadoSeResponde500SinDetallesInternos() throws Exception {
        when(generarJob.generar()).thenThrow(new IllegalStateException("clave-secreta-interna"));

        mvc.perform(post(RUTA))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.codigo").value("ERROR_INTERNO"))
                .andExpect(content().string(not(containsString("clave-secreta-interna"))));
    }
}
