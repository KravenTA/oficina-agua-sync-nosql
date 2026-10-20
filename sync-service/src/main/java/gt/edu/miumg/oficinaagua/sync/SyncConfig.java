package gt.edu.miumg.oficinaagua.sync;

import java.time.Clock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import gt.edu.miumg.oficinaagua.sync.seguridad.ApiKeyFilter;

@Configuration
@EnableConfigurationProperties(SyncProperties.class)
public class SyncConfig {

    private static final Logger log = LoggerFactory.getLogger(SyncConfig.class);

    @Bean
    public Clock reloj() {
        return Clock.systemUTC();
    }

    /**
     * El Thread de segundo plano. Es uno solo a propósito: las replicaciones se hacen una tras otra,
     * nunca dos a la vez escribiendo en DynamoDB. Si llegan varias solicitudes, esperan su turno.
     */
    @Bean(name = "hiloSync")
    public ExecutorService hiloSync() {
        return Executors.newSingleThreadExecutor(tarea -> {
            Thread hilo = new Thread(tarea, "sync-thread");
            hilo.setDaemon(true);
            return hilo;
        });
    }

    /** Solo protege la API (/api/*): Swagger UI y el contrato quedan libres. */
    @Bean
    public FilterRegistrationBean<ApiKeyFilter> apiKeyFilter(SyncProperties propiedades,
                                                             ObjectMapper mapper) {
        String clave = propiedades.getApiKey();
        if (clave == null || clave.isBlank()) {
            log.warn("SYNC_API_KEY no está configurada: todas las solicitudes a /api/* serán rechazadas con 401.");
        }
        FilterRegistrationBean<ApiKeyFilter> registro =
                new FilterRegistrationBean<>(new ApiKeyFilter(clave, mapper));
        registro.addUrlPatterns("/api/*");
        registro.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registro;
    }
}
