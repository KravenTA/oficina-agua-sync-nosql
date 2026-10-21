package gt.edu.miumg.oficinaagua.sync.replicacion;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class ValoresTest {

    @Test
    void losNumerosSeConviertenEnBigDecimal() {
        assertThat(Valores.normalizar(25L)).isEqualTo(new BigDecimal("25"));
        assertThat(Valores.normalizar(new BigDecimal("15.00"))).isEqualTo(new BigDecimal("15.00"));
    }

    @Test
    void lasFechasQuedanEnFormatoIso() {
        assertThat(Valores.normalizar(Date.valueOf("2026-07-28"))).isEqualTo("2026-07-28");
        assertThat(Valores.normalizar(LocalDate.of(2026, 7, 28))).isEqualTo("2026-07-28");
        assertThat(Valores.normalizar(Timestamp.valueOf("2026-08-05 10:15:00"))).isEqualTo("2026-08-05T10:15:00");
        assertThat(Valores.normalizar(LocalDateTime.of(2026, 8, 5, 10, 15))).isEqualTo("2026-08-05T10:15:00");
    }

    @Test
    void textoBooleanosYNulosSeConservan() {
        assertThat(Valores.normalizar("efectivo")).isEqualTo("efectivo");
        assertThat(Valores.normalizar(true)).isEqualTo(true);
        assertThat(Valores.normalizar(null)).isNull();
    }
}
