package smartrics.iotics.identity.resolver;

/**
 * Represents the result of a document retrieval operation, containing the raw document and any fetch error message.
 */
public record DocumentResult(String rawDocument, String fetchErrorMessage) {

    /**
     * Checks if the document retrieval operation resulted in an error.
     *
     * @return true if there is a fetch error message, indicating an error; otherwise, false.
     */
    public boolean hasError() {
        return fetchErrorMessage != null;
    }
}