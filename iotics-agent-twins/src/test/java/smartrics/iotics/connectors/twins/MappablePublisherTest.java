package smartrics.iotics.connectors.twins;

import com.google.common.util.concurrent.SettableFuture;
import com.iotics.api.Headers;
import com.iotics.api.ShareFeedDataRequest;
import com.iotics.api.ShareFeedDataResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import smartrics.iotics.host.IoticsApi;
import smartrics.iotics.host.wrappers.FeedAPIFuture;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MappablePublisherTest {

    @Mock
    private Mapper mapper;
    @Mock
    private FeedAPIFuture feedAPIFuture;
    @Mock
    private IoticsApi ioticsApi;
    private MappablePublisher mappablePublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(ioticsApi.feedAPIFuture()).thenReturn(feedAPIFuture);

        mappablePublisher = new ConcreteMappablePublisher(ioticsApi);
    }

    @Test
    void testShare() {
        ShareFeedDataRequest request1 = ShareFeedDataRequest.newBuilder().setHeaders(Headers.newBuilder().setClientRef("cli1").build()).build();
        ShareFeedDataRequest request2 = ShareFeedDataRequest.newBuilder().setHeaders(Headers.newBuilder().setClientRef("cli2").build()).build();
        List<ShareFeedDataRequest> requests = Arrays.asList(request1, request2);

        when(mapper.getShareFeedDataRequest()).thenReturn(requests);

        SettableFuture<ShareFeedDataResponse> futureResponse1 = SettableFuture.create();
        futureResponse1.set(ShareFeedDataResponse.newBuilder().build());
        SettableFuture<ShareFeedDataResponse> futureResponse2 = SettableFuture.create();
        futureResponse2.set(ShareFeedDataResponse.newBuilder().build());

        when(feedAPIFuture.shareFeedData(request1)).thenReturn(futureResponse1);
        when(feedAPIFuture.shareFeedData(request2)).thenReturn(futureResponse2);

        CompletableFuture<Void> resultFuture = mappablePublisher.share();

        // Verify that the shareFeedData method is called for each request
        verify(feedAPIFuture).shareFeedData(eq(request1));
        verify(feedAPIFuture).shareFeedData(eq(request2));

        // Assert that the CompletableFuture completes successfully
        assertTrue(resultFuture.isDone(), "The CompletableFuture should be completed");
        assertFalse(resultFuture.isCompletedExceptionally(), "The CompletableFuture should not be completed exceptionally");
        assertNull(resultFuture.join(), "The result of CompletableFuture should be null");
    }

    private class ConcreteMappablePublisher extends BaseTwin implements MappablePublisher {
        ConcreteMappablePublisher(IoticsApi api) {
            super(api);
        }

        @Override
        public Mapper getMapper() {
            return MappablePublisherTest.this.mapper;
        }

    }
}
