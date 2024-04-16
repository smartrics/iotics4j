package smartrics.iotics.identity.resolver;

import java.io.IOException;

public class Resolver {

    private final ResolverClient client;

    public Resolver(ResolverClient client) {
        this.client = client;
    }

    public DocumentResult discover(String did) {
        try {
            ResolverClient.Result result = this.client.discover(did);
            if(result.isErr()) {
                return new DocumentResult(null, result.content());
            }
            return new DocumentResult(result.content(), null);
        } catch (IOException e) {
            return new DocumentResult(null, e.getMessage());
        }
    }

}
