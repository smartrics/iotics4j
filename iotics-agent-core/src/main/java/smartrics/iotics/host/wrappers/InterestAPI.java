package smartrics.iotics.host.wrappers;

import com.iotics.api.FetchInterestRequest;
import com.iotics.api.FetchInterestResponse;
import com.iotics.api.FetchLastStoredRequest;
import io.grpc.stub.StreamObserver;

/**
 * Internalised interface of IOTICS Interests API gRPC Service
 */
public interface InterestAPI {

    /**
     * Follows a feed backed by the interest in request
     * @param request the request specifying the feed to follow
     * @param observer the observer of the feed shares received
     */
    void fetchInterests(FetchInterestRequest request, StreamObserver<FetchInterestResponse> observer);

    void fetchLastStored(FetchLastStoredRequest request, StreamObserver<FetchInterestResponse> observer);
}
