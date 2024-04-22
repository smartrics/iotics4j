package smartrics.iotics.host;

import com.iotics.api.*;

import java.time.Duration;

public interface IoticsApi {
    void stop(Duration timeout);

    TwinAPIGrpc.TwinAPIFutureStub twinAPIFutureStub();

    FeedAPIGrpc.FeedAPIFutureStub feedAPIFutureStub();

    FeedAPIGrpc.FeedAPIStub feedAPIStub();

    InputAPIGrpc.InputAPIFutureStub inputAPIFutureStub();

    InterestAPIGrpc.InterestAPIStub interestAPIStub();

    InterestAPIGrpc.InterestAPIBlockingStub interestAPIBlockingStub();

    SearchAPIGrpc.SearchAPIStub searchAPIStub();

    MetaAPIGrpc.MetaAPIStub metaAPIStub();
}
