package gt.edu.miumg.oficinaagua.sync;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
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

    @MockBean
    private JobRepositorio repositorio;

    private static final OffsetDateTime CREADO = OffsetDateTime.parse("2026-10-19T21:40:00Z");
    private static final OffsetDateTime INICIO = OffsetDateTime.parse("2026-10-19T21:40:01Z");
    private static final OffsetDateTime FIN = OffsetDateTime.parse("2026-10-19T21:40:04Z");

    private Job registrado(Job job) {
        when(repositorio.buscar(JOB_ID)).thenReturn(Optional.of(job));
        return job;
    }

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

    @Test
    void getJobDeUnJobPendienteSoloTraeSuIdEstadoYCreacion() throws Exception {
        registrado(new Job(JOB_ID, CREADO));

        mvc.perform(get("/api/sync/jobs/" + JOB_ID))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(jsonPath("$.jobId").value(JOB_ID.toString()))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.creadoEn").exists())
                .andExpect(jsonPath("$.iniciadoEn").doesNotExist())
                .andExpect(jsonPath("$.registros").doesNotExist());
    }

    @Test
    void getJobDeUnJobEnProgresoAgregaCuandoInicio() throws Exception {
        Job job = registrado(new Job(JOB_ID, CREADO));
        job.iniciar(INICIO);

        mvc.perform(get("/api/sync/jobs/" + JOB_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_PROGRESO"))
                .andExpect(jsonPath("$.iniciadoEn").exists())
                .andExpect(jsonPath("$.finalizadoEn").doesNotExist());
    }

    @Test
    void getJobDeUnJobCompletadoTraeLosConteosYSuDetalle() throws Exception {
        Job job = registrado(new Job(JOB_ID, CREADO));
        job.iniciar(INICIO);
        job.completar(FIN, new ResultadoReplicacion(Map.of(
                Entidad.CLIENTE, new Conteo(2, 1, 23),
                Entidad.CONSUMO, new Conteo(6, 2, 25),
                Entidad.PAGO, new Conteo(4, 0, 0))));

        mvc.perform(get("/api/sync/jobs/" + JOB_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETADO"))
                .andExpect(jsonPath("$.finalizadoEn").exists())
                .andExpect(jsonPath("$.registros.insertados").value(12))
                .andExpect(jsonPath("$.registros.actualizados").value(3))
                .andExpect(jsonPath("$.registros.sinCambios").value(48))
                .andExpect(jsonPath("$.detalle.clientes.insertados").value(2))
                .andExpect(jsonPath("$.detalle.consumos.actualizados").value(2))
                .andExpect(jsonPath("$.detalle.pagos.sinCambios").value(0))
                .andExpect(jsonPath("$.motivoFallo").doesNotExist());
    }

    @Test
    void getJobDeUnJobFallidoTraeElMotivo() throws Exception {
        Job job = registrado(new Job(JOB_ID, CREADO));
        job.iniciar(INICIO);
        job.fallar(FIN, "No se pudo escribir en el destino en DynamoDB.");

        mvc.perform(get("/api/sync/jobs/" + JOB_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("FALLIDO"))
                .andExpect(jsonPath("$.finalizadoEn").exists())
                .andExpect(jsonPath("$.motivoFallo").value("No se pudo escribir en el destino en DynamoDB."))
                .andExpect(jsonPath("$.registros").doesNotExist());
    }

    @Test
    void getJobDeUnJobQueNoExisteResponde404() throws Exception {
        when(repositorio.buscar(JOB_ID)).thenReturn(Optional.empty());

        mvc.perform(get("/api/sync/jobs/" + JOB_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("JOB_NO_ENCONTRADO"));
    }

    @Test
    void getJobConUnIdQueNoEsUuidResponde400() throws Exception {
        mvc.perform(get("/api/sync/jobs/no-es-un-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("JOB_ID_INVALIDO"));
    }
}
