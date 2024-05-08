package smartrics.iotics.host.grpc;

import io.grpc.*;
import smartrics.iotics.identity.IdentityManager;
import smartrics.iotics.host.grpc.token.TokenScheduler;
import smartrics.iotics.host.grpc.token.TokenTimerSchedulerBuilder;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class HostManagedChannelBuilderFactory {

    private Duration tokenDuration;
    private IdentityManager sim;
    private String grpcEndpoint;
    private String userAgent;
    private ScheduledExecutorService scheduler;
    private Consumer<String> logConsumer = null;

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

    public HostManagedChannelBuilderFactory withLogConsumer(Consumer<String> logConsumer) {
        this.logConsumer = logConsumer;
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
        if(this.logConsumer != null) {
            interceptorList.add(new TracingClientInterceptor(logConsumer));
        }
        builder.intercept(interceptorList);
        builder.userAgent(Objects.requireNonNullElseGet(userAgent, () -> "UserAgent=" + sim.agentIdentity().did()));
        return builder;
    }

    public static class TracingClientInterceptor implements ClientInterceptor {

        private final Consumer<String> logConsumer;

        public TracingClientInterceptor(Consumer<String> logConsumer) {
            this.logConsumer = logConsumer;
        }

        @Override
        public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
                MethodDescriptor<ReqT, RespT> method,
                CallOptions callOptions,
                Channel next) {
            return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
                @Override
                public void start(io.grpc.ClientCall.Listener<RespT> responseListener, Metadata headers) {
                    System.out.println("start|headers: " + headers);
                    super.start(new ClientCall.Listener<RespT>() {
                        public void onHeaders(Metadata headers) {
                            logConsumer.accept("onHeaders|headers: " + headers);
                            responseListener.onHeaders(headers);
                        }

                        public void onMessage(RespT message) {
                            logConsumer.accept("onMessage|: " + message);
                            responseListener.onMessage(message);
                        }

                        public void onClose(Status status, Metadata trailers) {
                            logConsumer.accept("onClose| : " + status + ", trailers: "+trailers);
                            responseListener.onClose(status, trailers);
                        }

                        public void onReady() {
                            responseListener.onReady();
                        }
                    }, headers);
                }

                @Override
                public void request(int numMessages) {
                    logConsumer.accept("request|numMessages: " + numMessages);
                    super.request(numMessages);
                }

                @Override
                public void cancel(String message, Throwable cause) {
                    logConsumer.accept("cancel|message: " + message + ", " + cause);
                    super.cancel(message, cause);
                }

                @Override
                public void halfClose() {
                    logConsumer.accept("halfClose");
                    super.halfClose();
                }

                @Override
                public void sendMessage(ReqT message) {
                    logConsumer.accept("sendMessage|message: " + message);
                    super.sendMessage(message);
                }
            };
        }
    }

}
