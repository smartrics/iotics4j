package smartrics.iotics.host.wrappers;

import com.google.common.util.concurrent.ListenableFuture;
import com.iotics.api.*;

/**
 * Internalised interface of IOTICS Twins API gRPC Service
 */
public interface TwinAPIFuture {
    ListenableFuture<ListAllTwinsResponse> listAllTwins(ListAllTwinsRequest request);

    ListenableFuture<DeleteTwinResponse> deleteTwin(DeleteTwinRequest request);

    ListenableFuture<DescribeTwinResponse> describeTwin(DescribeTwinRequest request);

    ListenableFuture<UpsertTwinResponse> upsertTwin(UpsertTwinRequest upsertRequest);
}
