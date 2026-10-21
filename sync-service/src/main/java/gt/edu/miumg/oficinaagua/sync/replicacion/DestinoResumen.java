package gt.edu.miumg.oficinaagua.sync.replicacion;

import java.util.List;
import java.util.Map;

import gt.edu.miumg.oficinaagua.sync.Entidad;

/**
 * HU-56: dónde se escribe el resumen. A propósito no tiene una operación para borrar:
 * la replicación solo inserta o actualiza.
 */
public interface DestinoResumen {

    /** La huella de cada registro que ya está en el destino, por id. */
    Map<String, String> hashes(Entidad entidad);

    /** Inserta cada registro o, si ya existe con la misma clave, lo reemplaza. */
    void upsert(List<RegistroResumen> registros);
}
