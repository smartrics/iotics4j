package smartrics.iotics.identity.resolver.model;

import java.util.List;
import java.util.Map;

public record Document(
        String id,
        String ioticsSpecVersion,
        String ioticsDIDType,
        long updateTime,
        String proof,
        List<PublicKey> publicKey,
        List<DelegateAuthentication> delegateAuthentication,
        Map<String, String> metadata
) {}