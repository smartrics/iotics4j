package smartrics.iotics.connectors.twins;

import com.google.protobuf.Timestamp;
import com.iotics.api.SearchRequest;
import com.iotics.api.SearchResponse;
import com.iotics.api.SparqlQueryRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import smartrics.iotics.host.Builders;

import java.time.Duration;
import java.time.Instant;


/**
 * Interface that extends {@link Identifiable} and {@link ApiUser} to support search and query operations.
 * Provides default implementations for executing SPARQL queries and performing more general search operations,
 * leveraging the underlying API's capabilities to fetch and aggregate data.
 */
public interface Searcher extends Identifiable, ApiUser {

    /**
     * Executes a SPARQL query using a given payload and sends the results back through a {@link StreamObserver}.
     * This method processes each query response sequentially, aggregates the result chunks, and sends the
     * aggregated result back once all parts have been received and concatenated.
     *
     * @param payload The payload for the SPARQL query request.
     * @param delegate  The observer to handle responses or errors from the query operation.
     */
    default void query(SparqlQueryRequest.Payload payload, StreamObserver<String> delegate) {
        SparqlQueryRequest request = SparqlQueryRequest.newBuilder()
                .setHeaders(Builders.newHeadersBuilder(getAgentIdentity()).build())
                .setPayload(payload)
                .build();

        ioticsApi().metaAPI().sparqlQuery(request, new SparqlQueryResponseStreamObserver(delegate));
    }

    /**
     * Performs a search based on a specific payload and provides the results incrementally
     * through a {@link StreamObserver}. Handles each piece of twin data as it arrives, forwarding
     * it to the provided observer.
     *
     * @param searchRequestPayload      The payload specifying the search criteria.
     * @param twinDetailsStreamObserver The observer to receive and handle the twin details as they are found.
     */
    default void search(SearchRequest.Payload searchRequestPayload, Duration searchTimeout, StreamObserver<SearchResponse.TwinDetails> twinDetailsStreamObserver) {
        StreamObserver<SearchResponse> obs = new StreamObserver<>() {
            @Override
            public void onNext(SearchResponse searchResponse) {
                if (searchResponse.getPayload().hasStatus()) {
                    twinDetailsStreamObserver.onError(new SearchException("Search operation failure", searchResponse.getPayload().getStatus()));
                    return;
                }
                for (SearchResponse.TwinDetails d : searchResponse.getPayload().getTwinsList()) {
                    twinDetailsStreamObserver.onNext(d);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                if(throwable instanceof StatusRuntimeException e) {
                    if(Status.Code.DEADLINE_EXCEEDED.equals(e.getStatus().getCode())) {
                        twinDetailsStreamObserver.onCompleted();
                        return;
                    }
                }
                twinDetailsStreamObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                twinDetailsStreamObserver.onCompleted();
            }
        };
        SearchRequest request = SearchRequest.newBuilder()
                .setHeaders(Builders.newHeadersBuilder(getAgentIdentity())
                        .setRequestTimeout(Timestamp.newBuilder()
                                .setSeconds(Instant.now().getEpochSecond() + searchTimeout.getSeconds())
                                .build())
                        .build())
                .setPayload(searchRequestPayload)
                .build();
        ioticsApi().searchAPI().synchronousSearch(request, obs);
    }
}
