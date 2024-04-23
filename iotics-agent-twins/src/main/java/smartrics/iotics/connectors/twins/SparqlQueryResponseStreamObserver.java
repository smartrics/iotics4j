package smartrics.iotics.connectors.twins;

import com.iotics.api.SparqlQueryResponse;
import io.grpc.stub.StreamObserver;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Implementation of a {@link StreamObserver} that reorders packets based on the response sequence number.
 * It will stream to the delegate the current buffer as soon as ordered.
 */
class SparqlQueryResponseStreamObserver implements StreamObserver<SparqlQueryResponse> {

    private final StreamObserver<String> delegate;

    private final List<SparqlQueryResponse.Payload> queue = new CopyOnWriteArrayList<>();

    private final AtomicLong expectedTotalPackets = new AtomicLong(-1);


    SparqlQueryResponseStreamObserver(StreamObserver<String> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onNext(SparqlQueryResponse response) {
        SparqlQueryResponse.Payload payload = response.getPayload();
        if (payload.hasStatus()) {
            delegate.onError(new SearchException("Query operation failure", payload.getStatus()));
            return;
        }

        queue.add(payload);

        if (payload.getLast()) {
            expectedTotalPackets.set(payload.getSeqNum());
        }

        if (!queue.isEmpty() && queue.size() == expectedTotalPackets.get()) {
            queue.sort(Comparator.comparingLong(SparqlQueryResponse.Payload::getSeqNum));

            String fullString = queue.stream().map(p -> p.getResultChunk().toStringUtf8()).collect(Collectors.joining());

            delegate.onNext(fullString);
            delegate.onCompleted(); // Ensure to signal completion after sending the full concatenated string
        }
    }

    @Override
    public void onError(Throwable t) {
        delegate.onError(t);
    }

    @Override
    public void onCompleted() {
        delegate.onCompleted();
    }
}
