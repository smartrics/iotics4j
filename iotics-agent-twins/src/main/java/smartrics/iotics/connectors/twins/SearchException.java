package smartrics.iotics.connectors.twins;

import com.google.rpc.Status;

public class SearchException extends RuntimeException {
    private final Status status;

    public SearchException(String message, Status status) {
        super(message + ". Search status: [" + status + "]");
        this.status = status;
    }

    public Status status() {
        return status;
    }

}
