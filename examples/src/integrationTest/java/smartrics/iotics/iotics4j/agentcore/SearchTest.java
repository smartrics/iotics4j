package smartrics.iotics.iotics4j.agentcore;

import com.google.protobuf.Timestamp;
import com.iotics.api.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import smartrics.iotics.host.Builders;
import smartrics.iotics.host.IoticsApi;
import smartrics.iotics.host.UriConstants;
import smartrics.iotics.identity.IdentityManager;
import smartrics.iotics.iotics4j.Configuration;
import smartrics.iotics.iotics4j.Factory;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SearchTest {
    private static IoticsApi api;
    private static IdentityManager sim;

    @BeforeAll
    static void init() {
        Configuration configuration = Configuration.load();
        sim = Factory.newSIM(configuration);
        api = Factory.newIoticsApi(configuration, sim);
    }

    @Test
    @Timeout(5)
    void searchExpiresInTimeoutProvided() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        api.searchAPI().synchronousSearch(
                SearchRequest.newBuilder()
                        .setHeaders(Builders.newHeadersBuilder(sim.agentIdentity())
                                .setRequestTimeout(Timestamp.newBuilder()
                                        .setSeconds(Instant.now().getEpochSecond() + 3)
                                        .build())
                                .build())
                        .setScope(Scope.LOCAL)

                        .setPayload(SearchRequest.Payload.newBuilder()
                                .setFilter(SearchRequest.Payload.Filter.newBuilder()
                                        .addProperties(Property.newBuilder()
                                                .setKey(UriConstants.RDFSProperty.Label)
                                                .setStringLiteralValue(StringLiteral.newBuilder().setValue("i90u8y9tufhbj").build())
                                                .build())
                                        .build())
                                .build())
                        .build(), new StreamObserver<>() {

                    @Override
                    public void onNext(SearchResponse searchResponse) {
                        System.out.println("response");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if(throwable instanceof StatusRuntimeException &&
                                ((StatusRuntimeException)throwable).getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
                            System.out.println("Search completed");
                        }
                        latch.countDown();
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("completed");
                        latch.countDown();
                    }
                });
        latch.await();
    }
}
