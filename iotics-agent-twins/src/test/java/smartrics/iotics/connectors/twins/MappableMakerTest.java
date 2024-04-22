package smartrics.iotics.connectors.twins;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.util.concurrent.*;
import com.iotics.api.ShareFeedDataRequest;
import com.iotics.api.TwinID;
import com.iotics.api.UpsertTwinRequest;
import com.iotics.api.UpsertTwinResponse;
import org.junit.jupiter.api.*;
import org.mockito.*;
import smartrics.iotics.host.IoticsApi;
import smartrics.iotics.host.wrappers.TwinAPIFuture;
import smartrics.iotics.identity.Identity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MappableMakerTest {

    @Mock private IoticsApi ioticsApi;
    @Mock private TwinAPIFuture twinAPIFuture;
    private MappableMaker mappableMaker;
    private Mapper realMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(ioticsApi.twinAPIFuture()).thenReturn(twinAPIFuture);

        // Assuming realMapper can be a simple implementation if real behavior is predictable and side-effect-free.
        realMapper = new Mapper() {
            @Override
            public Identity getTwinIdentity() {
                return new Identity("kN", "n", "did");
            }

            @Override
            public UpsertTwinRequest getUpsertTwinRequest() {
                return UpsertTwinRequest.newBuilder()
                        .setPayload(UpsertTwinRequest.Payload.newBuilder().setTwinId(TwinID.newBuilder().setId("twinID123").build()).build())  // Assuming some settings that don't require complex setup
                        .build();
            }

            @Override
            public List<ShareFeedDataRequest> getShareFeedDataRequest() {
                return Collections.singletonList(ShareFeedDataRequest.getDefaultInstance());
            }
        };

        mappableMaker = new MappableMaker() {
            @Override
            public Identity getMyIdentity() { return new Identity("keyName", "John Doe", "did123"); }
            @Override
            public Identity getAgentIdentity() { return new Identity("keyName", "Agent Name", "agentDid123"); }
            @Override
            public IoticsApi ioticsApi() { return ioticsApi; }
            @Override
            public Mapper getMapper() { return realMapper; }
        };
    }

    @Test
    void testUpsert() {
        UpsertTwinRequest expectedRequest = realMapper.getUpsertTwinRequest();
        SettableFuture<UpsertTwinResponse> futureResponse = SettableFuture.create();
        when(twinAPIFuture.upsertTwin(expectedRequest)).thenReturn(futureResponse);

        ListenableFuture<UpsertTwinResponse> response = mappableMaker.upsert();

        verify(twinAPIFuture).upsertTwin(expectedRequest);  // Check that the request is sent correctly
        assertSame(futureResponse, response);  // Confirm that the response is handled correctly
    }
}
