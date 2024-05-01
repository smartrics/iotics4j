package smartrics.iotics.connectors.twins;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import com.iotics.api.FeedData;
import com.iotics.api.FeedID;
import com.iotics.api.ShareFeedDataRequest;
import com.iotics.api.ShareFeedDataResponse;
import io.grpc.stub.StreamObserver;
import org.jetbrains.annotations.NotNull;
import smartrics.iotics.host.Builders;

import java.nio.charset.StandardCharsets;

/**
 * Interface for publishing data to feeds, extending the capabilities to identify entities and interact with APIs.
 * Provides methods for both one-time sharing and continuous streaming of feed data.
 */
public interface Publisher extends Identifiable, ApiUser {

    /**
     * Shares data to a feed using a specified {@link ShareFeedDataRequest}. This method sends the request asynchronously
     * to the feed API and returns a future that will complete with the response from the operation.
     *
     * @param request The {@link ShareFeedDataRequest} containing all the details necessary for the data sharing operation.
     * @return a ListenableFuture containing the ShareFeedDataResponse from the sharing operation.
     */
    default ListenableFuture<ShareFeedDataResponse> share(ShareFeedDataRequest request) {
        return ioticsApi().feedAPIFuture().shareFeedData(request);
    }

    /**
     * Opens a stream for continuously sending feed data and receiving responses. This method establishes a
     * bi-directional stream using a {@link StreamObserver} for both sending requests and handling responses.
     *
     * @param response The {@link StreamObserver} to handle incoming responses to the streamed data.
     * @return a {@link StreamObserver} for sending feed data requests to the API.
     */
    default StreamObserver<ShareFeedDataRequest> stream(StreamObserver<ShareFeedDataResponse> response) {
        return ioticsApi().feedAPI().streamFeedData(response);
    }

    /**
     * Convenience method to share data to a feed identified by {@link FeedID} with a specific payload.
     * This method constructs a {@link ShareFeedDataRequest} and then delegates to the share method.
     *
     * @param feedID  The identifier of the feed to which the data is to be shared.
     * @param payload The data payload to be shared.
     * @return a {@link ListenableFuture} containing the response from the sharing operation.
     */
    default ListenableFuture<ShareFeedDataResponse> share(FeedID feedID, String payload) {
        ShareFeedDataRequest request = newRequest(feedID, payload);
        return share(request);
    }

    /**
     * Constructs a new ShareFeedDataRequest based on the provided feed ID and payload.
     *
     * @param feedID  The identifier of the feed to which the data will be shared.
     * @param payload The data payload to be shared as a string.
     * @return a {@link ShareFeedDataRequest} configured with the specified feed ID and payload.
     */
    @NotNull
    default ShareFeedDataRequest newRequest(FeedID feedID, String payload) {
        return ShareFeedDataRequest.newBuilder()
                .setHeaders(Builders.newHeadersBuilder(getAgentIdentity()).build())
                .setPayload(ShareFeedDataRequest.Payload.newBuilder()
                        .setSample(FeedData.newBuilder()
                                .setData(ByteString.copyFrom(payload.getBytes(StandardCharsets.UTF_8)))
                                .build())
                        .build())
                .setArgs(ShareFeedDataRequest.Arguments.newBuilder()
                        .setFeedId(feedID)
                        .build())
                .build();
    }
}

