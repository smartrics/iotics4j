package smartrics.iotics.connectors.twins;

import com.google.protobuf.BoolValue;
import com.iotics.api.*;
import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import dev.failsafe.RetryPolicyBuilder;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import smartrics.iotics.host.Builders;
import smartrics.iotics.host.wrappers.InterestAPI;
import smartrics.iotics.identity.Identity;

import java.time.Duration;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for following feed data in a resilient manner, with built-in support for retries
 * and cancellations. This interface extends Identifiable for identity management and ApiUser
 * for API interaction, allowing for the implementation of robust data consumption patterns.
 */
public interface Follower extends Identifiable, ApiUser {

    /**
     * Default retry policy for following operations, tailored to handle specific gRPC status exceptions
     * and configured with predefined delay and jitter.
     */
    RetryPolicyBuilder<Object> DEF_RETRY_POLICY_FOLLOW_BUILDER = RetryPolicy.builder().handle(StatusRuntimeException.class).handleIf(e -> {
        StatusRuntimeException sre = (StatusRuntimeException) e;
        return sre.getStatus() == Status.DEADLINE_EXCEEDED || sre.getStatus() == Status.UNAUTHENTICATED || sre.getStatus() == Status.UNAVAILABLE;
    }).withDelay(Duration.ofSeconds(10)).withMaxRetries(-1).withJitter(Duration.ofMillis(3000));

    /**
     * Begins following a feed identified by a FeedID, using a blocking API call to fetch
     * interest data indefinitely.
     *
     * @param feedId the identifier of the feed to follow
     * @return an iterator of FetchInterestResponse providing continuous feed data
     */
    default Iterator<FetchInterestResponse> follow(FeedID feedId) {
        FetchInterestRequest request = newRequest(feedId);
        return ioticsApi().interestAPIBlocking().fetchInterests(request);
    }

    /**
     * Begins following a feed without applying any retry mechanisms, using non-blocking calls
     * to handle asynchronous feed data.
     *
     * @param feedId   the identifier of the feed to follow
     * @param observer the observer to handle responses and errors
     */
    default void followNoRetry(FeedID feedId, StreamObserver<FetchInterestResponse> observer) {
        FetchInterestRequest request = newRequest(feedId);
        InterestAPI interestAPI = ioticsApi().interestAPI();
        interestAPI.fetchInterests(request, observer);
    }

    /**
     * Begins following a feed with a customizable retry configuration.
     *
     * @param feedID    the identifier of the feed to follow
     * @param retryConf configuration settings for retry behavior
     * @param observer  the observer to handle responses and errors
     */
    default void follow(FeedID feedID, RetryConf retryConf, StreamObserver<FetchInterestResponse> observer) {
        Failsafe.with(DEF_RETRY_POLICY_FOLLOW_BUILDER.withJitter(retryConf.jitter).withDelay(retryConf.delay).build()).runAsync(() -> {
            CompletableFuture<Void> result = new CompletableFuture<>();
            followNoRetry(feedID, new StreamObserver<>() {
                @Override
                public void onNext(FetchInterestResponse value) {
                    observer.onNext(value);
                }

                @Override
                public void onError(Throwable t) {
                    observer.onError(t);
                    result.completeExceptionally(t);
                }

                @Override
                public void onCompleted() {
                    observer.onCompleted();
                    result.complete(null);
                }
            });
            try {
                // blocks until the future is completed
                result.get();
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                throw e.getCause();
            }
        });
    }

    /**
     * Provides a cancellable context for ongoing follow operations, allowing for graceful
     * termination of these operations when necessary.
     *
     * @return a cancellable context to manage operation cancellation
     */
    default Context.CancellableContext getCancellableContext() {
        return Context.current().withCancellation();
    }

    /**
     * Helper method to create a FetchInterestRequest based on the feed ID.
     *
     * @param feedId the identifier of the feed
     * @return a new fetch interest request configured with the given feed ID
     */
    private FetchInterestRequest newRequest(FeedID feedId) {
        try {
            Identity agentIdentity = getAgentIdentity();
            return FetchInterestRequest.newBuilder().setHeaders(Builders.newHeadersBuilder(agentIdentity).build()).setFetchLastStored(BoolValue.newBuilder().setValue(true).build()).setArgs(FetchInterestRequest.Arguments.newBuilder().setInterest(Interest.newBuilder().setFollowerTwinId(TwinID.newBuilder().setId(getMyIdentity().did())).setFollowedFeedId(feedId).build()).build()).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Configuration settings for retry behavior when following feeds, including initial delay,
     * jitter to prevent synchronization, backoff delay, and maximum backoff delay.
     */
    record RetryConf(Duration delay, Duration jitter, Duration backoffDelay, Duration backoffMaxDelay) {
    }
}
