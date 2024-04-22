package smartrics.iotics.host.wrappers;

import com.google.common.util.concurrent.ListenableFuture;
import com.iotics.api.ShareFeedDataRequest;
import com.iotics.api.ShareFeedDataResponse;

/**
 * Internalised interface of IOTICS Async Feeds API gRPC Service
 */
public interface FeedAPIFuture {
    ListenableFuture<ShareFeedDataResponse> shareFeedData(ShareFeedDataRequest request);
}
