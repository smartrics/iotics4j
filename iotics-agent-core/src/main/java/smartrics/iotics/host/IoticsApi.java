package smartrics.iotics.host;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.iotics.api.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import smartrics.iotics.host.grpc.HostConnection;
import smartrics.iotics.host.grpc.HostManagedChannelBuilderFactory;
import smartrics.iotics.identity.IdentityManager;
import smartrics.iotics.identity.SimpleConfig;
import smartrics.iotics.identity.SimpleIdentityManager;

import java.time.Duration;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The IoticsApi class manages the setup and interactions with Iotics gRPC services.
 * This includes managing channels, stubs for different API services, and the identity management necessary
 * for authentication and authorization.
 *
 * <p>It encapsulates the process of creating and managing connections to the Iotics space through gRPC, handling
 * identity via {@link SimpleIdentityManager}, and scheduling token renewals with a {@link Timer}.
 */
public class IoticsApi {

    private final TwinAPIGrpc.TwinAPIFutureStub twinAPIFutureStub;
    private final FeedAPIGrpc.FeedAPIFutureStub feedAPIFutureStub;
    private final FeedAPIGrpc.FeedAPIStub feedAPIStub;
    private final InputAPIGrpc.InputAPIFutureStub inputAPIFutureStub;
    private final InterestAPIGrpc.InterestAPIStub interestAPIStub;
    private final InterestAPIGrpc.InterestAPIBlockingStub interestAPIBlockingStub;
    private final SearchAPIGrpc.SearchAPIStub searchAPIStub;
    private final MetaAPIGrpc.MetaAPIStub metaAPIStub;
    private final HostConnection connection;

    /**
     * Constructs an IoticsApi instance configured to interact with a specific Host.
     *
     * @param connection The connection to IOTICS Host.
     */
    public IoticsApi(HostConnection connection) {
        this.connection = connection;
        ManagedChannel channel = connection.getGrpcChannel();
        this.twinAPIFutureStub = TwinAPIGrpc.newFutureStub(channel);
        this.feedAPIFutureStub = FeedAPIGrpc.newFutureStub(channel);
        this.feedAPIStub = FeedAPIGrpc.newStub(channel);
        this.inputAPIFutureStub = InputAPIGrpc.newFutureStub(channel);
        this.metaAPIStub = MetaAPIGrpc.newStub(channel);
        this.interestAPIStub = InterestAPIGrpc.newStub(channel);
        this.interestAPIBlockingStub = InterestAPIGrpc.newBlockingStub(channel);
        this.searchAPIStub = SearchAPIGrpc.newStub(channel);
    }

    /**
     * Stops all activities managed by this instance, including shutting down gRPC services and cancelling the timer.
     *
     * @param timeout The maximum time to wait for the services to shut down.
     */
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

    public TwinAPIGrpc.TwinAPIFutureStub twinAPIFutureStub() {
        return twinAPIFutureStub;
    }

    public FeedAPIGrpc.FeedAPIFutureStub feedAPIFutureStub() {
        return feedAPIFutureStub;
    }

    public FeedAPIGrpc.FeedAPIStub feedAPIStub() {
        return feedAPIStub;
    }

    public InputAPIGrpc.InputAPIFutureStub inputAPIFutureStub() {
        return inputAPIFutureStub;
    }

    public InterestAPIGrpc.InterestAPIStub interestAPIStub() {
        return interestAPIStub;
    }

    public InterestAPIGrpc.InterestAPIBlockingStub interestAPIBlockingStub() {
        return interestAPIBlockingStub;
    }

    public SearchAPIGrpc.SearchAPIStub searchAPIStub() {
        return searchAPIStub;
    }

    public MetaAPIGrpc.MetaAPIStub metaAPIStub() {
        return metaAPIStub;
    }

}
