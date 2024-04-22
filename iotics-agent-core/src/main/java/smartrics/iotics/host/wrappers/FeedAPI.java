package smartrics.iotics.host.wrappers;

import com.iotics.api.*;
import io.grpc.stub.StreamObserver;

/**
 * Internalised interface of IOTICS Feeds API gRPC Service
 */
public interface FeedAPI {
    void createFeed(CreateFeedRequest request, StreamObserver<CreateFeedResponse> responseObserver);

    void deleteFeed(DeleteFeedRequest request, StreamObserver<DeleteFeedResponse> responseObserver);

    void updateFeed(UpdateFeedRequest request, StreamObserver<UpdateFeedResponse> responseObserver);

    void listAllFeeds(ListAllFeedsRequest request, StreamObserver<ListAllFeedsResponse> responseObserver);

    void describeFeed(DescribeFeedRequest request, StreamObserver<DescribeFeedResponse> responseObserver);

    void shareFeedData(ShareFeedDataRequest request, StreamObserver<ShareFeedDataResponse> observer) ;

    StreamObserver<ShareFeedDataRequest> streamFeedData(StreamObserver<ShareFeedDataResponse> response);
}
