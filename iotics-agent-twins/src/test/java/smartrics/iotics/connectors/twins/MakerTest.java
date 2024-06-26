package smartrics.iotics.connectors.twins;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.iotics.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import smartrics.iotics.host.IoticsApi;
import smartrics.iotics.host.wrappers.TwinAPIFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MakerTest {

    private Maker maker;
    private IoticsApi ioticsApi;
    private TwinAPIFuture twinAPIFuture;

    @BeforeEach
    void setUp() {
        ioticsApi = mock(IoticsApi.class);
        twinAPIFuture = mock(TwinAPIFuture.class);
        when(ioticsApi.twinAPIFuture()).thenReturn(twinAPIFuture);

        maker = new ConcreteMaker(ioticsApi);
    }

    @Test
    void testDelete() {
        ArgumentCaptor<DeleteTwinRequest> requestCaptor = ArgumentCaptor.forClass(DeleteTwinRequest.class);
        SettableFuture<DeleteTwinResponse> futureResponse = SettableFuture.create();
        when(twinAPIFuture.deleteTwin(requestCaptor.capture())).thenReturn(futureResponse);

        ListenableFuture<DeleteTwinResponse> response = maker.delete();

        // Verify the method was called with the correct arguments
        verify(twinAPIFuture).deleteTwin(requestCaptor.getValue());

        // Validate captured request
        DeleteTwinRequest capturedRequest = requestCaptor.getValue();
        assertNotNull(capturedRequest);
        assertEquals("agentDid123", capturedRequest.getHeaders().getClientAppId());
        assertEquals("did123", capturedRequest.getArgs().getTwinId().getId());

        assertSame(futureResponse, response);
    }

    @Test
    void testMakeIfAbsent_ExistingTwin() {
        DescribeTwinResponse describeResponse = DescribeTwinResponse.newBuilder().setPayload(DescribeTwinResponse.Payload.newBuilder().setTwinId(TwinID.newBuilder().setId("did123").build()).build()).build();
        SettableFuture<DescribeTwinResponse> describeFuture = SettableFuture.create();
        describeFuture.set(describeResponse);
        when(twinAPIFuture.describeTwin(any(DescribeTwinRequest.class))).thenReturn(describeFuture);

        ListenableFuture<TwinID> result = maker.makeIfAbsent();

        assertTrue(result.isDone());
        assertDoesNotThrow(() -> assertEquals("did123", result.get().getId()));
    }

    private static class ConcreteMaker extends BaseTwin implements Maker {
        ConcreteMaker(IoticsApi api) {
            super(api);
        }

        @Override
        public ListenableFuture<UpsertTwinResponse> upsert() {
            return null;
        }
    }
}
