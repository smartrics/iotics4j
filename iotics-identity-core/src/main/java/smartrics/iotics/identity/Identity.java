package smartrics.iotics.identity;

import java.util.Objects;

/**
 * Represents an identity within the system, characterized by key identifiers.
 * This immutable record is used to encapsulate the identity attributes that are used
 * for authentication and identification processes across various components of the system.
 *
 * <p>The constructor of this record ensures that all fields are non-null, preventing the
 * creation of invalid identity instances.
 *
 * @param keyName the key name associated with the identity, must not be null
 * @param name    the name of the identity, must not be null
 * @param did     the decentralized identifier (DID), must not be null
 */
public record Identity(String keyName, String name, String did) {

    /**
     * Ensures that none of the parameters are null to maintain data integrity
     * across system operations that depend on valid identity instances.
     */
    public Identity {
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(keyName, "Key name cannot be null");
        Objects.requireNonNull(did, "DID cannot be null");
    }
}
