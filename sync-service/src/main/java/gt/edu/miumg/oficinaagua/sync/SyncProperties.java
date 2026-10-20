package gt.edu.miumg.oficinaagua.sync;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sync")
public class SyncProperties {

    /** API key que deben enviar los clientes en la cabecera X-API-Key. Vacía = nadie entra. */
    private String apiKey = "";

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
