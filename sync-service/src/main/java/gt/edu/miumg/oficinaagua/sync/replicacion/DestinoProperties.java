package gt.edu.miumg.oficinaagua.sync.replicacion;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** HU-56: conexión con la tabla de resumen en DynamoDB. */
@ConfigurationProperties(prefix = "sync.dynamodb")
public class DestinoProperties {

    /** Con endpoint se usa DynamoDB Local; vacío, DynamoDB de AWS con las credenciales del entorno. */
    private String endpoint = "";
    private String tabla = "oficina_agua_resumen";
    private String region = "us-east-1";
    /** Solo para DynamoDB Local, que acepta cualquier valor. */
    private String accessKey = "local";
    private String secretKey = "local";

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getTabla() {
        return tabla;
    }

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
