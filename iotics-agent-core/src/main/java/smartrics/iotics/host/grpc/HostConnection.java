package smartrics.iotics.host.grpc;

import io.grpc.ManagedChannel;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public interface HostConnection {

    ManagedChannel getGrpcChannel();

    void shutdown(Duration wait) throws InterruptedException;
}
