package gt.edu.miumg.oficinaagua.sync.replicacion;

import java.net.URI;
import java.time.Duration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

/** HU-56: cliente de DynamoDB. Crearlo no abre conexiones: solo conecta al replicar. */
@Configuration
@EnableConfigurationProperties(DestinoProperties.class)
public class DynamoDbConfig {

    @Bean
    public DynamoDbClient dynamoDbClient(DestinoProperties propiedades) {
        DynamoDbClientBuilder builder = DynamoDbClient.builder()
                .region(Region.of(propiedades.getRegion()))
                // Cliente HTTP explícito: el que el SDK elige por defecto requiere una versión de
                // Apache HttpClient más nueva que la que trae Spring Boot 3.3.
                .httpClientBuilder(UrlConnectionHttpClient.builder()
                        .connectionTimeout(Duration.ofSeconds(3))
                        .socketTimeout(Duration.ofSeconds(5)))
                .overrideConfiguration(config -> config
                        .apiCallTimeout(Duration.ofSeconds(10))
                        .apiCallAttemptTimeout(Duration.ofSeconds(3)));

        String endpoint = propiedades.getEndpoint();
        if (endpoint == null || endpoint.isBlank()) {
            builder.credentialsProvider(DefaultCredentialsProvider.builder().build());
        } else {
            builder.endpointOverride(URI.create(endpoint))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(propiedades.getAccessKey(), propiedades.getSecretKey())));
        }
        return builder.build();
    }
}
