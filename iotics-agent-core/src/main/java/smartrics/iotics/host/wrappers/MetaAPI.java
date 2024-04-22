package smartrics.iotics.host.wrappers;

import com.iotics.api.SparqlQueryRequest;
import com.iotics.api.SparqlQueryResponse;
import com.iotics.api.SparqlUpdateRequest;
import com.iotics.api.SparqlUpdateResponse;
import io.grpc.stub.StreamObserver;


/**
 * Internalised interface of IOTICS Metadata API gRPC Service
 */
public interface MetaAPI {
    void sparqlQuery(SparqlQueryRequest request, StreamObserver<SparqlQueryResponse> queryOperationFailure);

    void sparqlUpdate(SparqlUpdateRequest request, StreamObserver<SparqlUpdateResponse> observer);
}
