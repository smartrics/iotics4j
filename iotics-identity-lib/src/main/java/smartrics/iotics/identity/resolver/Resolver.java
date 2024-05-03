package smartrics.iotics.identity.resolver;

import java.io.IOException;

/**
 * Resolves decentralized identifiers (DIDs) using a resolver client.
 */
public class Resolver {

    private final ResolverClient client;

    /**
     * Constructs a Resolver with the specified resolver client.
     *
     * @param client The resolver client to use for resolving DIDs.
     */
    public Resolver(ResolverClient client) {
        this.client = client;
    }

    /**
     * Discovers information about the specified decentralized identifier (DID).
     *
     * @param did The decentralized identifier (DID) to discover.
     * @return A DocumentResult containing the raw document or fetch error message.
     */
    public DocumentResult discover(String did) {
        try {
            ResolverClient.Result result = this.client.discover(did);
            if (result.isErr()) {
                return new DocumentResult(null, result.content());
            }
            return new DocumentResult(result.content(), null);
        } catch (IOException e) {
            return new DocumentResult(null, e.getMessage());
        }
    }
}
