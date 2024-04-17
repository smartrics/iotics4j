package smartrics.iotics.host.grpc.token;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import smartrics.iotics.identity.IdentityManager;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenTimerSchedulerTest {

    @Mock
    private ScheduledExecutorService mockScheduler;
    @Mock
    private IdentityManager mockIdentityManager;

    private TokenTimerScheduler scheduler;
    private Duration duration;

    @BeforeEach
    void setUp() {
        duration = Duration.ofSeconds(3600); // 1 hour for the token validity
        scheduler = new TokenTimerScheduler(mockIdentityManager, duration, mockScheduler);
    }

    @Test
    void testScheduleTokenRefresh() {
        doAnswer(invocation -> {
            ((TimerTask) invocation.getArgument(0)).run();
            return null;
        }).when(mockScheduler).scheduleAtFixedRate(any(Runnable.class), eq(0L), anyLong(), any());

        when(mockIdentityManager.newAuthenticationToken(duration)).thenReturn("newToken");

        scheduler.schedule();

        verify(mockScheduler).scheduleAtFixedRate(any(TimerTask.class), eq(0L), eq(duration.toMillis() - 10), TimeUnit.MILLISECONDS);
        assertEquals("newToken", scheduler.validToken());
    }

    @Test
    void testCancelTimer() {
        scheduler.cancel();
        verify(mockScheduler).shutdown();
    }

    @Test
    void testValidTokenNotScheduled() {
        Exception exception = assertThrows(IllegalStateException.class, scheduler::validToken);
        assertEquals("not scheduled", exception.getMessage());
    }

    @Test
    void testNullTimerConstruction() {
        Exception exception = assertThrows(IllegalStateException.class, () ->
                new TokenTimerScheduler(mockIdentityManager, duration, null).schedule()
        );
        assertEquals("null timer", exception.getMessage());
    }
}
