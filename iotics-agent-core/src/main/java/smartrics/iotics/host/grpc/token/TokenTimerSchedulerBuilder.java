package smartrics.iotics.host.grpc.token;

import smartrics.iotics.identity.IdentityManager;

import java.time.Duration;
import java.util.Timer;
import java.util.concurrent.ScheduledExecutorService;

public final class TokenTimerSchedulerBuilder {
    private ScheduledExecutorService scheduler;
    private IdentityManager identityManager;
    private Duration duration;

    private TokenTimerSchedulerBuilder() {
    }

    public static TokenTimerSchedulerBuilder aTokenTimerScheduler() {
        return new TokenTimerSchedulerBuilder();
    }

    public TokenTimerSchedulerBuilder withScheduler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public TokenTimerSchedulerBuilder withIdentityManager(IdentityManager identityManager) {
        this.identityManager = identityManager;
        return this;
    }

    public TokenTimerSchedulerBuilder withDuration(Duration duration) {
        this.duration = duration;
        return this;
    }

    public TokenTimerScheduler build() {
        return new TokenTimerScheduler(identityManager, duration, scheduler);
    }
}
