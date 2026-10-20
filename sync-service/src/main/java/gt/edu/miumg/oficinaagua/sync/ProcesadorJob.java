package gt.edu.miumg.oficinaagua.sync;

/** Lo que hace el Thread de segundo plano con cada job que recibe. */
public interface ProcesadorJob {

    void procesar(Job job);
}
