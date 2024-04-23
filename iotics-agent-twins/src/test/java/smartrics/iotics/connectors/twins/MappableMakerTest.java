package smartrics.iotics.connectors.twins;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.iotics.api.ShareFeedDataRequest;
import com.iotics.api.TwinID;
import com.iotics.api.UpsertTwinRequest;
import com.iotics.api.UpsertTwinResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import smartrics.iotics.host.IoticsApi;
import smartrics.iotics.host.wrappers.TwinAPIFuture;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MappableMakerTest {

    @Mock
    private IoticsApi ioticsApi;
    @Mock
    private TwinAPIFuture twinAPIFuture;
    private MappableMaker mappableMaker;
    private Mapper realMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(ioticsApi.twinAPIFuture()).thenReturn(twinAPIFuture);

        realMapper = new Mapper() {

            @Override
            public UpsertTwinRequest getUpsertTwinRequest() {
                return UpsertTwinRequest.newBuilder().setPayload(UpsertTwinRequest.Payload.newBuilder().setTwinId(TwinID.newBuilder().setId("twinID123").build()).build())  // Assuming some settings that don't require complex setup
                        .build();
            }

            @Override
            public List<ShareFeedDataRequest> getShareFeedDataRequest() {
                return Collections.singletonList(ShareFeedDataRequest.getDefaultInstance());
            }
        };


        mappableMaker = new ConcreteMappableMaker(ioticsApi);
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

    private class ConcreteMappableMaker extends BaseTwin implements MappableMaker {

        ConcreteMappableMaker(IoticsApi api) {
            super(api);
        }

        @Override
        public Mapper getMapper() {
            return MappableMakerTest.this.realMapper;
        }
    }
}
