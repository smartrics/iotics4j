package smartrics.iotics.host;

import com.google.common.util.concurrent.ListenableFuture;
import com.iotics.api.*;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import smartrics.iotics.host.wrappers.*;

import java.util.Iterator;

/**
 * Factory class for creating wrappers around various gRPC service stubs.
 * This class simplifies the creation and usage of service APIs by providing
 * methods that return higher-level abstractions of gRPC stubs tailored to
 * specific service functionalities within an IOTICS Host. Each method returns
 * an implementation of a service-specific API that internally uses a gRPC stub
 * to communicate with the respective services over a given {@link ManagedChannel}.
 */
final class WrapperFactory {
    static TwinAPIFuture newTwinAPIFuture(ManagedChannel channel) {
        final TwinAPIGrpc.TwinAPIFutureStub delegate = TwinAPIGrpc.newFutureStub(channel);
        return new TwinAPIFuture() {

            @Override
            public ListenableFuture<ListAllTwinsResponse> listAllTwins(ListAllTwinsRequest request) {
                return delegate.listAllTwins(request);
            }

            @Override
            public ListenableFuture<DeleteTwinResponse> deleteTwin(DeleteTwinRequest request) {
                return delegate.deleteTwin(request);
            }

            @Override
            public ListenableFuture<DescribeTwinResponse> describeTwin(DescribeTwinRequest request) {
                return delegate.describeTwin(request);
            }

            @Override
            public ListenableFuture<UpsertTwinResponse> upsertTwin(UpsertTwinRequest upsertRequest) {
                return delegate.upsertTwin(upsertRequest);
            }
        };
    }

    public static FeedAPIFuture newFeedAPIFuture(ManagedChannel channel) {
        final FeedAPIGrpc.FeedAPIFutureStub delegate = FeedAPIGrpc.newFutureStub(channel);
        return new FeedAPIFuture() {
            @Override
            public ListenableFuture<ShareFeedDataResponse> shareFeedData(ShareFeedDataRequest request) {
                return delegate.shareFeedData(request);
            }
        };
    }

    public static FeedAPI newFeedApi(ManagedChannel channel) {
        final FeedAPIGrpc.FeedAPIStub delegate = FeedAPIGrpc.newStub(channel);
        return new FeedAPI() {

            @Override
            public void createFeed(CreateFeedRequest request, StreamObserver<CreateFeedResponse> responseObserver) {
                delegate.createFeed(request, responseObserver);
            }

            @Override
            public void deleteFeed(DeleteFeedRequest request, StreamObserver<DeleteFeedResponse> responseObserver) {
                delegate.deleteFeed(request, responseObserver);
            }

            @Override
            public void updateFeed(UpdateFeedRequest request, StreamObserver<UpdateFeedResponse> responseObserver) {
                delegate.updateFeed(request, responseObserver);
            }

            @Override
            public void listAllFeeds(ListAllFeedsRequest request, StreamObserver<ListAllFeedsResponse> responseObserver) {
                delegate.listAllFeeds(request, responseObserver);
            }

            @Override
            public void describeFeed(DescribeFeedRequest request, StreamObserver<DescribeFeedResponse> responseObserver) {
                delegate.describeFeed(request, responseObserver);
            }

            @Override
            public void shareFeedData(ShareFeedDataRequest request, StreamObserver<ShareFeedDataResponse> observer) {
                delegate.shareFeedData(request, observer);
            }

            @Override
            public StreamObserver<ShareFeedDataRequest> streamFeedData(StreamObserver<ShareFeedDataResponse> response) {
                return delegate.streamFeedData(response);
            }

        };
    }

    public static InputAPIFuture newInputAPIFuture(ManagedChannel channel) {
        final InputAPIGrpc.InputAPIStub delegate = InputAPIGrpc.newStub(channel);
        return new InputAPIFuture() {

            @Override
            public void receiveInputMessages(ReceiveInputMessageRequest request, StreamObserver<ReceiveInputMessageResponse> responseObserver) {
                delegate.receiveInputMessages(request, responseObserver);
            }

            @Override
            public void describeInput(DescribeInputRequest request, StreamObserver<DescribeInputResponse> observer) {
                delegate.describeInput(request, observer);
            }

            @Override
            public void createInput(CreateInputRequest request, StreamObserver<CreateInputResponse> observer) {
                delegate.createInput(request, observer);
            }
            @Override
            public void deleteInput(DeleteInputRequest request, StreamObserver<DeleteInputResponse> observer) {
                delegate.deleteInput(request, observer);
            }
        };
    }

    public static MetaAPI newMetaAPI(ManagedChannel channel) {
        final MetaAPIGrpc.MetaAPIStub delegate = MetaAPIGrpc.newStub(channel);

        return new MetaAPI() {
            @Override
            public void sparqlQuery(SparqlQueryRequest request, StreamObserver<SparqlQueryResponse> observer) {
                delegate.sparqlQuery(request, observer);
            }
            @Override
            public void sparqlUpdate(SparqlUpdateRequest request, StreamObserver<SparqlUpdateResponse> observer) {
                delegate.sparqlUpdate(request, observer);
            }
        };
    }

    public static InterestAPI newInterestAPI(ManagedChannel channel) {
        final InterestAPIGrpc.InterestAPIStub delegate = InterestAPIGrpc.newStub(channel);
        return new InterestAPI() {
            @Override
            public void fetchInterests(FetchInterestRequest request, StreamObserver<FetchInterestResponse> observer) {
                delegate.fetchInterests(request, observer);
            }

            @Override
            public void fetchLastStored(FetchLastStoredRequest request, StreamObserver<FetchInterestResponse> observer) {
                delegate.fetchLastStored(request, observer);
            }

        };
    }

    public static InterestAPIBlocking newInterestAPIBlocking(ManagedChannel channel) {
        final InterestAPIGrpc.InterestAPIBlockingStub delegate = InterestAPIGrpc.newBlockingStub(channel);
        return new InterestAPIBlocking() {
            @Override
            public Iterator<FetchInterestResponse> fetchInterests(FetchInterestRequest request) {
                return delegate.fetchInterests(request);
            }

            @Override
            public FetchInterestResponse fetchLastStored(FetchLastStoredRequest request) {
                return delegate.fetchLastStored(request);
            }

            @Override
            public SendInputMessageResponse sendInputMessage(SendInputMessageRequest request) {
                return delegate.sendInputMessage(request);
            }
        };
    }

    public static SearchAPI newSearchAPI(ManagedChannel channel) {
        final SearchAPIGrpc.SearchAPIStub delegate = SearchAPIGrpc.newStub(channel);
        return new SearchAPI() {
            @Override
            public void synchronousSearch(SearchRequest request, StreamObserver<SearchResponse> obs) {
                delegate.synchronousSearch(request, obs);
            }

            @Override
            public void dispatchSearchRequest(SearchRequest request, StreamObserver<DispatchSearchResponse> obs) {
                delegate.dispatchSearchRequest(request, obs);
            }

            @Override
            public void dispatchSearchRequest(SubscriptionHeaders headers, StreamObserver<SearchResponse> obs) {
                delegate.receiveAllSearchResponses (headers, obs);
            }

        };
    }


}
