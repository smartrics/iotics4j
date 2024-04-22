package smartrics.iotics.host.wrappers;

import com.iotics.api.DispatchSearchResponse;
import com.iotics.api.SearchRequest;
import com.iotics.api.SearchResponse;
import com.iotics.api.SubscriptionHeaders;
import io.grpc.stub.StreamObserver;

/**
 * Internalised interface of IOTICS Search API gRPC Service
 */
public interface SearchAPI {
    void synchronousSearch(SearchRequest request, StreamObserver<SearchResponse> obs);

    void dispatchSearchRequest(SearchRequest request, StreamObserver<DispatchSearchResponse> obs);

    void dispatchSearchRequest(SubscriptionHeaders headers, StreamObserver<SearchResponse> obs);
}
