package gt.edu.miumg.oficinaagua.sync.replicacion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;

import gt.edu.miumg.oficinaagua.sync.Conteo;
import gt.edu.miumg.oficinaagua.sync.Entidad;
import gt.edu.miumg.oficinaagua.sync.ReplicacionException;
import gt.edu.miumg.oficinaagua.sync.ResultadoReplicacion;
import software.amazon.awssdk.core.exception.SdkClientException;

/** La replicación completa con un origen y un destino en memoria, sin MySQL ni DynamoDB. */
class ReplicadorResumenTest {

    /** Origen en memoria: lo que hay en MySQL. */
    private static class OrigenEnMemoria implements OrigenResumen {
        final Map<Entidad, List<RegistroResumen>> registros = new EnumMap<>(Entidad.class);
        RuntimeException falla;

        @Override
        public List<RegistroResumen> leer(Entidad entidad) {
            if (falla != null) {
                throw falla;
            }
            return registros.getOrDefault(entidad, List.of());
        }
    }

    /** Destino en memoria: lo que hay en DynamoDB. No tiene forma de borrar, igual que el real. */
    private static class DestinoEnMemoria implements DestinoResumen {
        final Map<String, RegistroResumen> registros = new HashMap<>();
        final List<RegistroResumen> escritos = new ArrayList<>();
        RuntimeException fallaAlEscribir;

        @Override
        public Map<String, String> hashes(Entidad entidad) {
            Map<String, String> hashes = new HashMap<>();
            registros.values().stream()
                    .filter(registro -> registro.entidad() == entidad)
                    .forEach(registro -> hashes.put(registro.id(), registro.hash()));
            return hashes;
        }

        @Override
        public void upsert(List<RegistroResumen> lote) {
            if (fallaAlEscribir != null) {
                throw fallaAlEscribir;
            }
            for (RegistroResumen registro : lote) {
                registros.put(registro.entidad() + "#" + registro.id(), registro);
                escritos.add(registro);
            }
        }
    }

    private final OrigenEnMemoria origen = new OrigenEnMemoria();
    private final DestinoEnMemoria destino = new DestinoEnMemoria();
    private final ReplicadorResumen replicador = new ReplicadorResumen(origen, destino);

    private static RegistroResumen cliente(String id, String nombre) {
        return new RegistroResumen(Entidad.CLIENTE, id, Map.of("nombre", nombre, "activo", true));
    }

    private static RegistroResumen pago(String id, String monto) {
        return new RegistroResumen(Entidad.PAGO, id, Map.of("monto", new BigDecimal(monto), "metodo", "efectivo"));
    }

    @BeforeEach
    void datosDeOrigen() {
        origen.registros.put(Entidad.CLIENTE, List.of(cliente("1", "Ana"), cliente("2", "Carlos")));
        origen.registros.put(Entidad.PAGO, List.of(pago("1", "15.00")));
    }

    @Test
    void conElDestinoVacioInsertaTodo() throws Exception {
        ResultadoReplicacion resultado = replicador.replicar();

        assertThat(resultado.de(Entidad.CLIENTE)).isEqualTo(new Conteo(2, 0, 0));
        assertThat(resultado.de(Entidad.CONSUMO)).isEqualTo(Conteo.CERO);
        assertThat(resultado.de(Entidad.PAGO)).isEqualTo(new Conteo(1, 0, 0));
        assertThat(destino.registros).hasSize(3);
    }

    @Test
    void ejecutarDosVecesSeguidasDaElMismoResultadoYNoVuelveAEscribir() throws Exception {
        replicador.replicar();
        Map<String, RegistroResumen> despuesDeLaPrimera = new HashMap<>(destino.registros);
        destino.escritos.clear();

        ResultadoReplicacion segunda = replicador.replicar();

        assertThat(segunda.total()).isEqualTo(new Conteo(0, 0, 3));
        assertThat(destino.escritos).isEmpty();
        assertThat(destino.registros).isEqualTo(despuesDeLaPrimera);
    }

    @Test
    void conCambiosParcialesSoloEscribeLosNuevosYLosModificados() throws Exception {
        replicador.replicar();
        destino.escritos.clear();
        origen.registros.put(Entidad.CLIENTE, List.of(cliente("1", "Ana"), cliente("2", "Carlos Pérez"), cliente("3", "María")));

        ResultadoReplicacion resultado = replicador.replicar();

        assertThat(resultado.de(Entidad.CLIENTE)).isEqualTo(new Conteo(1, 1, 1));
        assertThat(destino.escritos).extracting(RegistroResumen::id).containsExactlyInAnyOrder("2", "3");
        assertThat(destino.registros.get("CLIENTE#2").datos()).containsEntry("nombre", "Carlos Pérez");
    }

    @Test
    void nuncaBorraUnRegistroQueDesaparecioDelOrigen() throws Exception {
        replicador.replicar();
        origen.registros.put(Entidad.CLIENTE, List.of(cliente("1", "Ana")));

        ResultadoReplicacion resultado = replicador.replicar();

        assertThat(destino.registros).containsKey("CLIENTE#2");
        assertThat(resultado.de(Entidad.CLIENTE)).isEqualTo(new Conteo(0, 0, 1));
    }

    @Test
    void siFallaElOrigenElMotivoNoRevelaDetallesInternos() {
        origen.falla = new DataAccessResourceFailureException("jdbc:mysql://mysql-origen:3306 usuario=sync_ro");

        assertThatThrownBy(replicador::replicar)
                .isInstanceOf(ReplicacionException.class)
                .hasMessage(ReplicadorResumen.ERROR_ORIGEN)
                .hasMessageNotContaining("jdbc");
    }

    @Test
    void siFallaElDestinoAlEscribirLaReplicacionFallaConUnMotivoClaro() {
        destino.fallaAlEscribir = SdkClientException.create("Unable to execute HTTP request: http://dynamodb:8000");

        assertThatThrownBy(replicador::replicar)
                .isInstanceOf(ReplicacionException.class)
                .hasMessage(ReplicadorResumen.ERROR_ESCRITURA_DESTINO)
                .hasMessageNotContaining("http");
    }
}
