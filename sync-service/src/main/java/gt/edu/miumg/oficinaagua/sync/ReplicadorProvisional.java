package gt.edu.miumg.oficinaagua.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Replicador provisional de la HU-54: no hace nada, así que todos los jobs terminan COMPLETADO.
 * Las HU-55 (diff) y HU-56 (upsert) lo reemplazan por el real.
 */
@Component
public class ReplicadorProvisional implements Replicador {

    private static final Logger log = LoggerFactory.getLogger(ReplicadorProvisional.class);

    @Override
    public void replicar() {
        log.info("Replicación provisional: todavía no se lee de MySQL ni se escribe en DynamoDB (HU-55 y HU-56).");
    }
}
