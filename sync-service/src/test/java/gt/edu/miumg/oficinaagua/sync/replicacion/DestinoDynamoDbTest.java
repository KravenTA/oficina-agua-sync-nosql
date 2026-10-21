package gt.edu.miumg.oficinaagua.sync.replicacion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import gt.edu.miumg.oficinaagua.sync.Entidad;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

class DestinoDynamoDbTest {

    private static final String TABLA = "oficina_agua_resumen";

    private final DynamoDbClient dynamo = mock(DynamoDbClient.class);
    // Sin espera entre reintentos para que las pruebas sean rápidas.
    private final DestinoDynamoDb destino = new DestinoDynamoDb(dynamo, new DestinoProperties(),
            Clock.fixed(Instant.parse("2026-10-21T10:00:00Z"), ZoneOffset.UTC), 0);

    private static RegistroResumen cliente(int id) {
        return new RegistroResumen(Entidad.CLIENTE, String.valueOf(id), Map.of("nombre", "Cliente " + id));
    }

    private static BatchWriteItemResponse sinPendientes() {
        return BatchWriteItemResponse.builder().build();
    }

    private static BatchWriteItemResponse conPendientes(List<WriteRequest> pendientes) {
        return BatchWriteItemResponse.builder().unprocessedItems(Map.of(TABLA, pendientes)).build();
    }

    @Test
    void escribeConPutRequestLaClaveLaHuellaYLosTiposCorrectos() {
        when(dynamo.batchWriteItem(any(BatchWriteItemRequest.class))).thenReturn(sinPendientes());
        Map<String, Object> datos = new LinkedHashMap<>();
        datos.put("monto", new BigDecimal("15.00"));
        datos.put("metodo", "efectivo");
        datos.put("anulado", false);
        RegistroResumen pago = new RegistroResumen(Entidad.PAGO, "7", datos);

        destino.upsert(List.of(pago));

        ArgumentCaptor<BatchWriteItemRequest> captor = ArgumentCaptor.forClass(BatchWriteItemRequest.class);
        verify(dynamo).batchWriteItem(captor.capture());
        WriteRequest escritura = captor.getValue().requestItems().get(TABLA).get(0);
        assertThat(escritura.deleteRequest()).isNull();
        Map<String, AttributeValue> item = escritura.putRequest().item();
        assertThat(item.get("entidad").s()).isEqualTo("PAGO");
        assertThat(item.get("id").s()).isEqualTo("7");
        assertThat(item.get("hash").s()).isEqualTo(pago.hash());
        assertThat(item.get("monto").n()).isEqualTo("15.00");
        assertThat(item.get("metodo").s()).isEqualTo("efectivo");
        assertThat(item.get("anulado").bool()).isFalse();
        assertThat(item.get("replicadoEn").s()).isEqualTo("2026-10-21T10:00:00Z");
        // Solo se escribió: ninguna otra operación, en particular ningún DeleteItem.
        verifyNoMoreInteractions(dynamo);
    }

    @Test
    void escribeEnLotesDeHasta25Registros() {
        when(dynamo.batchWriteItem(any(BatchWriteItemRequest.class))).thenReturn(sinPendientes());
        List<RegistroResumen> registros = new ArrayList<>();
        for (int id = 1; id <= 61; id++) {
            registros.add(cliente(id));
        }

        destino.upsert(registros);

        ArgumentCaptor<BatchWriteItemRequest> captor = ArgumentCaptor.forClass(BatchWriteItemRequest.class);
        verify(dynamo, times(3)).batchWriteItem(captor.capture());
        assertThat(captor.getAllValues()).extracting(lote -> lote.requestItems().get(TABLA).size())
                .containsExactly(25, 25, 11);
    }

    @Test
    void reintentaLoQueDynamoDbDejaSinEscribir() {
        RegistroResumen uno = cliente(1);
        RegistroResumen dos = cliente(2);
        List<WriteRequest> pendiente = new ArrayList<>();
        when(dynamo.batchWriteItem(any(BatchWriteItemRequest.class)))
                .thenAnswer(llamada -> {
                    BatchWriteItemRequest lote = llamada.getArgument(0);
                    pendiente.add(lote.requestItems().get(TABLA).get(1));
                    return conPendientes(List.copyOf(pendiente));
                })
                .thenReturn(sinPendientes());

        destino.upsert(List.of(uno, dos));

        ArgumentCaptor<BatchWriteItemRequest> captor = ArgumentCaptor.forClass(BatchWriteItemRequest.class);
        verify(dynamo, times(2)).batchWriteItem(captor.capture());
        List<WriteRequest> reintento = captor.getAllValues().get(1).requestItems().get(TABLA);
        assertThat(reintento).hasSize(1);
        assertThat(reintento.get(0).putRequest().item().get("id").s()).isEqualTo("2");
    }

    @Test
    void siDespuesDeVariosIntentosQuedanPendientesLaEscrituraFalla() {
        when(dynamo.batchWriteItem(any(BatchWriteItemRequest.class))).thenAnswer(llamada -> {
            BatchWriteItemRequest lote = llamada.getArgument(0);
            return conPendientes(lote.requestItems().get(TABLA));
        });

        assertThatThrownBy(() -> destino.upsert(List.of(cliente(1))))
                .isInstanceOf(SdkClientException.class)
                .hasMessageContaining("sin escribir");
        verify(dynamo, times(DestinoDynamoDb.MAX_INTENTOS)).batchWriteItem(any(BatchWriteItemRequest.class));
    }

    @Test
    void leeLasHuellasDeTodasLasPaginas() {
        Map<String, AttributeValue> continuar = Map.of(
                "entidad", AttributeValue.fromS("CLIENTE"), "id", AttributeValue.fromS("1"));
        when(dynamo.query(any(QueryRequest.class)))
                .thenReturn(QueryResponse.builder()
                        .items(List.of(Map.of("id", AttributeValue.fromS("1"), "hash", AttributeValue.fromS("aaa"))))
                        .lastEvaluatedKey(continuar)
                        .build())
                .thenReturn(QueryResponse.builder()
                        .items(List.of(Map.of("id", AttributeValue.fromS("2"), "hash", AttributeValue.fromS("bbb"))))
                        .build());

        Map<String, String> hashes = destino.hashes(Entidad.CLIENTE);

        assertThat(hashes).containsOnly(Map.entry("1", "aaa"), Map.entry("2", "bbb"));
        ArgumentCaptor<QueryRequest> captor = ArgumentCaptor.forClass(QueryRequest.class);
        verify(dynamo, times(2)).query(captor.capture());
        assertThat(captor.getAllValues().get(0).expressionAttributeValues().get(":entidad").s()).isEqualTo("CLIENTE");
        assertThat(captor.getAllValues().get(1).exclusiveStartKey()).isEqualTo(continuar);
    }
}
