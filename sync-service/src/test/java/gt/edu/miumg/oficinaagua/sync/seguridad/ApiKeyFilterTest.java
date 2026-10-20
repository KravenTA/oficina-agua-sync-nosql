package gt.edu.miumg.oficinaagua.sync.seguridad;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class ApiKeyFilterTest {

    private static final String CLAVE = "clave-de-prueba";

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    private MockHttpServletRequest solicitud(String apiKey) {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/sync/resumen");
        if (apiKey != null) {
            request.addHeader("X-API-Key", apiKey);
        }
        return request;
    }

    @Test
    void conLaClaveCorrectaDejaPasar() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain cadena = new MockFilterChain();

        new ApiKeyFilter(CLAVE, mapper).doFilter(solicitud(CLAVE), response, cadena);

        assertThat(cadena.getRequest()).isNotNull();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void sinLaCabeceraResponde401YNoLlegaAlControlador() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain cadena = new MockFilterChain();

        new ApiKeyFilter(CLAVE, mapper).doFilter(solicitud(null), response, cadena);

        assertThat(cadena.getRequest()).isNull();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("NO_AUTORIZADO");
    }

    @Test
    void conUnaClaveIncorrectaResponde401() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain cadena = new MockFilterChain();

        new ApiKeyFilter(CLAVE, mapper).doFilter(solicitud("otra-clave"), response, cadena);

        assertThat(cadena.getRequest()).isNull();
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void siElServicioNoTieneClaveConfiguradaRechazaTodoIncluidaLaCabeceraVacia() throws Exception {
        for (String configurada : new String[] {null, "", "   "}) {
            for (String enviada : new String[] {null, "", "   ", "cualquiera"}) {
                MockHttpServletResponse response = new MockHttpServletResponse();
                MockFilterChain cadena = new MockFilterChain();

                new ApiKeyFilter(configurada, mapper).doFilter(solicitud(enviada), response, cadena);

                assertThat(cadena.getRequest()).isNull();
                assertThat(response.getStatus()).isEqualTo(401);
            }
        }
    }

    @Test
    void laRespuestaDeErrorNoRevelaLaClaveEsperada() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        new ApiKeyFilter(CLAVE, mapper).doFilter(solicitud("mala"), response, new MockFilterChain());

        assertThat(response.getContentAsString()).doesNotContain(CLAVE);
    }
}
