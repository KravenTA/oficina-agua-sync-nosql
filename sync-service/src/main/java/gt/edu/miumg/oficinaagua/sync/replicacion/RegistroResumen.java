package gt.edu.miumg.oficinaagua.sync.replicacion;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import gt.edu.miumg.oficinaagua.sync.Entidad;

/**
 * Un registro del resumen, igual en el origen (MySQL) y en el destino (DynamoDB).
 *
 * @param entidad tipo de dato (clave de partición en DynamoDB)
 * @param id      id del registro en MySQL, como texto (clave de ordenamiento en DynamoDB)
 * @param datos   los demás campos; los valores son String, BigDecimal o Boolean, nunca null
 */
public record RegistroResumen(Entidad entidad, String id, Map<String, Object> datos) {

    public RegistroResumen {
        datos = Collections.unmodifiableMap(new LinkedHashMap<>(datos));
    }

    /**
     * Huella SHA-256 de los datos. Se guarda en DynamoDB junto al registro: si en la siguiente
     * replicación la huella del origen es otra, el registro cambió. No depende del orden de los
     * campos ni de ceros de más en los decimales (15.00 y 15.0 dan la misma huella).
     */
    public String hash() {
        StringBuilder texto = new StringBuilder();
        new TreeMap<>(datos).forEach((campo, valor) ->
                texto.append(campo).append('=').append(canonico(valor)).append('\n'));
        try {
            byte[] huella = MessageDigest.getInstance("SHA-256")
                    .digest(texto.toString().getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(huella);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no está disponible", e);
        }
    }

    private static String canonico(Object valor) {
        if (valor instanceof BigDecimal numero) {
            return numero.stripTrailingZeros().toPlainString();
        }
        return String.valueOf(valor);
    }
}
