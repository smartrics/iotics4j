package smartrics.iotics.host.grpc.token;

import smartrics.iotics.identity.IdentityManager;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class TokenTimerScheduler implements TokenScheduler {

    private final ScheduledExecutorService scheduler;
    private final IdentityManager identityManager;
    private final Duration duration;
    private final AtomicReference<String> validToken;

    TokenTimerScheduler(IdentityManager identityManager, Duration duration, ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
        this.identityManager = identityManager;
        this.validToken = new AtomicReference<>();
        this.duration = duration;
    }

    @Override
    public void schedule() {
        if (scheduler == null) {
            throw new IllegalStateException("null scheduler");
        }
        this.scheduler.scheduleAtFixedRate(() -> {
            // a token is used to auth this agent and user - the token has a validity. The longer the validity
            // the lower the security - if token is stolen the thief can impersonate
            validToken.set(identityManager.newAuthenticationToken(duration));
        }, 0, duration.toMillis() - 10, TimeUnit.MILLISECONDS);

    }

    @Override
    public void cancel() {
        if (scheduler != null) {
            this.scheduler.shutdown();
        }
    }

    @Override
    public String validToken() {
        String value = validToken.get();
        if (value == null) {
            throw new IllegalStateException("not scheduled");
        }
        return value;
    }

}
