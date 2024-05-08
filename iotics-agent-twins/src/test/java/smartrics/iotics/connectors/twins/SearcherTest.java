package smartrics.iotics.connectors.twins;

import com.google.protobuf.ByteString;
import com.iotics.api.SearchRequest;
import com.iotics.api.SearchResponse;
import com.iotics.api.SparqlQueryRequest;
import com.iotics.api.SparqlQueryResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import smartrics.iotics.host.IoticsApi;
import smartrics.iotics.host.wrappers.MetaAPI;
import smartrics.iotics.host.wrappers.SearchAPI;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearcherTest {

    @Mock
    private IoticsApi ioticsApi;
    @Mock
    private MetaAPI metaAPI;
    @Mock
    private SearchAPI searchAPI;
    @Mock
    private StreamObserver<String> qResultObserver;

    @Mock
    private StreamObserver<SearchResponse.TwinDetails> sResultObserver;

    private Searcher searcher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(ioticsApi.metaAPI()).thenReturn(metaAPI);
        when(ioticsApi.searchAPI()).thenReturn(searchAPI);
        searcher = new ConcreteSearcher(ioticsApi);
    }

    @Test
    void testSearch() {
        SearchRequest.Payload payload = SearchRequest.Payload.newBuilder().build();
        ArgumentCaptor<StreamObserver<SearchResponse>> responseCaptor = ArgumentCaptor.forClass(StreamObserver.class);
        ArgumentCaptor<SearchRequest> requestCaptor = ArgumentCaptor.forClass(SearchRequest.class);
        searcher.search(payload, Duration.ofSeconds(10), sResultObserver);
        verify(searchAPI).synchronousSearch(requestCaptor.capture(), responseCaptor.capture());
        assertTrue(requestCaptor.getValue().getHeaders().hasRequestTimeout());
        assertSame(requestCaptor.getValue().getPayload(), payload);
    }

    @Test
    void testQuery() {
        SparqlQueryRequest.Payload payload = SparqlQueryRequest.Payload.newBuilder().setQuery(ByteString.copyFromUtf8("SELECT * WHERE {?s ?p ?o}")).build();
        ArgumentCaptor<StreamObserver<SparqlQueryResponse>> responseCaptor = ArgumentCaptor.forClass(StreamObserver.class);
        searcher.query(payload, qResultObserver);
        verify(metaAPI).sparqlQuery(any(SparqlQueryRequest.class), responseCaptor.capture());
        assertEquals(responseCaptor.getValue().getClass(), SparqlQueryResponseStreamObserver.class);
    }

    private static class ConcreteSearcher extends BaseTwin implements Searcher {
        ConcreteSearcher(IoticsApi api) {
            super(api);
        }
    }
}
