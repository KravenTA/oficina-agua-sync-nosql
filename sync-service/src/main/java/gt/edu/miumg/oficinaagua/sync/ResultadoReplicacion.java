package gt.edu.miumg.oficinaagua.sync;

import java.util.Map;

/**
 * Lo que dejó una replicación: los conteos por tipo de dato. GetJob (HU-57) los devuelve como
 * "detalle" (por entidad) y "registros" (el total).
 */
public record ResultadoReplicacion(Map<Entidad, Conteo> porEntidad) {

    public static final ResultadoReplicacion VACIO = new ResultadoReplicacion(Map.of());

    public ResultadoReplicacion {
        porEntidad = Map.copyOf(porEntidad);
    }

    public Conteo de(Entidad entidad) {
        return porEntidad.getOrDefault(entidad, Conteo.CERO);
    }

    public Conteo total() {
        return porEntidad.values().stream().reduce(Conteo.CERO, Conteo::sumar);
    }
}
