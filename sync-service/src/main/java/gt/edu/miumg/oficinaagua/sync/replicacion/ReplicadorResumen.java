package gt.edu.miumg.oficinaagua.sync.replicacion;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import gt.edu.miumg.oficinaagua.sync.Conteo;
import gt.edu.miumg.oficinaagua.sync.Entidad;
import gt.edu.miumg.oficinaagua.sync.ReplicacionException;
import gt.edu.miumg.oficinaagua.sync.Replicador;
import gt.edu.miumg.oficinaagua.sync.ResultadoReplicacion;
import software.amazon.awssdk.core.exception.SdkException;

/**
 * HU-55 y HU-56: la replicación real. Por cada tipo de dato (clientes, consumos y pagos):
 * lee el origen, lee las huellas del destino, calcula la diferencia y escribe solo los registros
 * nuevos y modificados.
 *
 * Es repetible: si se ejecuta dos veces seguidas, la segunda encuentra todo igual y no escribe
 * nada. Si falla a la mitad, lo ya escrito es correcto y la siguiente ejecución continúa.
 */
@Component
public class ReplicadorResumen implements Replicador {

    static final String ERROR_ORIGEN = "No se pudo leer la instancia MySQL de origen.";
    static final String ERROR_LECTURA_DESTINO = "No se pudo consultar el destino en DynamoDB.";
    static final String ERROR_ESCRITURA_DESTINO = "No se pudo escribir en el destino en DynamoDB.";

    private static final Logger log = LoggerFactory.getLogger(ReplicadorResumen.class);

    private final OrigenResumen origen;
    private final DestinoResumen destino;

    public ReplicadorResumen(OrigenResumen origen, DestinoResumen destino) {
        this.origen = origen;
        this.destino = destino;
    }

    @Override
    public ResultadoReplicacion replicar() throws ReplicacionException {
        Map<Entidad, Conteo> conteos = new EnumMap<>(Entidad.class);
        for (Entidad entidad : Entidad.values()) {
            Diferencia diferencia = Diferencia.calcular(leerOrigen(entidad), leerDestino(entidad));
            escribir(diferencia.nuevos());
            escribir(diferencia.modificados());
            Conteo conteo = diferencia.conteo();
            conteos.put(entidad, conteo);
            log.info("{}: {} insertados, {} actualizados, {} sin cambios",
                    entidad, conteo.insertados(), conteo.actualizados(), conteo.sinCambios());
        }
        return new ResultadoReplicacion(conteos);
    }

    private List<RegistroResumen> leerOrigen(Entidad entidad) throws ReplicacionException {
        try {
            return origen.leer(entidad);
        } catch (DataAccessException ex) {
            throw new ReplicacionException(ERROR_ORIGEN, ex);
        }
    }

    private Map<String, String> leerDestino(Entidad entidad) throws ReplicacionException {
        try {
            return destino.hashes(entidad);
        } catch (SdkException ex) {
            throw new ReplicacionException(ERROR_LECTURA_DESTINO, ex);
        }
    }

    private void escribir(List<RegistroResumen> registros) throws ReplicacionException {
        try {
            if (!registros.isEmpty()) {
                destino.upsert(registros);
            }
        } catch (SdkException ex) {
            throw new ReplicacionException(ERROR_ESCRITURA_DESTINO, ex);
        }
    }
}
