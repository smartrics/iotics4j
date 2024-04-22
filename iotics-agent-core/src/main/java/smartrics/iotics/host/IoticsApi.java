package smartrics.iotics.host;

import smartrics.iotics.host.wrappers.*;

import java.time.Duration;

public interface IoticsApi {
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
