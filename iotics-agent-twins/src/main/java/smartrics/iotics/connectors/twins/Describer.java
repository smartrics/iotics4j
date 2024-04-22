package smartrics.iotics.connectors.twins;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.iotics.api.DescribeTwinRequest;
import com.iotics.api.DescribeTwinResponse;
import com.iotics.api.TwinID;
import io.grpc.stub.StreamObserver;
import org.jetbrains.annotations.NotNull;
import smartrics.iotics.host.Builders;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public interface Describer extends Identifiable, ApiUser {

    default void describe(TwinID twinID, ScheduledExecutorService scheduler, Duration initialDelay, Duration pollingFrequency, StreamObserver<DescribeTwinResponse> result) {
        scheduler.scheduleAtFixedRate(() -> {
            ListenableFuture<DescribeTwinResponse> f = describe(twinID);
            Futures.addCallback(f, new FutureCallback<>() {
                @Override
                public void onSuccess(DescribeTwinResponse describeTwinResponse) {
                    result.onNext(describeTwinResponse);
                }

                @Override
                public void onFailure(@NotNull Throwable throwable) {
                    result.onError(throwable);
                }
            }, MoreExecutors.directExecutor());
        }, initialDelay.toMillis(), pollingFrequency.toMillis(), TimeUnit.MILLISECONDS);

    }

    default ListenableFuture<DescribeTwinResponse> describe() {
        return describe(TwinID.newBuilder().setId(getMyIdentity().did()).build());
    }

    default ListenableFuture<DescribeTwinResponse> describe(TwinID twinID) {
        return ioticsApi().twinAPIFuture().describeTwin(DescribeTwinRequest.newBuilder()
                .setHeaders(Builders.newHeadersBuilder(getAgentIdentity().did())
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
