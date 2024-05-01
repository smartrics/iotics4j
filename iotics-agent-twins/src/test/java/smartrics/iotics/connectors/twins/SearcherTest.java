package smartrics.iotics.connectors.twins;

import com.google.protobuf.ByteString;
import com.iotics.api.SparqlQueryRequest;
import com.iotics.api.SparqlQueryResponse;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import smartrics.iotics.host.IoticsApi;
import smartrics.iotics.host.wrappers.MetaAPI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SearcherTest {

    @Mock
    private IoticsApi ioticsApi;
    @Mock
    private MetaAPI metaAPI;
    @Mock
    private StreamObserver<String> resultObserver;

    private Searcher searcher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(ioticsApi.metaAPI()).thenReturn(metaAPI);
        searcher = new ConcreteSearcher(ioticsApi);
    }

    @Test
    void testQuery() {
        SparqlQueryRequest.Payload payload = SparqlQueryRequest.Payload.newBuilder().setQuery(ByteString.copyFromUtf8("SELECT * WHERE {?s ?p ?o}")).build();
        ArgumentCaptor<StreamObserver<SparqlQueryResponse>> responseCaptor = ArgumentCaptor.forClass(StreamObserver.class);
        searcher.query(payload, resultObserver);
        verify(metaAPI).sparqlQuery(any(SparqlQueryRequest.class), responseCaptor.capture());
        assertEquals(responseCaptor.getValue().getClass(), SparqlQueryResponseStreamObserver.class);

    }

    private static class ConcreteSearcher extends BaseTwin implements Searcher {
        ConcreteSearcher(IoticsApi api) {
            super(api);
        }
    }
}
