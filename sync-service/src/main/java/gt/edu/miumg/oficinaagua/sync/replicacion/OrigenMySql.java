package gt.edu.miumg.oficinaagua.sync.replicacion;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import gt.edu.miumg.oficinaagua.sync.Entidad;

/**
 * HU-55: lee el resumen de la instancia MySQL con el usuario de solo lectura (SELECT).
 *
 * Solo se leen los campos que necesita el resumen: nunca DPI, NIT, teléfono, correo ni documentos
 * del cliente. Los alias de cada columna son los nombres de los atributos en DynamoDB.
 */
@Repository
public class OrigenMySql implements OrigenResumen {

    private static final Map<Entidad, String> CONSULTAS = new EnumMap<>(Entidad.class);

    static {
        CONSULTAS.put(Entidad.CLIENTE, """
                SELECT c.id, c.nombre, c.direccion, c.activo
                FROM clientes c
                ORDER BY c.id
                """);
        CONSULTAS.put(Entidad.CONSUMO, """
                SELECT l.id, s.cliente_id AS clienteId, ct.numero_serie AS contador,
                       CONCAT(p.anio, '-', LPAD(p.mes, 2, '0')) AS periodo,
                       l.lectura_anterior AS lecturaAnterior, l.lectura_actual AS lecturaActual,
                       l.consumo, l.monto, l.fecha_lectura AS fechaLectura
                FROM lecturas l
                JOIN servicios s ON s.id = l.servicio_id
                JOIN contadores ct ON ct.id = l.contador_id
                JOIN periodos p ON p.id = l.periodo_id
                ORDER BY l.id
                """);
        CONSULTAS.put(Entidad.PAGO, """
                SELECT pg.id, s.cliente_id AS clienteId, pg.lectura_id AS lecturaId, r.numero AS recibo,
                       pg.monto, pg.fecha_pago AS fechaPago, pg.metodo
                FROM pagos pg
                LEFT JOIN recibos r ON r.id = pg.recibo_id
                LEFT JOIN lecturas l ON l.id = COALESCE(pg.lectura_id, r.lectura_id)
                LEFT JOIN servicios s ON s.id = l.servicio_id
                ORDER BY pg.id
                """);
    }

    private final JdbcTemplate jdbc;

    public OrigenMySql(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<RegistroResumen> leer(Entidad entidad) {
        List<RegistroResumen> registros = new ArrayList<>();
        for (Map<String, Object> fila : jdbc.queryForList(CONSULTAS.get(entidad))) {
            Map<String, Object> datos = new LinkedHashMap<>();
            String id = null;
            for (Map.Entry<String, Object> columna : fila.entrySet()) {
                if ("id".equalsIgnoreCase(columna.getKey())) {
                    id = String.valueOf(columna.getValue());
                    continue;
                }
                Object valor = Valores.normalizar(columna.getValue());
                if (valor != null) {
                    datos.put(columna.getKey(), valor);
                }
            }
            registros.add(new RegistroResumen(entidad, id, datos));
        }
        return registros;
    }
}
