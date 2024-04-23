package smartrics.iotics.connectors.twins;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.iotics.api.FeedID;
import com.iotics.api.ShareFeedDataRequest;
import com.iotics.api.ShareFeedDataResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import smartrics.iotics.host.IoticsApi;
import smartrics.iotics.host.wrappers.FeedAPIFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class PublisherTest {

    @Mock
    private IoticsApi ioticsApi;
    @Mock
    private FeedAPIFuture feedAPIFuture;
    private Publisher publisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(ioticsApi.feedAPIFuture()).thenReturn(feedAPIFuture);
        publisher = new ConcretePublisher(ioticsApi);
    }

    @Test
    void testShareWithRequest() {
        ShareFeedDataRequest request = ShareFeedDataRequest.newBuilder().build();
        ShareFeedDataResponse response = ShareFeedDataResponse.newBuilder().build();
        SettableFuture<ShareFeedDataResponse> futureResponse = SettableFuture.create();
        futureResponse.set(response);

        when(feedAPIFuture.shareFeedData(request)).thenReturn(futureResponse);

        ListenableFuture<ShareFeedDataResponse> result = publisher.share(request);

        verify(feedAPIFuture).shareFeedData(request);
        assertSame(futureResponse, result, "The future returned should be the same as the one returned by the API.");
    }

    @Test
    void testShareWithFeedIDAndPayload() throws Exception {
        FeedID feedID = FeedID.newBuilder().setId("feed123").build();
        String payload = "test data";

        ArgumentCaptor<ShareFeedDataRequest> requestCaptor = ArgumentCaptor.forClass(ShareFeedDataRequest.class);

        ShareFeedDataResponse response = ShareFeedDataResponse.newBuilder().build();
        SettableFuture<ShareFeedDataResponse> futureResponse = SettableFuture.create();
        futureResponse.set(response);

        when(feedAPIFuture.shareFeedData(any())).thenReturn(futureResponse);

        ListenableFuture<ShareFeedDataResponse> result = publisher.share(feedID, payload);

        verify(feedAPIFuture).shareFeedData(requestCaptor.capture());

        assertEquals("feed123", requestCaptor.getValue().getArgs().getFeedId().getId());
        assertEquals("test data", requestCaptor.getValue().getPayload().getSample().getData().toStringUtf8());
        assertSame(response, result.get());
    }

    private static class ConcretePublisher extends BaseTwin implements Publisher {
        ConcretePublisher(IoticsApi api) {
            super(api);
        }
    }
}
