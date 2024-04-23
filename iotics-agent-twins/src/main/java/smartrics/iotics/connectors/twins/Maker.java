package smartrics.iotics.connectors.twins;

import com.google.common.util.concurrent.*;
import com.iotics.api.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.jetbrains.annotations.NotNull;
import smartrics.iotics.host.Builders;

import java.util.concurrent.Executor;

/**
 * Interface for managing twin entities, extending the capabilities to identify and describe twins.
 * Provides methods to upsert, delete, and conditionally create twins if they are not already present.
 */
public interface Maker extends Identifiable, Describer {

    /**
     * Attempts to upsert (update or insert) a twin based on the current state or provided data.
     * This operation is asynchronous and returns a future that will complete with the response from the upsert operation.
     *
     * @return a ListenableFuture containing the UpsertTwinResponse from the operation
     */
    ListenableFuture<UpsertTwinResponse> upsert();

    /**
     * Provides a default executor that runs tasks in the calling thread, simplifying the execution
     * of asynchronous callbacks and future tasks within the same thread context.
     *
     * @return an Executor that runs each task in the thread that invokes the executor
     */
    default Executor getExecutor() {
        return MoreExecutors.directExecutor();
    }

    /**
     * Deletes the twin associated with the current entity's identity. This method constructs
     * a DeleteTwinRequest and sends it to the server asynchronously.
     *
     * @return a ListenableFuture containing the DeleteTwinResponse from the delete operation
     */
    default ListenableFuture<DeleteTwinResponse> delete() {
        return ioticsApi().twinAPIFuture().deleteTwin(DeleteTwinRequest.newBuilder()
                .setHeaders(Builders.newHeadersBuilder(getAgentIdentity().did()).build())
                .setArgs(DeleteTwinRequest.Arguments.newBuilder()
                        .setTwinId(TwinID.newBuilder().setId(getMyIdentity().did()).build())
                        .build())
                .build());
    }

    /**
     * Ensures that a twin exists for the current entity by attempting to describe it first and
     * if not found, upserting it.
     *
     * @return a ListenableFuture containing the TwinID of the described or upserted twin
     */
    default ListenableFuture<TwinID> makeIfAbsent() {
        SettableFuture<TwinID> future = SettableFuture.create();
        Futures.addCallback(describe(), describeCallback(this, future, getExecutor()), getExecutor());
        return future;
    }

    /**
     * Callback for handling responses from the describe operation, potentially triggering an upsert
     * if the twin is not found.
     *
     * @param maker    the instance of Maker to use for upserting if necessary
     * @param future   the future to be completed with the result
     * @param executor the executor to run asynchronous operations
     * @return a callback that processes the result of the describe operation
     */
    private static FutureCallback<DescribeTwinResponse> describeCallback(Maker maker, SettableFuture<TwinID> future, Executor executor) {
        return new FutureCallback<>() {
            @Override
            public void onSuccess(DescribeTwinResponse describeTwinResponse) {
                future.set(describeTwinResponse.getPayload().getTwinId());
            }

            @Override
            public void onFailure(@NotNull Throwable thrown) {
                if ((thrown instanceof StatusRuntimeException sre) && sre.getStatus().getCode() == Status.Code.NOT_FOUND) {
                    Futures.addCallback(maker.upsert(), makeCallback(future), executor);
                } else {
                    future.setException(thrown);
                }
            }
        };
    }

    /**
     * Callback for processing the upsert response and completing the future with the twin ID.
     *
     * @param future the future to be completed with the twin ID
     * @return a callback that handles the result of the upsert operation
     */
    @NotNull
    private static FutureCallback<UpsertTwinResponse> makeCallback(SettableFuture<TwinID> future) {
        return new FutureCallback<>() {
            @Override
            public void onSuccess(UpsertTwinResponse upsertTwinResponse) {
                future.set(upsertTwinResponse.getPayload().getTwinId());
            }

            @Override
            public void onFailure(@NotNull Throwable throwable) {
                future.setException(throwable);
            }
        };
    }
}
