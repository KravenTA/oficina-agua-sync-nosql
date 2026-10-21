package gt.edu.miumg.oficinaagua.sync;

/**
 * La replicación en sí: leer de la instancia MySQL (solo lectura), calcular la diferencia y aplicar
 * los upserts en DynamoDB. Las HU-55 y HU-56 la implementan.
 *
 * Si algo falla, lanza ReplicacionException con un mensaje seguro. Cualquier otra excepción también
 * deja el job FALLIDO, pero con un motivo genérico: el detalle solo queda en el log.
 */
public interface Replicador {

    void replicar() throws Exception;
}
