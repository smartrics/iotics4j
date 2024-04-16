package smartrics.iotics.identity;

import java.util.Objects;

/**
 * Simple data bag for most frequent identity data.
 */
public record Identity(String keyName, String name, String did) {

    public Identity {
        Objects.requireNonNull(name);
        Objects.requireNonNull(keyName);
        Objects.requireNonNull(did);
    }
}