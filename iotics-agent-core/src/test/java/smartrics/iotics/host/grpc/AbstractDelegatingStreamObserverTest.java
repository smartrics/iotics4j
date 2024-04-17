package smartrics.iotics.host.grpc;

import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AbstractDelegatingStreamObserverTest {

    private static class TestDelegatingStreamObserver<T> extends AbstractDelegatingStreamObserver<T> {
        TestDelegatingStreamObserver(StreamObserver<T> delegate) {
            super(delegate);
        }

        @Override
        public void onNext(T t) {
            delegate.onNext(t);
        }
    }

    @Mock
    private StreamObserver<Object> mockDelegate;

    private TestDelegatingStreamObserver<Object> observer;

    @BeforeEach
    void setUp() {
        observer = new TestDelegatingStreamObserver<>(mockDelegate);
    }

    @Test
    void shouldDelegateOnError() {
        Throwable mockThrowable = new Throwable("test error");
        observer.onError(mockThrowable);
        verify(mockDelegate).onError(mockThrowable);
    }

    @Test
    void shouldDelegateOnCompleted() {
        observer.onCompleted();
        verify(mockDelegate).onCompleted();
    }
}
