package gt.edu.miumg.oficinaagua.sync.replicacion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import gt.edu.miumg.oficinaagua.sync.Conteo;
import gt.edu.miumg.oficinaagua.sync.Entidad;

class DiferenciaTest {

    private static RegistroResumen cliente(String id, String nombre) {
        return new RegistroResumen(Entidad.CLIENTE, id, Map.of("nombre", nombre));
    }

    @Test
    void clasificaEnNuevosModificadosEIguales() {
        RegistroResumen igual = cliente("1", "Ana");
        RegistroResumen modificado = cliente("2", "Carlos Pérez");
        RegistroResumen nuevo = cliente("3", "María");
        Map<String, String> destino = Map.of(
                "1", igual.hash(),
                "2", cliente("2", "Carlos").hash());

        Diferencia diferencia = Diferencia.calcular(List.of(igual, modificado, nuevo), destino);

        assertThat(diferencia.nuevos()).containsExactly(nuevo);
        assertThat(diferencia.modificados()).containsExactly(modificado);
        assertThat(diferencia.iguales()).isEqualTo(1);
        assertThat(diferencia.conteo()).isEqualTo(new Conteo(1, 1, 1));
    }

    @Test
    void conElDestinoVacioTodoEsNuevo() {
        Diferencia diferencia = Diferencia.calcular(List.of(cliente("1", "Ana"), cliente("2", "Luis")), Map.of());

        assertThat(diferencia.conteo()).isEqualTo(new Conteo(2, 0, 0));
    }

    @Test
    void losRegistrosQueSoloEstanEnElDestinoSeIgnoran() {
        Diferencia diferencia = Diferencia.calcular(List.of(), Map.of("99", "huella-vieja"));

        assertThat(diferencia.conteo()).isEqualTo(Conteo.CERO);
    }
}
