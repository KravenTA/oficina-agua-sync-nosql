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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import gt.edu.miumg.oficinaagua.sync.api.model.CodigoError;
import gt.edu.miumg.oficinaagua.sync.api.model.ErrorRespuesta;

/** Traduce las excepciones al formato único de error del contrato. Nunca expone detalles internos. */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /** GetJob con un jobId que no tiene formato de UUID. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorRespuesta> jobIdInvalido(MethodArgumentTypeMismatchException ex) {
        return error(HttpStatus.BAD_REQUEST, CodigoError.JOB_ID_INVALIDO, "El jobId debe ser un UUID.");
    }

    /** GetJob con un jobId que no existe. */
    @ExceptionHandler(JobNoEncontradoException.class)
    public ResponseEntity<ErrorRespuesta> jobNoEncontrado(JobNoEncontradoException ex) {
        return error(HttpStatus.NOT_FOUND, CodigoError.JOB_NO_ENCONTRADO, "No se encontró un job con ese jobId.");
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
