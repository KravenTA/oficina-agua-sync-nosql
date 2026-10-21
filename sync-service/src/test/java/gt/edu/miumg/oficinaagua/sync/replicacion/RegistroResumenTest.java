package gt.edu.miumg.oficinaagua.sync.replicacion;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import gt.edu.miumg.oficinaagua.sync.Entidad;

class RegistroResumenTest {

    private static RegistroResumen pago(Map<String, Object> datos) {
        return new RegistroResumen(Entidad.PAGO, "1", datos);
    }

    @Test
    void laHuellaNoDependeDelOrdenDeLosCampos() {
        Map<String, Object> unOrden = new LinkedHashMap<>();
        unOrden.put("monto", new BigDecimal("15.00"));
        unOrden.put("metodo", "efectivo");
        Map<String, Object> otroOrden = new LinkedHashMap<>();
        otroOrden.put("metodo", "efectivo");
        otroOrden.put("monto", new BigDecimal("15.00"));

        assertThat(pago(unOrden).hash()).isEqualTo(pago(otroOrden).hash());
    }

    @Test
    void losCerosDeMasEnLosDecimalesNoCambianLaHuella() {
        assertThat(pago(Map.of("monto", new BigDecimal("15.00"))).hash())
                .isEqualTo(pago(Map.of("monto", new BigDecimal("15.0"))).hash());
    }

    @Test
    void unCambioEnLosDatosCambiaLaHuella() {
        assertThat(pago(Map.of("monto", new BigDecimal("15.00"))).hash())
                .isNotEqualTo(pago(Map.of("monto", new BigDecimal("18.00"))).hash());
    }

    @Test
    void laHuellaEsUnSha256EnHexadecimal() {
        assertThat(pago(Map.of("metodo", "efectivo")).hash()).matches("[0-9a-f]{64}");
    }
}
