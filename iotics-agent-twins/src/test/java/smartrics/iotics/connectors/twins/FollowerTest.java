package smartrics.iotics.connectors.twins;

import com.iotics.api.FeedID;
import com.iotics.api.FetchInterestRequest;
import com.iotics.api.FetchInterestResponse;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import smartrics.iotics.host.IoticsApi;
import smartrics.iotics.host.wrappers.InterestAPI;
import smartrics.iotics.host.wrappers.InterestAPIBlocking;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class FollowerTest {

    private IoticsApi ioticsApi;
    private InterestAPIBlocking interestAPIBlocking;
    private InterestAPI interestAPI;
    private StreamObserver<FetchInterestResponse> responseObserver;
    private Follower follower;

    @BeforeEach
    void setUp() {

        ioticsApi = mock(IoticsApi.class);
        interestAPIBlocking = mock(InterestAPIBlocking.class);
        interestAPI = mock(InterestAPI.class);
        responseObserver = mock(StreamObserver.class);

        when(ioticsApi.interestAPIBlocking()).thenReturn(interestAPIBlocking);
        when(ioticsApi.interestAPI()).thenReturn(interestAPI);

        follower = new ConcreteFollower(ioticsApi);
    }

    @Test
    void testFollowBlocking() {
        FeedID feedId = FeedID.newBuilder().setId("feed123").build();
        ArgumentCaptor<FetchInterestRequest> requestCaptor = ArgumentCaptor.forClass(FetchInterestRequest.class);

        follower.follow(feedId);

        verify(interestAPIBlocking).fetchInterests(requestCaptor.capture());
        FetchInterestRequest capturedRequest = requestCaptor.getValue();
        assertNotNull(capturedRequest);
        assertEquals("feed123", capturedRequest.getArgs().getInterest().getFollowedFeedId().getId());
    }

    @Test
    void testFollowNoRetry() {
        FeedID feedId = FeedID.newBuilder().setId("feed123").build();
        ArgumentCaptor<FetchInterestRequest> requestCaptor = ArgumentCaptor.forClass(FetchInterestRequest.class);

        follower.followNoRetry(feedId, responseObserver);

        verify(interestAPI).fetchInterests(requestCaptor.capture(), eq(responseObserver));
        FetchInterestRequest capturedRequest = requestCaptor.getValue();

        // Assertions to check if the request sent is as expected
        assertNotNull(capturedRequest);
        assertEquals("feed123", capturedRequest.getArgs().getInterest().getFollowedFeedId().getId());
    }

    @Test
    void testFollowWithRetry() throws Exception {
        StreamObserver<FetchInterestResponse> internalObserver = fetchInterestResponseStreamObserver();

        // Create a sample response to simulate onNext
        FetchInterestResponse sampleResponse = FetchInterestResponse.newBuilder().build();

        // Simulate receiving a response
        internalObserver.onNext(sampleResponse);

        // Verify that the original observer received the response
        verify(responseObserver).onNext(sampleResponse);
    }

    @Test
    void testFollowWithRetryOnError() throws Exception {
        StreamObserver<FetchInterestResponse> internalObserver = fetchInterestResponseStreamObserver();

        Exception sampleException = new RuntimeException("Test exception");
        internalObserver.onError(sampleException);

        // Verify that the original observer received the error
        verify(responseObserver).onError(sampleException);
    }

    @Test
    void testFollowWithRetryOnCompleted() throws Exception {
        StreamObserver<FetchInterestResponse> internalObserver = fetchInterestResponseStreamObserver();

        internalObserver.onCompleted();

        // Verify that the original observer received the error
        verify(responseObserver).onCompleted();
    }

    private StreamObserver<FetchInterestResponse> fetchInterestResponseStreamObserver() throws InterruptedException {
        FeedID feedID = FeedID.newBuilder().setId("feed123").build();
        Follower.RetryConf retryConf = new Follower.RetryConf(Duration.ofSeconds(1), Duration.ofMillis(100), Duration.ofSeconds(1), Duration.ofSeconds(5));

        // Capture the StreamObserver used in followNoRetry within follow
        ArgumentCaptor<StreamObserver<FetchInterestResponse>> observerCaptor = ArgumentCaptor.forClass(StreamObserver.class);
        ArgumentCaptor<FetchInterestRequest> requestCaptor = ArgumentCaptor.forClass(FetchInterestRequest.class);

        follower.follow(feedID, retryConf, responseObserver);

        // mockito doesn't have time to register the call without this delay
        Thread.sleep(100);

        // Verify fetchInterests was called with the correct parameters
        verify(interestAPI).fetchInterests(requestCaptor.capture(), observerCaptor.capture());
        return observerCaptor.getValue();
    }

    private static class ConcreteFollower extends BaseTwin implements Follower {
        ConcreteFollower(IoticsApi api) {
            super(api);
        }
    }
}
