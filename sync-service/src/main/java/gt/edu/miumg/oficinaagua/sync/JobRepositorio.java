package gt.edu.miumg.oficinaagua.sync;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

/**
 * Guarda los jobs en memoria. Se pierden al reiniciar el servicio: es suficiente porque un job
 * solo sirve para seguir una replicación en curso, y la replicación es repetible (upsert).
 */
@Repository
public class JobRepositorio {

    private final Map<UUID, Job> jobs = new ConcurrentHashMap<>();

    public void guardar(Job job) {
        jobs.put(job.getId(), job);
    }

    public Optional<Job> buscar(UUID id) {
        return Optional.ofNullable(jobs.get(id));
    }
}
