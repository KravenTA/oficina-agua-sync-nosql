package gt.edu.miumg.oficinaagua.sync;

/**
 * Los tres tipos de dato que forman el resumen. Su nombre es la clave de partición
 * (atributo "entidad") de la tabla de resumen en DynamoDB.
 */
public enum Entidad {
    CLIENTE,
    CONSUMO,
    PAGO
}
