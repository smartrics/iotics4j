package smartrics.iotics.host;

import com.iotics.api.*;

import java.util.List;

import java.util.List;
import java.util.List;

/**
 * Represents a data structure containing information about an IOTICS Digital Twin.
 */
public record TwinDataBag(
        List<Property> properties,   // List of properties describing the twin
        TwinID twinID,               // Unique identifier for the twin
        int feedsCount,              // Number of feed connections associated with the twin
        int inputsCount,             // Number of input connections associated with the twin
        GeoLocation location         // Geographical location of the twin
) {

    /**
     * Creates a {@code TwinDataBag} instance from a search response detailing twin information.
     * This static factory method extracts necessary details from a {@link SearchResponse.TwinDetails} object,
     * which is typically received from a search query within the system.
     *
     * @param twinDetails the twin details as received from a search operation
     * @return a new {@code TwinDataBag} instance populated with data from the search response
     */
    public static TwinDataBag from(SearchResponse.TwinDetails twinDetails) {
        return new TwinDataBag(
                twinDetails.getPropertiesList(),
                twinDetails.getTwinId(),
                twinDetails.getFeedsCount(),
                twinDetails.getInputsCount(),
                twinDetails.getLocation());
    }

    /**
     * Creates a {@code TwinDataBag} instance from a response to a twin description query.
     * This method parses a {@link DescribeTwinResponse} to construct a new {@code TwinDataBag},
     * extracting twin-specific details such as properties, counts of feeds and inputs, and location.
     *
     * @param twinDescription the response to a twin description request, typically used to fetch detailed
     *                        information about a specific twin
     * @return a new {@code TwinDataBag} instance populated with detailed information from the description response
     */
    public static TwinDataBag from(DescribeTwinResponse twinDescription) {
        return new TwinDataBag(
                twinDescription.getPayload().getResult().getPropertiesList(),
                twinDescription.getPayload().getTwinId(),
                twinDescription.getPayload().getResult().getFeedsCount(),
                twinDescription.getPayload().getResult().getInputsCount(),
                twinDescription.getPayload().getResult().getLocation());
    }
}
