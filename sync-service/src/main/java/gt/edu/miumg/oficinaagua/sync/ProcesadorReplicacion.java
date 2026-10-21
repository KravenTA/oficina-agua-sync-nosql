package gt.edu.miumg.oficinaagua.sync;

import java.time.Clock;
import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Lo que hace el Thread de segundo plano con cada job: lo pasa a EN_PROGRESO, ejecuta la
 * replicación y lo deja COMPLETADO o FALLIDO. Nunca lanza excepciones: un job que falla no debe
 * tumbar al Thread ni a los jobs que vienen detrás.
 *
 * Un fallo tampoco afecta a la instancia MySQL: la replicación solo lee de ella, con un usuario de
 * solo lectura (HU-55).
 */
@Component
public class ProcesadorReplicacion implements ProcesadorJob {

    static final String MOTIVO_INESPERADO = "Error inesperado durante la replicación.";
    static final String MOTIVO_INTERRUMPIDO = "La replicación fue interrumpida.";

    private static final Logger log = LoggerFactory.getLogger(ProcesadorReplicacion.class);

    private final Replicador replicador;
    private final Clock reloj;

    public ProcesadorReplicacion(Replicador replicador, Clock reloj) {
        this.replicador = replicador;
        this.reloj = reloj;
    }

    @Override
    public void procesar(Job job) {
        job.iniciar(ahora());
        log.info("Job {}: PENDIENTE -> EN_PROGRESO", job.getId());

        try {
            ResultadoReplicacion resultado = replicador.replicar();
            job.completar(ahora(), resultado);
            Conteo total = resultado.total();
            log.info("Job {}: EN_PROGRESO -> COMPLETADO ({} insertados, {} actualizados, {} sin cambios)",
                    job.getId(), total.insertados(), total.actualizados(), total.sinCambios());
        } catch (ReplicacionException ex) {
            log.warn("Job {}: EN_PROGRESO -> FALLIDO ({})", job.getId(), ex.getMessage(), ex);
            job.fallar(ahora(), ex.getMessage());
        } catch (InterruptedException ex) {
            // El servicio se está apagando: se conserva la señal de interrupción para el hilo.
            Thread.currentThread().interrupt();
            log.warn("Job {}: EN_PROGRESO -> FALLIDO (interrumpido)", job.getId());
            job.fallar(ahora(), MOTIVO_INTERRUMPIDO);
        } catch (Exception ex) {
            // El detalle puede traer URLs, usuarios o SQL: va al log, no al motivo del job.
            log.error("Job {}: EN_PROGRESO -> FALLIDO por un error inesperado", job.getId(), ex);
            job.fallar(ahora(), MOTIVO_INESPERADO);
        }
    }

    private OffsetDateTime ahora() {
        return OffsetDateTime.now(reloj);
    }
}
