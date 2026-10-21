package gt.edu.miumg.oficinaagua.sync.replicacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gt.edu.miumg.oficinaagua.sync.Conteo;

/**
 * HU-55: cómo está cada registro del origen respecto al destino.
 *
 * @param nuevos      no existen en el destino
 * @param modificados existen, pero su huella cambió
 * @param iguales     existen con la misma huella: no hace falta escribirlos
 */
public record Diferencia(List<RegistroResumen> nuevos, List<RegistroResumen> modificados, int iguales) {

    /**
     * Compara el origen con las huellas del destino. Los registros que solo están en el destino
     * se ignoran: nunca se borran.
     */
    public static Diferencia calcular(List<RegistroResumen> origen, Map<String, String> hashesDestino) {
        List<RegistroResumen> nuevos = new ArrayList<>();
        List<RegistroResumen> modificados = new ArrayList<>();
        int iguales = 0;
        for (RegistroResumen registro : origen) {
            String hashDestino = hashesDestino.get(registro.id());
            if (hashDestino == null) {
                nuevos.add(registro);
            } else if (!hashDestino.equals(registro.hash())) {
                modificados.add(registro);
            } else {
                iguales++;
            }
        }
        return new Diferencia(List.copyOf(nuevos), List.copyOf(modificados), iguales);
    }

    public Conteo conteo() {
        return new Conteo(nuevos.size(), modificados.size(), iguales);
    }
}
