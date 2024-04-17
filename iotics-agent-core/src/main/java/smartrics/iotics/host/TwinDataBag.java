package smartrics.iotics.host;

import com.iotics.api.*;

import java.util.List;

public record TwinDataBag(List<Property> properties,
                          TwinID twinID,
                          int feedsCount, int inputsCount,
                          GeoLocation location) {

    public static TwinDataBag from(SearchResponse.TwinDetails twinDetails) {
        return new TwinDataBag(twinDetails.getPropertiesList(), twinDetails.getTwinId(), twinDetails.getFeedsCount(), twinDetails.getInputsCount(), twinDetails.getLocation());
    }

    public static TwinDataBag from(DescribeTwinResponse twinDescription) {
        return new TwinDataBag(twinDescription.getPayload().getResult().getPropertiesList(), twinDescription.getPayload().getTwinId(), twinDescription.getPayload().getResult().getFeedsCount(), twinDescription.getPayload().getResult().getInputsCount(), twinDescription.getPayload().getResult().getLocation());
    }

}
