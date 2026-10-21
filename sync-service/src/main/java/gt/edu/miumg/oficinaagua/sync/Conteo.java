package gt.edu.miumg.oficinaagua.sync;

/** Cuántos registros se insertaron, se actualizaron o ya estaban iguales en el destino. */
public record Conteo(int insertados, int actualizados, int sinCambios) {

    public static final Conteo CERO = new Conteo(0, 0, 0);

    public Conteo sumar(Conteo otro) {
        return new Conteo(insertados + otro.insertados, actualizados + otro.actualizados,
                sinCambios + otro.sinCambios);
    }
}
