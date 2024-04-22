package smartrics.iotics.host;

import io.grpc.ManagedChannel;
import smartrics.iotics.host.grpc.HostConnection;
import smartrics.iotics.host.wrappers.*;
import smartrics.iotics.identity.SimpleIdentityManager;

import java.time.Duration;
import java.util.Timer;

/**
 * The IoticsApiImpl implements the {@code IoticsApi} interface and manages the setup and interactions with IOTICS host gRPC services.
 * This includes managing channels, stubs for different API services, and the identity management necessary
 * for authentication and authorization.
 *
 * <p>The API delegates to an underlying gRPC client the respective service calls.
 *
 * <p>Currently this implementation is very basic and doesn't handle reconnects or any other form or reliability.
 */
public class IoticsApiImpl implements IoticsApi {

    private final TwinAPIFuture twinAPIFuture;
    private final FeedAPIFuture feedAPIFuture;
    private final FeedAPI feedAPI;
    private final InputAPIFuture inputAPIFuture;
    private final InterestAPI interestAPI;
    private final InterestAPIBlocking interestAPIBlocking;
    private final SearchAPI searchAPI;
    private final MetaAPI metaAPI;
    private final HostConnection connection;

    /**
     * Constructs an IoticsApi instance configured to interact with a specific Host.
     *
     * @param connection The connection to IOTICS Host.
     */
    public IoticsApiImpl(HostConnection connection) {
        this.connection = connection;
        ManagedChannel channel = connection.getGrpcChannel();
        this.twinAPIFuture = WrapperFactory.newTwinAPIFuture(channel);
        this.feedAPIFuture = WrapperFactory.newFeedAPIFuture(channel);
        this.feedAPI = WrapperFactory.newFeedApi(channel);
        this.inputAPIFuture = WrapperFactory.newInputAPIFuture(channel);
        this.metaAPI = WrapperFactory.newMetaAPI(channel);
        this.interestAPI = WrapperFactory.newInterestAPI(channel);
        this.interestAPIBlocking = WrapperFactory.newInterestAPIBlocking(channel);
        this.searchAPI = WrapperFactory.newSearchAPI(channel);
    }

    /**
     * Stops all activities managed by this instance, including shutting down gRPC services and cancelling the timer.
     *
     * @param timeout The maximum time to wait for the services to shut down.
     */
    @Override
    public void stop(Duration timeout) {
        try {
            connection.shutdown(timeout);
        } catch (InterruptedException e) {
            // Preserve interrupt status
            Thread.currentThread().interrupt();
            // Consider adding more context or handling differently
            throw new RuntimeException("Failed to shut down connection within the provided timeout: " + timeout, e);
        }

    }

    @Override
    public TwinAPIFuture twinAPIFuture() {
        return twinAPIFuture;
    }

    @Override
    public FeedAPIFuture feedAPIFuture() {
        return feedAPIFuture;
    }

    @Override
    public FeedAPI feedAPI() {
        return feedAPI;
    }

    @Override
    public InputAPIFuture inputAPIFuture() {
        return inputAPIFuture;
    }

    @Override
    public InterestAPI interestAPI() {
        return interestAPI;
    }

    @Override
    public InterestAPIBlocking interestAPIBlocking() {
        return interestAPIBlocking;
    }

    @Override
    public SearchAPI searchAPI() {
        return searchAPI;
    }

    @Override
    public MetaAPI metaAPI() {
        return metaAPI;
    }

}
