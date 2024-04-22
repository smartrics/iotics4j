package smartrics.iotics.connectors.twins;

import com.google.common.util.concurrent.ListenableFuture;
import com.iotics.api.UpsertTwinRequest;
import com.iotics.api.UpsertTwinResponse;

public interface MappableMaker extends Maker, Mappable {

    default ListenableFuture<UpsertTwinResponse> upsert() {
        UpsertTwinRequest upsertRequest = getMapper().getUpsertTwinRequest();
        return ioticsApi().twinAPIFuture().upsertTwin(upsertRequest);
    }

}
