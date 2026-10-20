package gt.edu.miumg.oficinaagua.sync;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import gt.edu.miumg.oficinaagua.sync.api.model.CodigoError;
import gt.edu.miumg.oficinaagua.sync.api.model.ErrorRespuesta;

/** Traduce las excepciones al formato único de error del contrato. Nunca expone detalles internos. */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /** Temporal: GetJob se implementa en la HU-57. */
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ErrorRespuesta> noDisponible(UnsupportedOperationException ex) {
        return error(HttpStatus.NOT_IMPLEMENTED, CodigoError.ERROR_INTERNO,
                "Esta operación todavía no está disponible.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorRespuesta> inesperado(RuntimeException ex) {
        log.error("Error inesperado en el sync-service", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, CodigoError.ERROR_INTERNO,
                "No se pudo completar la solicitud. Intente más tarde.");
    }

    private ResponseEntity<ErrorRespuesta> error(HttpStatus estado, CodigoError codigo, String mensaje) {
        ErrorRespuesta cuerpo = new ErrorRespuesta();
        cuerpo.setCodigo(codigo);
        cuerpo.setMensaje(mensaje);
        cuerpo.setFecha(OffsetDateTime.now(ZoneOffset.UTC));
        return ResponseEntity.status(estado).contentType(MediaType.APPLICATION_JSON).body(cuerpo);
    }
}
