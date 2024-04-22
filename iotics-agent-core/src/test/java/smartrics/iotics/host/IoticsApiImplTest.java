package smartrics.iotics.host;

import com.google.common.util.concurrent.ListenableFuture;
import com.iotics.api.*;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import smartrics.iotics.host.grpc.HostConnection;

import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;

import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class IoticsApiImplTest {

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private HostConnection hostConnection;

    private IoticsApiImpl api;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final TwinAPIGrpc.TwinAPIImplBase twinApi = mock(TwinAPIGrpc.TwinAPIImplBase.class, delegatesTo(new TwinAPIGrpc.TwinAPIImplBase() {
        @Override
        public void listAllTwins(ListAllTwinsRequest request, StreamObserver<ListAllTwinsResponse> responseObserver) {
            responseObserver.onNext(ListAllTwinsResponse.newBuilder()
                    .setHeaders(Headers.newBuilder().setClientRef(request.getHeaders().getClientRef()).build()).build());
            responseObserver.onCompleted();
        }
    }));

    @Before
    public void setup() throws Exception {
        String serverName = InProcessServerBuilder.generateName();

        // Create and start an in-process server.
        grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor()
                .addService(twinApi)
                .build().start());

        ManagedChannel channel = grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());

        hostConnection = mock(HostConnection.class);
        when(hostConnection.getGrpcChannel()).thenReturn(channel);

        api = new IoticsApiImpl(hostConnection);


    }

    @Test
    /* other methods need to be checked for wiring */
    public void twinStubIsWired() throws Exception {
        TwinAPIGrpc.TwinAPIFutureStub stub = api.twinAPIFutureStub();
        ListAllTwinsRequest request = ListAllTwinsRequest.newBuilder()
                .setHeaders(Headers.newBuilder().setClientRef("cli1").build())
                .build();


        ListenableFuture<ListAllTwinsResponse> res = stub.listAllTwins(request);

        assertEquals("cli1", res.get().getHeaders().getClientRef());

    }

    @Test
    public void onStopShutdownConnection() throws InterruptedException {
        Duration to = Duration.ofSeconds(10);
        api.stop(to);
        verify(hostConnection).shutdown(to);
    }

}
