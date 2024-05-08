package smartrics.iotics.identity.resolver.model;

public record Delegation(
        String id,
        String controller,
        String proof,
        String proofType
) {}
