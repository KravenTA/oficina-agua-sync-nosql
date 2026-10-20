package gt.edu.miumg.oficinaagua.sync.seguridad;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import gt.edu.miumg.oficinaagua.sync.api.model.CodigoError;
import gt.edu.miumg.oficinaagua.sync.api.model.ErrorRespuesta;

/**
 * Deja pasar solo las solicitudes con la API key correcta en la cabecera X-API-Key; al resto les
 * responde 401 antes de llegar al controlador. Si el servicio no tiene clave configurada, rechaza
 * todo: es mejor no responder que dejar la API abierta por un olvido.
 */
public class ApiKeyFilter extends OncePerRequestFilter {

    public static final String CABECERA = "X-API-Key";

    private static final Logger log = LoggerFactory.getLogger(ApiKeyFilter.class);

    private final byte[] claveEsperada;
    private final ObjectMapper mapper;

    public ApiKeyFilter(String claveEsperada, ObjectMapper mapper) {
        // Una clave vacía o solo con espacios cuenta como "no configurada": nadie entra.
        this.claveEsperada = claveEsperada == null || claveEsperada.isBlank()
                ? new byte[0] : claveEsperada.getBytes(StandardCharsets.UTF_8);
        this.mapper = mapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain cadena) throws ServletException, IOException {
        if (autorizada(request.getHeader(CABECERA))) {
            cadena.doFilter(request, response);
            return;
        }

        log.warn("Solicitud rechazada por API key ausente o inválida: {} {}", request.getMethod(),
                request.getRequestURI());

        ErrorRespuesta cuerpo = new ErrorRespuesta();
        cuerpo.setCodigo(CodigoError.NO_AUTORIZADO);
        cuerpo.setMensaje("Falta la API key o no es válida.");
        cuerpo.setFecha(OffsetDateTime.now(ZoneOffset.UTC));

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getOutputStream(), cuerpo);
    }

    private boolean autorizada(String recibida) {
        if (claveEsperada.length == 0 || recibida == null) {
            return false;
        }
        // Comparación en tiempo constante, para no revelar la clave por el tiempo de respuesta.
        return MessageDigest.isEqual(claveEsperada, recibida.getBytes(StandardCharsets.UTF_8));
    }
}
