package smartrics.iotics.host.grpc;

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannelBuilder;
import smartrics.iotics.identity.IdentityManager;
import smartrics.iotics.identity.SimpleIdentityManager;
import smartrics.iotics.host.grpc.token.TokenScheduler;
import smartrics.iotics.host.grpc.token.TokenTimerSchedulerBuilder;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.concurrent.ScheduledExecutorService;

public class HostManagedChannelBuilderFactory {

    private Duration tokenDuration;
    private IdentityManager sim;
    private String grpcEndpoint;
    private String userAgent;
    private ScheduledExecutorService scheduler;

    public HostManagedChannelBuilderFactory withSGrpcEndpoint(String endpoint) {
        this.grpcEndpoint = endpoint;
        return this;
    }

    public HostManagedChannelBuilderFactory withScheduler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public HostManagedChannelBuilderFactory withUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public HostManagedChannelBuilderFactory withIdentityManager(IdentityManager sim) {
        this.sim = sim;
        return this;
    }

    public HostManagedChannelBuilderFactory withTokenTokenDuration(Duration tokenDuration) {
        this.tokenDuration = tokenDuration;
        return this;
    }

    public ManagedChannelBuilder<?> makeManagedChannelBuilder() {
        var builder = ManagedChannelBuilder.forTarget(grpcEndpoint);

        TokenScheduler tokenScheduler = TokenTimerSchedulerBuilder
                .aTokenTimerScheduler()
                .withScheduler(scheduler)
                .withDuration(tokenDuration)
                .withIdentityManager(sim)
                .build();
        tokenScheduler.schedule();

        TokenInjectorClientInterceptor tokenInjectorClientInterceptor = new TokenInjectorClientInterceptor(tokenScheduler);
        List<ClientInterceptor> interceptorList = new ArrayList<>();
        interceptorList.add(tokenInjectorClientInterceptor);
        builder.intercept(interceptorList);
        builder.userAgent(Objects.requireNonNullElseGet(userAgent, () -> "UserAgent=" + sim.agentIdentity().did()));
        return builder;
    }

}
