package smartrics.iotics.connectors.twins;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.iotics.api.DescribeTwinRequest;
import com.iotics.api.DescribeTwinResponse;
import com.iotics.api.TwinID;
import io.grpc.stub.StreamObserver;
import smartrics.iotics.host.Builders;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Interface for describing twins using the Iotics API. Extends Identifiable and ApiUser to provide identity
 * management and API access functionalities. This interface supports methods for retrieving detailed information
 * about twins in both one-off and periodic manners.
 */
public interface Describer extends Identifiable, ApiUser {

    /**
     * Schedules periodic retrieval of twin details from the Iotics API based on a specified twin ID.
     * Uses a given scheduler to execute the retrieval at a fixed rate defined by initialDelay and pollingFrequency.
     * Results are communicated through a StreamObserver.
     *
     * @param twinID           The unique identifier of the twin to describe.
     * @param scheduler        The scheduled executor service to manage timing of retrieval tasks.
     * @param initialDelay     The initial delay before the first retrieval is executed.
     * @param pollingFrequency The frequency with which twin details are retrieved.
     * @param result           The observer to handle responses or failures of the retrieval tasks.
     */
    default void describe(TwinID twinID, ScheduledExecutorService scheduler, Duration initialDelay, Duration pollingFrequency, StreamObserver<DescribeTwinResponse> result) {
        scheduler.scheduleAtFixedRate(() -> {
            ListenableFuture<DescribeTwinResponse> f = describe(twinID);
            Futures.addCallback(f, new FutureCallback<>() {
                @Override
                public void onSuccess(DescribeTwinResponse describeTwinResponse) {
                    result.onNext(describeTwinResponse);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    result.onError(throwable);
                }
            }, MoreExecutors.directExecutor());
        }, initialDelay.toMillis(), pollingFrequency.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Retrieves the details of the twin associated with the current twin's identity.
     *
     * @return A future that completes with the twin's detailed response or an error if failed.
     */
    default ListenableFuture<DescribeTwinResponse> describe() {
        return describe(TwinID.newBuilder().setId(getMyIdentity().did()).build());
    }

    /**
     * Retrieves the details of a specified twin using its unique identifier.
     *
     * @param twinID The unique identifier of the twin to describe.
     * @return A future that completes with the twin's detailed response or an error if failed.
     */
    default ListenableFuture<DescribeTwinResponse> describe(TwinID twinID) {
        return ioticsApi().twinAPIFuture().describeTwin(DescribeTwinRequest.newBuilder()
                .setHeaders(Builders.newHeadersBuilder(getAgentIdentity())
                        .build())
                .setArgs(DescribeTwinRequest.Arguments.newBuilder()
                        .setTwinId(TwinID.newBuilder()
                                .setId(twinID.getId())
                                .setHostId(twinID.getHostId())
                                .build())
                        .build())
                .build());
    }
}
