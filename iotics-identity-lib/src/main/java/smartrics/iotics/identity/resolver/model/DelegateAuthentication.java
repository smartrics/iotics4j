package smartrics.iotics.identity.resolver.model;

public record DelegateAuthentication(
        String id,
        String controller,
        String proof,
        String proofType
) {}
