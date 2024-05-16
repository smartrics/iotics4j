package smartrics.iotics.connectors.twins;

import com.google.common.util.concurrent.ListenableFuture;
import com.iotics.api.ShareFeedDataRequest;
import smartrics.iotics.host.grpc.ListenableFutureAdapter;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Interface combining the capabilities of {@link Publisher} and {@link Mappable} to support sharing feed data.
 * This interface enables the dynamic generation and sharing of feed data requests based on the
 * current state of the object, utilizing mappings provided by the associated {@link Mapper}.
 */
public interface MappablePublisher extends Publisher, Mappable {

    /**
     * Shares feed data by generating {@link ShareFeedDataRequest} objects through the {@link Mapper} and
     * asynchronously sending these requests to the feed API. Each request is handled in a non-blocking
     * manner, and all share operations are coordinated to complete as a batch.
     *
     * @return a {@link CompletableFuture} that completes when all feed data share operations have completed.
     */
    default CompletableFuture<Void> share() {
        List<ShareFeedDataRequest> list = getMapper().getShareFeedDataRequest();
        Function<ShareFeedDataRequest, ListenableFuture<?>> function = this::share;
        return map(list, function);
    }

    /**
     * Maps a list of {@link ShareFeedDataRequest} objects to their respective asynchronous operations and
     * aggregates their futures into a single CompletableFuture that tracks the completion of all operations.
     *
     * @param list     The list of {@link ShareFeedDataRequest} objects to be processed.
     * @param function A function that maps each ShareFeedDataRequest to a {@link ListenableFuture} representing
     *                 the asynchronous operation to share the feed data.
     * @return a {@link CompletableFuture} that completes when all operations in the list are complete.
     */
    private CompletableFuture<Void> map(List<ShareFeedDataRequest> list, Function<ShareFeedDataRequest, ListenableFuture<?>> function) {
        List<CompletableFuture<?>> futures = list.stream()
                .map(function)
                .map((Function<ListenableFuture<?>, CompletableFuture<?>>) ListenableFutureAdapter::toCompletable)
                .toList();
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
}
