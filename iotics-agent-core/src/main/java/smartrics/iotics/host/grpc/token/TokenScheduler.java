package smartrics.iotics.host.grpc.token;

public interface TokenScheduler {
    void schedule();

    void cancel();

    String validToken() throws IllegalStateException;
}
