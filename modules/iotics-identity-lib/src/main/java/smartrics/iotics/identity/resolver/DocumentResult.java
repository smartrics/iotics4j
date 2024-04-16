package smartrics.iotics.identity.resolver;

public record DocumentResult(String rawDocument, String fetchErrorMessage) {
    public boolean hasError() {
        return fetchErrorMessage != null;
    }
}
