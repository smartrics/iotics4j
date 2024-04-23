package smartrics.iotics.connectors.twins;

import com.google.protobuf.ByteString;
import com.google.rpc.Status;
import com.iotics.api.SparqlQueryResponse;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

class SparqlQueryResponseStreamObserverTest {

    @Mock
    private StreamObserver<String> mockResultObserver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAggregatesResponsesInOrder() {
        SparqlQueryResponseStreamObserver observer = new SparqlQueryResponseStreamObserver(mockResultObserver);

        observer.onNext(createResponse("Part 1", 1, false));
        observer.onNext(createResponse("Part 2", 2, true)); // This response marks the last

        // Verify concatenated output is sent once the last response is received
        verify(mockResultObserver).onNext(eq("Part 1Part 2"));
        verify(mockResultObserver).onCompleted();
    }

    @Test
    void testAggregatesResponsesOutOfOrder() {
        SparqlQueryResponseStreamObserver observer = new SparqlQueryResponseStreamObserver(mockResultObserver);

        observer.onNext(createResponse("Part 2", 2, false));
        observer.onNext(createResponse("Part 1", 1, false));
        observer.onNext(createResponse("Part 3", 3, true));

        // Verify concatenated output is sent once the last response is received
        verify(mockResultObserver).onNext(eq("Part 1Part 2Part 3"));
        verify(mockResultObserver).onCompleted();
    }

    @Test
    void testAggregatesResponsesOutOfOrderWithLastReceivedBefore() {
        SparqlQueryResponseStreamObserver observer = new SparqlQueryResponseStreamObserver(mockResultObserver);

        observer.onNext(createResponse("Part 1", 1, false));
        observer.onNext(createResponse("Part 3", 3, true));
        observer.onNext(createResponse("Part 2", 2, false));

        // Verify concatenated output is sent once the last response is received
        verify(mockResultObserver).onNext(eq("Part 1Part 2Part 3"));
        verify(mockResultObserver).onCompleted();
    }

    @Test
    void testOnErrorCalledWhenReceivedError() {
        SparqlQueryResponseStreamObserver observer = new SparqlQueryResponseStreamObserver(mockResultObserver);

        ArgumentCaptor<SearchException> exceptionArgumentCaptor = ArgumentCaptor.forClass(SearchException.class);


        observer.onNext(createErrorResponse(-1000));

        // Verify concatenated output is sent once the last response is received
        verify(mockResultObserver).onError(exceptionArgumentCaptor.capture());

        SearchException exception = exceptionArgumentCaptor.getValue();
        assertEquals(-1000, exception.status().getCode());
    }

    private SparqlQueryResponse createResponse(String result, long seqNum, boolean isLast) {
        return SparqlQueryResponse.newBuilder()
                .setPayload(SparqlQueryResponse.Payload.newBuilder()
                        .setResultChunk(ByteString.copyFromUtf8(result))
                        .setSeqNum(seqNum)
                        .setLast(isLast)
                        .build())
                .build();
    }

    private SparqlQueryResponse createErrorResponse(int code) {
        return SparqlQueryResponse.newBuilder()
                .setPayload(SparqlQueryResponse.Payload.newBuilder()
                        .setStatus(Status.newBuilder().setCode(code).build()))
                .build();
    }
}
