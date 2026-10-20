package gt.edu.miumg.oficinaagua.sync;

/**
 * Un fallo esperado de la replicación (por ejemplo, DynamoDB no responde).
 *
 * El mensaje se guarda tal cual como motivo del job y lo verá quien consulte GetJob, así que
 * debe ser legible y sin detalles internos: nada de URLs, usuarios, claves ni SQL. El detalle
 * técnico va en la causa, que solo se escribe en el log del servidor.
 */
public class ReplicacionException extends Exception {

    public ReplicacionException(String mensajeSeguro) {
        super(mensajeSeguro);
    }

    public ReplicacionException(String mensajeSeguro, Throwable causa) {
        super(mensajeSeguro, causa);
    }
}
