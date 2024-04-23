package smartrics.iotics.connectors.twins;

import com.iotics.api.ShareFeedDataRequest;
import com.iotics.api.UpsertTwinRequest;

import java.util.List;

/**
 * Interface defining the mapping logic for converting object state into specific gRPC API requests.
 * Implementors of this interface provide methods to generate request objects needed for various operations,
 * such as upserting twins or sharing feed data. This interface is typically used in conjunction with other interfaces
 * requiring the definition of {@link UpsertTwinRequest} and {@link ShareFeedDataRequest}.
 *
 * <p>Each method in this interface is responsible for producing a fully formed request object
 * based on the current state of the implementing object or system.
 */
public interface Mapper {

    /**
     * Generates an {@link UpsertTwinRequest} for creating or updating a twin's data. This request is
     * constructed based on the internal state of the twin and is ready to be sent to a twin management API.
     *
     * @return An {@link UpsertTwinRequest} that encapsulates all necessary data for the upsert operation.
     */
    UpsertTwinRequest getUpsertTwinRequest();

    /**
     * Produces a list of {@link ShareFeedDataRequest}s necessary for sharing data associated with the twin's feeds.
     * Each request in the list represents a single operation to share data from one of the twin's feeds,
     * constructed based on the current state and configuration of the feeds.
     *
     * @return A list of {@link ShareFeedDataRequest} objects, each configured for sharing data from a specific feed.
     */
    List<ShareFeedDataRequest> getShareFeedDataRequest();
}

