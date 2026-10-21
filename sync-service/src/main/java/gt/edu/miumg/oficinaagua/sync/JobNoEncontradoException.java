package gt.edu.miumg.oficinaagua.sync;

import java.util.UUID;

/** HU-57: se pidió un job que no existe (o que se perdió al reiniciar el servicio). Responde 404. */
public class JobNoEncontradoException extends RuntimeException {

    public JobNoEncontradoException(UUID jobId) {
        super("No existe el job " + jobId);
    }
}
