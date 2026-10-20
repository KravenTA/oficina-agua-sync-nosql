package gt.edu.miumg.oficinaagua.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Procesador provisional de la HU-53: solo avisa que recibió el job y lo deja PENDIENTE.
 * La HU-54 lo reemplaza por el que cambia los estados y las HU-55 y HU-56 le agregan el diff y
 * el upsert.
 */
@Component
public class ProcesadorJobProvisional implements ProcesadorJob {

    private static final Logger log = LoggerFactory.getLogger(ProcesadorJobProvisional.class);

    @Override
    public void procesar(Job job) {
        log.info("Job {} recibido por el Thread. La replicación se implementa en las HU-54 a HU-56.",
                job.getId());
    }
}
