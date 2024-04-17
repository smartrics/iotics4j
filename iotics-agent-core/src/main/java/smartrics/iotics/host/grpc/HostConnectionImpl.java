package smartrics.iotics.host.grpc;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import smartrics.iotics.identity.IdentityManager;
import smartrics.iotics.identity.SimpleConfig;
import smartrics.iotics.identity.SimpleIdentityManager;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HostConnectionImpl implements HostConnection {
    private final ManagedChannel channel;
    private final ScheduledExecutorService scheduler;

    public HostConnectionImpl(String grpcEndpoint, IdentityManager sim, Duration tokenValidityDuration) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("token-refresh-%d").build());
        ManagedChannelBuilder<?> channelBuilder = newHostManagedChannelBuilderFactory(sim, scheduler, grpcEndpoint, tokenValidityDuration);
        ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("iot-grpc-%d").build());
        channel = channelBuilder.executor(executor).keepAliveWithoutCalls(true).build();
    }

    public HostConnectionImpl(String grpcEndpoint, SimpleConfig userConf, SimpleConfig agentConf, Duration tokenValidityDuration) {
        this(grpcEndpoint, SimpleIdentityManager.Builder
                .anIdentityManager()
                .withAgentKeyID(agentConf.keyId())
                .withUserKeyID(userConf.keyId())
                .withAgentKeyName(agentConf.keyName())
                .withUserKeyName(userConf.keyName()).build(), tokenValidityDuration);
    }

    public static ManagedChannelBuilder<?> newHostManagedChannelBuilderFactory(IdentityManager sim, ScheduledExecutorService scheduler, String grpcEndpoint, Duration tokenValidityDuration) {
        return new HostManagedChannelBuilderFactory().withIdentityManager(sim).withScheduler(scheduler).withSGrpcEndpoint(grpcEndpoint).withTokenTokenDuration(tokenValidityDuration).makeManagedChannelBuilder();
    }

    @Override
    public ManagedChannel getGrpcChannel() {
        return this.channel;
    }

    @Override
    public void shutdown(Duration wait) throws InterruptedException {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(wait.getSeconds(), TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // preserve interruption status
            scheduler.shutdownNow();
        }

        channel.shutdown();
        if (!channel.awaitTermination(wait.getSeconds(), TimeUnit.SECONDS)) {
            channel.shutdownNow(); // ensure channel is closed if not successfully shutdown
        }

    }
}
