package smartrics.iotics.identity.resolver.model;

public record PublicKey(
        String id,
        String type,
        String publicKeyBase58
) {}