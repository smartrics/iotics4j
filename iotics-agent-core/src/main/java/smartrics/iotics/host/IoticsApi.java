package smartrics.iotics.host;

import smartrics.iotics.host.wrappers.*;

import java.time.Duration;

/**
 * Interface defining the operations available for interacting with an IOTICS Host.
 * This interface provides access to various APIs that handle different aspects of interactions with Host,
 * including twin management, feed operations, input handling, interest registration, and metadata operations.
 * Implementations of this interface are expected to manage the underlying communication with the backed service.
 */
public interface IoticsApi {

    /**
     * Stops all ongoing operations or services within the specified timeout.
     * This method is intended to provide a graceful shutdown mechanism for implementations.
     *
     * @param timeout the maximum time to wait for active operations to terminate before forcibly stopping.
     */
    void stop(Duration timeout);

    TwinAPIFuture twinAPIFuture();

    FeedAPIFuture feedAPIFuture();

    FeedAPI feedAPI();

    InputAPIFuture inputAPIFuture();

    InterestAPI interestAPI();

    InterestAPIBlocking interestAPIBlocking();

    SearchAPI searchAPI();

    MetaAPI metaAPI();
}
