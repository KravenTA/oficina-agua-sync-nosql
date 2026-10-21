package gt.edu.miumg.oficinaagua.sync.replicacion;

import java.util.List;

import gt.edu.miumg.oficinaagua.sync.Entidad;

/** HU-55: de dónde se lee el resumen. Solo lectura. */
public interface OrigenResumen {

    List<RegistroResumen> leer(Entidad entidad);
}
