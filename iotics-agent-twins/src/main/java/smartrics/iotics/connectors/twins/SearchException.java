package smartrics.iotics.connectors.twins;

import com.google.rpc.Status;

/**
 * Exception thrown to indicate an error during search operations.
 */
public class SearchException extends RuntimeException {

    private final Status status;

    /**
     * Constructs a SearchException with the specified error message and status.
     *
     * @param message The error message.
     * @param status  The status associated with the exception.
     */
    public SearchException(String message, Status status) {
        super(message + ". Search status: [" + status + "]");
        this.status = status;
    }

    /**
     * Retrieves the status associated with the exception.
     *
     * @return The status.
     */
    public Status status() {
        return status;
    }
}

