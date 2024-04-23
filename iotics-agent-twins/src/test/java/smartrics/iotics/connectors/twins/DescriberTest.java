package smartrics.iotics.connectors.twins;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.iotics.api.DescribeTwinResponse;
import com.iotics.api.TwinID;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import smartrics.iotics.host.IoticsApi;
import smartrics.iotics.host.wrappers.TwinAPIFuture;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DescriberTest {

    private Describer describer;
    private IoticsApi ioticsApi;
    private TwinAPIFuture twinAPIFuture;
    private StreamObserver<DescribeTwinResponse> streamObserver;
    private ScheduledExecutorService executorService;

    @BeforeEach
    void setUp() {
        ioticsApi = mock(IoticsApi.class);
        twinAPIFuture = mock(TwinAPIFuture.class);
        streamObserver = mock(StreamObserver.class);
        executorService = Executors.newSingleThreadScheduledExecutor();

        describer = new ConcreteDescriber(ioticsApi);

        when(ioticsApi.twinAPIFuture()).thenReturn(twinAPIFuture);
    }

    @AfterEach
    void tearDown() {
        executorService.shutdownNow();
    }

    @Test
    void testDescribeScheduledPolling() {
        TwinID twinID = TwinID.newBuilder().setId("twinId").build();
        Duration initialDelay = Duration.ofMillis(10);
        Duration pollingFrequency = Duration.ofMillis(100);

        DescribeTwinResponse expectedResponse = DescribeTwinResponse.newBuilder().build();
        SettableFuture<DescribeTwinResponse> futureResponse = SettableFuture.create();
        futureResponse.set(expectedResponse);
        when(twinAPIFuture.describeTwin(any())).thenReturn(futureResponse);

        describer.describe(twinID, executorService, initialDelay, pollingFrequency, streamObserver);

        try {
            Thread.sleep(200);  // Wait enough time for the scheduled task to execute
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        verify(streamObserver, atLeastOnce()).onNext(expectedResponse);
    }

    @Test
    void testDescribeWithNoParameters() throws Exception {
        SettableFuture<DescribeTwinResponse> futureResponse = SettableFuture.create();
        DescribeTwinResponse expectedResponse = DescribeTwinResponse.newBuilder().build();
        futureResponse.set(expectedResponse);
        when(twinAPIFuture.describeTwin(any())).thenReturn(futureResponse);

        ListenableFuture<DescribeTwinResponse> response = describer.describe();

        assertEquals(expectedResponse, response.get());
    }

    @Test
    void testDescribeWithTwinID() throws Exception {
        TwinID twinID = TwinID.newBuilder().setId("uniqueId").build();

        SettableFuture<DescribeTwinResponse> futureResponse = SettableFuture.create();
        DescribeTwinResponse expectedResponse = DescribeTwinResponse.newBuilder().build();
        futureResponse.set(expectedResponse);
        when(twinAPIFuture.describeTwin(any())).thenReturn(futureResponse);

        ListenableFuture<DescribeTwinResponse> response = describer.describe(twinID);

        assertEquals(expectedResponse, response.get());
    }

    private static class ConcreteDescriber extends BaseTwin implements Describer {
        ConcreteDescriber(IoticsApi api) {
            super(api);
        }
    }
}
