package smartrics.iotics.host;

import com.iotics.api.FetchInterestResponse;
import com.iotics.api.SearchResponse;

import java.util.List;

/**
 * Immutable record that encapsulates data related to a specific feed of a digital twin.
 * This record combines the overarching twin data with specific feed details and the data
 * fetched based on interest subscriptions.
 */
public record FeedDataBag(
        TwinDataBag twinData,                       // The associated twin data encapsulating general twin information
        SearchResponse.FeedDetails feedDetails,     // Detailed information about the feed
        FetchInterestResponse fetchInterestResponse // Response data fetched based on feed interest
) {

    /**
     * Provides a string representation of the feed data bag, including detailed information about the twin,
     * the feed, and the fetched data. This method is particularly useful for logging or displaying the data
     * in a readable format. The data from the feed's interest response is converted to a UTF-8 string,
     * ensuring readability of any textual content.
     *
     * @return a string representation of the {@code FeedDataBag}, which includes twin details,
     *         feed identifier, and the fetched feed data.
     */
    @Override
    public String toString() {
        return "DataDetails{" +
                "twinData=" + twinData +
                ", feed=" + feedDetails.getFeedId() +
                ", data=" + fetchInterestResponse.getPayload().getFeedData().getData().toStringUtf8() +
                '}';
    }
}
