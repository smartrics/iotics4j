package smartrics.iotics.host.grpc;

import io.grpc.stub.StreamObserver;

public abstract class AbstractDelegatingStreamObserver<T> implements StreamObserver<T> {

    protected final StreamObserver<T> delegate;

    public AbstractDelegatingStreamObserver(StreamObserver<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onError(Throwable throwable) {
        delegate.onError(throwable);
    }

    @Override
    public void onCompleted() {
        delegate.onCompleted();
    }
}
