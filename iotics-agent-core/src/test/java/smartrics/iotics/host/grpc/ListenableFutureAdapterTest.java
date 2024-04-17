package smartrics.iotics.host.grpc;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class ListenableFutureAdapterTest {

    @Test
    void onSuccessShouldCompleteCompletableFuture() throws ExecutionException, InterruptedException {
        SettableFuture<String> listenableFuture = SettableFuture.create();
        ListenableFutureAdapter<String> adapter = new ListenableFutureAdapter<>(listenableFuture, MoreExecutors.directExecutor());

        String expectedValue = "test";
        listenableFuture.set(expectedValue);

        CompletableFuture<String> completableFuture = adapter.getCompletableFuture();
        assertEquals(expectedValue, completableFuture.get());
    }

    @Test
    void onFailureShouldCompleteExceptionally() {
        SettableFuture<String> listenableFuture = SettableFuture.create();
        ListenableFutureAdapter<String> adapter = new ListenableFutureAdapter<>(listenableFuture, MoreExecutors.directExecutor());

        Exception expectedException = new RuntimeException("Error");
        listenableFuture.setException(expectedException);

        CompletableFuture<String> completableFuture = adapter.getCompletableFuture();

        ExecutionException thrown = assertThrows(ExecutionException.class, completableFuture::get);
        assertEquals(expectedException, thrown.getCause());
    }

    @Test
    void cancelShouldCancelBothFutures() throws TimeoutException, InterruptedException {
        SettableFuture<String> listenableFuture = SettableFuture.create();
        ListenableFutureAdapter<String> adapter = new ListenableFutureAdapter<>(listenableFuture, MoreExecutors.directExecutor());

        CompletableFuture<String> completableFuture = adapter.getCompletableFuture();
        assertTrue(completableFuture.cancel(true));
        assertTrue(completableFuture.isCancelled());

        assertThrows(CancellationException.class, () -> listenableFuture.get(1, TimeUnit.SECONDS), "Future was not cancelled");
    }
}
