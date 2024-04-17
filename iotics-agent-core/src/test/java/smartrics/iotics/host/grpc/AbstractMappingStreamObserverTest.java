package smartrics.iotics.host.grpc;

import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AbstractMappingStreamObserverTest {

    @Mock
    private StreamObserver<String> mockDelegate;
    private TestMappingStreamObserver<Object, String> observer;

    @BeforeEach
    void setUp() {
        observer = new TestMappingStreamObserver<>(mockDelegate);
    }

    @Test
    void onNextShouldTransformDataAndDelegate() {
        Object testData = 123; // Example data
        String transformedData = "123"; // Expected result of applying the transformation

        observer.onNext(testData);

        verify(mockDelegate).onNext(transformedData);
    }

    @Test
    void onErrorShouldDelegate() {
        Throwable mockThrowable = new Exception("Test exception");

        observer.onError(mockThrowable);

        verify(mockDelegate).onError(mockThrowable);
    }

    @Test
    void onCompletedShouldDelegate() {
        observer.onCompleted();

        verify(mockDelegate).onCompleted();
    }

    private static class TestMappingStreamObserver<T, K> extends AbstractMappingStreamObserver<T, K> {
        TestMappingStreamObserver(StreamObserver<K> delegate) {
            super(delegate);
        }

        @Override
        public K apply(T data) {
            // Implement a simple transformation logic for testing, e.g., casting or converting the data type.
            return (K) data.toString(); // This example assumes K is String for simplicity.
        }
    }
}
