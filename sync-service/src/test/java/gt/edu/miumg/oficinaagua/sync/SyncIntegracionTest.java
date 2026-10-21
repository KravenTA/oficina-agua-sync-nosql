package gt.edu.miumg.oficinaagua.sync;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/** Arranca toda la aplicación (sin base de datos ni DynamoDB) para probar que las piezas encajan. */
@SpringBootTest(properties = "sync.api-key=clave-de-prueba")
@AutoConfigureMockMvc
class SyncIntegracionTest {

    private static final String RUTA = "/api/sync/resumen";
    private static final String API_KEY = "clave-de-prueba";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProcesadorJob procesador;

    @Test
    void sinApiKeyResponde401YNoSeCreaNingunJob() throws Exception {
        mvc.perform(post(RUTA))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("NO_AUTORIZADO"));

        verifyNoInteractions(procesador);
    }

    @Test
    void conUnaApiKeyIncorrectaResponde401() throws Exception {
        mvc.perform(post(RUTA).header("X-API-Key", "otra-clave"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(procesador);
    }

    @Test
    void tambienProtegeLaConsultaDeJobs() throws Exception {
        mvc.perform(get("/api/sync/jobs/3f2b8c1e-5d4a-4b7e-9c61-0a1d2e3f4a5b"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void conLaApiKeyCorrectaResponde202YEntregaElJobAlThread() throws Exception {
        mvc.perform(post(RUTA).header("X-API-Key", API_KEY))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.jobId").exists())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        verify(procesador, timeout(2000)).procesar(any(Job.class));
    }

    @Test
    void respondeSinEsperarAQueTermineLaReplicacion() throws Exception {
        CountDownLatch liberar = new CountDownLatch(1);
        doAnswer(invocacion -> {
            liberar.await();
            return null;
        }).when(procesador).procesar(any(Job.class));

        try {
            // El procesador queda bloqueado hasta el final de la prueba; aun así debe llegar el 202.
            assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
                mvc.perform(post(RUTA).header("X-API-Key", API_KEY))
                        .andExpect(status().isAccepted());
            });
            verify(procesador, timeout(2000)).procesar(any(Job.class));
        } finally {
            liberar.countDown();
        }
    }

    @Test
    void elJobCreadoSePuedeConsultarEnLaRutaDeLocation() throws Exception {
        String ruta = mvc.perform(post(RUTA).header("X-API-Key", API_KEY))
                .andExpect(status().isAccepted())
                .andReturn().getResponse().getHeader("Location");

        mvc.perform(get(ruta).header("X-API-Key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").exists());
    }

    @Test
    void consultarUnJobQueNoExisteResponde404() throws Exception {
        mvc.perform(get("/api/sync/jobs/3f2b8c1e-5d4a-4b7e-9c61-0a1d2e3f4a5b").header("X-API-Key", API_KEY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("JOB_NO_ENCONTRADO"));
    }

    @Test
    void elContratoYSwaggerUiNoPidenApiKey() throws Exception {
        mvc.perform(get("/openapi/sync-api.yaml")).andExpect(status().isOk());
    }
}
