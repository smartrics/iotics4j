package smartrics.iotics.connectors.twins;

import com.iotics.api.SearchRequest;
import com.iotics.api.SearchResponse;
import com.iotics.api.SparqlQueryRequest;
import io.grpc.stub.StreamObserver;
import smartrics.iotics.host.Builders;


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
     * @param result  The observer to handle responses or errors from the query operation.
     */
    default void query(SparqlQueryRequest.Payload payload, StreamObserver<String> delegate) {
        SparqlQueryRequest request = SparqlQueryRequest.newBuilder()
                .setHeaders(Builders.newHeadersBuilder(getAgentIdentity().did()).build())
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
    default void search(SearchRequest.Payload searchRequestPayload, StreamObserver<SearchResponse.TwinDetails> twinDetailsStreamObserver) {
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
                twinDetailsStreamObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                twinDetailsStreamObserver.onCompleted();
            }
        };
        SearchRequest request = SearchRequest.newBuilder()
                .setHeaders(Builders.newHeadersBuilder(getAgentIdentity().did()).build())
                .setPayload(searchRequestPayload)
                .build();
        ioticsApi().searchAPI().synchronousSearch(request, obs);
    }
}
