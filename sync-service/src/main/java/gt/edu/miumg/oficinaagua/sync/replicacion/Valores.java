package gt.edu.miumg.oficinaagua.sync.replicacion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Convierte los valores que entrega JDBC a los tres tipos que usa el resumen. */
final class Valores {

    private Valores() {
    }

    /** String, BigDecimal o Boolean; null si el valor es null. Fechas en formato ISO-8601. */
    static Object normalizar(Object valor) {
        if (valor == null || valor instanceof String || valor instanceof Boolean || valor instanceof BigDecimal) {
            return valor;
        }
        if (valor instanceof Number numero) {
            return new BigDecimal(numero.toString());
        }
        if (valor instanceof java.sql.Timestamp instante) {
            return instante.toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        if (valor instanceof java.sql.Date fecha) {
            return fecha.toLocalDate().toString();
        }
        if (valor instanceof LocalDateTime instante) {
            return instante.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        if (valor instanceof LocalDate fecha) {
            return fecha.toString();
        }
        return valor.toString();
    }
}
