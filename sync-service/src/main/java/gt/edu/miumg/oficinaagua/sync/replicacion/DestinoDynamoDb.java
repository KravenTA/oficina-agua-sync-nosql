package gt.edu.miumg.oficinaagua.sync.replicacion;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import gt.edu.miumg.oficinaagua.sync.Entidad;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

/**
 * HU-56: escribe el resumen en la tabla de DynamoDB (clave: entidad + id).
 *
 * El upsert usa BatchWriteItem con PutRequest, en lotes de hasta 25 registros (el máximo que
 * permite DynamoDB): si la clave no existe, inserta; si existe, reemplaza ese registro. Nunca se
 * usa DeleteRequest ni DeleteItem: un registro que desapareció del origen se queda en el destino.
 */
@Repository
public class DestinoDynamoDb implements DestinoResumen {

    static final String ENTIDAD = "entidad";
    static final String ID = "id";
    static final String HASH = "hash";
    static final String REPLICADO_EN = "replicadoEn";

    /** Máximo de operaciones por BatchWriteItem que acepta DynamoDB. */
    static final int TAMANO_LOTE = 25;
    /** Intentos por lote para escribir lo que DynamoDB deje pendiente (UnprocessedItems). */
    static final int MAX_INTENTOS = 5;
    private static final long ESPERA_INICIAL_MS = 100;

    private static final Logger log = LoggerFactory.getLogger(DestinoDynamoDb.class);

    private final DynamoDbClient dynamo;
    private final String tabla;
    private final Clock reloj;
    private final long esperaInicialMs;

    @Autowired
    public DestinoDynamoDb(DynamoDbClient dynamo, DestinoProperties propiedades, Clock reloj) {
        this(dynamo, propiedades, reloj, ESPERA_INICIAL_MS);
    }

    DestinoDynamoDb(DynamoDbClient dynamo, DestinoProperties propiedades, Clock reloj, long esperaInicialMs) {
        this.dynamo = dynamo;
        this.tabla = propiedades.getTabla();
        this.reloj = reloj;
        this.esperaInicialMs = esperaInicialMs;
    }

    @Override
    public Map<String, String> hashes(Entidad entidad) {
        Map<String, String> hashes = new HashMap<>();
        Map<String, AttributeValue> desde = null;
        do {
            // "hash" es palabra reservada en DynamoDB: se usa por medio de #hash.
            QueryRequest consulta = QueryRequest.builder()
                    .tableName(tabla)
                    .keyConditionExpression("#entidad = :entidad")
                    .projectionExpression("#id, #hash")
                    .expressionAttributeNames(Map.of("#entidad", ENTIDAD, "#id", ID, "#hash", HASH))
                    .expressionAttributeValues(Map.of(":entidad", AttributeValue.fromS(entidad.name())))
                    .consistentRead(true)
                    .exclusiveStartKey(desde)
                    .build();
            QueryResponse respuesta = dynamo.query(consulta);
            for (Map<String, AttributeValue> item : respuesta.items()) {
                AttributeValue hash = item.get(HASH);
                hashes.put(item.get(ID).s(), hash == null ? "" : hash.s());
            }
            desde = respuesta.hasLastEvaluatedKey() ? respuesta.lastEvaluatedKey() : null;
        } while (desde != null);
        return hashes;
    }

    @Override
    public void upsert(List<RegistroResumen> registros) {
        String replicadoEn = Instant.now(reloj).toString();
        for (int inicio = 0; inicio < registros.size(); inicio += TAMANO_LOTE) {
            List<WriteRequest> lote = new ArrayList<>();
            for (RegistroResumen registro : registros.subList(inicio, Math.min(inicio + TAMANO_LOTE, registros.size()))) {
                lote.add(WriteRequest.builder()
                        .putRequest(PutRequest.builder().item(item(registro, replicadoEn)).build())
                        .build());
            }
            escribirLote(lote);
        }
    }

    /** Escribe un lote y reintenta, con espera progresiva, lo que DynamoDB deje pendiente. */
    private void escribirLote(List<WriteRequest> lote) {
        Map<String, List<WriteRequest>> pendientes = Map.of(tabla, lote);
        for (int intento = 1; ; intento++) {
            BatchWriteItemResponse respuesta = dynamo.batchWriteItem(
                    BatchWriteItemRequest.builder().requestItems(pendientes).build());
            if (!respuesta.hasUnprocessedItems() || respuesta.unprocessedItems().isEmpty()) {
                return;
            }
            pendientes = respuesta.unprocessedItems();
            int cantidad = pendientes.values().stream().mapToInt(List::size).sum();
            if (intento >= MAX_INTENTOS) {
                throw SdkClientException.create(
                        cantidad + " registros quedaron sin escribir después de " + MAX_INTENTOS + " intentos");
            }
            log.warn("DynamoDB dejó {} registros sin escribir; reintento {} de {}", cantidad, intento + 1, MAX_INTENTOS);
            esperar(esperaInicialMs << (intento - 1));
        }
    }

    private static void esperar(long milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw SdkClientException.create("La escritura en DynamoDB fue interrumpida", e);
        }
    }

    private static Map<String, AttributeValue> item(RegistroResumen registro, String replicadoEn) {
        Map<String, AttributeValue> item = new HashMap<>();
        registro.datos().forEach((campo, valor) -> item.put(campo, atributo(valor)));
        item.put(ENTIDAD, AttributeValue.fromS(registro.entidad().name()));
        item.put(ID, AttributeValue.fromS(registro.id()));
        item.put(HASH, AttributeValue.fromS(registro.hash()));
        item.put(REPLICADO_EN, AttributeValue.fromS(replicadoEn));
        return item;
    }

    private static AttributeValue atributo(Object valor) {
        if (valor instanceof BigDecimal numero) {
            return AttributeValue.fromN(numero.toPlainString());
        }
        if (valor instanceof Boolean booleano) {
            return AttributeValue.fromBool(booleano);
        }
        return AttributeValue.fromS(String.valueOf(valor));
    }
}
