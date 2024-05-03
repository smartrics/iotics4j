package smartrics.iotics.identity;

import java.time.Duration;

/**
 * Low level Java API mapping to the native IOTICS Identity library.
 * The SimpleIdentity interface provides methods for creating, recreating, and managing various types of identities,
 * as well as generating authentication tokens and handling delegation of authentication and control.
 */
public interface SimpleIdentity {

    /**
     * Creates a new agent identity with the specified key name and name.
     * @param keyName The key name for the agent identity.
     * @param name The name for the agent identity.
     * @return The created agent identity.
     */
    Identity CreateAgentIdentity(String keyName, String name);

    /**
     * Recreates an agent identity with the specified key name and name.
     * @param keyName The key name for the agent identity.
     * @param name The name for the agent identity.
     * @return The recreated agent identity.
     */
    Identity RecreateAgentIdentity(String keyName, String name);

    /**
     * Creates a new twin identity with the specified key name and name.
     * @param keyName The key name for the twin identity.
     * @param name The name for the twin identity.
     * @return The created twin identity.
     */
    Identity CreateTwinIdentity(String keyName, String name);

    /**
     * Recreates a twin identity with the specified key name and name.
     * @param keyName The key name for the twin identity.
     * @param name The name for the twin identity.
     * @return The recreated twin identity.
     */
    Identity RecreateTwinIdentity(String keyName, String name);

    /**
     * Creates a new user identity with the specified key name and name.
     * @param keyName The key name for the user identity.
     * @param name The name for the user identity.
     * @return The created user identity.
     */
    Identity CreateUserIdentity(String keyName, String name);

    /**
     * Recreates a user identity with the specified key name and name.
     * @param keyName The key name for the user identity.
     * @param name The name for the user identity.
     * @return The recreated user identity.
     */
    Identity RecreateUserIdentity(String keyName, String name);

    /**
     * Creates a new twin identity with control delegation from the specified agent identity.
     * @param agentIdentity The agent identity delegating control.
     * @param twinKeyName The key name for the twin identity.
     * @param twinName The name for the twin identity.
     * @return The created twin identity with control delegation.
     */
    Identity CreateTwinIdentityWithControlDelegation(Identity agentIdentity, String twinKeyName, String twinName);

    /**
     * Creates an authentication token for the specified agent identity, user DID, audience, and duration.
     * @param agentIdentity The agent identity.
     * @param userDid The user decentralized identifier (DID).
     * @param audience The intended audience for the token.
     * @param duration The duration of the token validity.
     * @return The generated authentication token.
     */
    String CreateAgentAuthToken(Identity agentIdentity, String userDid, String audience, Duration duration);

    /**
     * Creates an authentication token for the specified agent identity, user DID, and duration.
     * @param agentIdentity The agent identity.
     * @param userDid The user decentralized identifier (DID).
     * @param duration The duration of the token validity.
     * @return The generated authentication token.
     */
    String CreateAgentAuthToken(Identity agentIdentity, String userDid, Duration duration);

    /**
     * Recreates an authentication token for the specified agent identity, user DID, audience, and duration.
     * @param agentIdentity The agent identity.
     * @param userDid The user decentralized identifier (DID).
     * @param audience The intended audience for the token.
     * @param duration The duration of the token validity.
     * @return The recreated authentication token.
     */
    String RecreateAgentAuthToken(Identity agentIdentity, String userDid, String audience, Duration duration);

    /**
     * Recreates an authentication token for the specified agent identity, user DID, and duration.
     * @param agentIdentity The agent identity.
     * @param userDid The user decentralized identifier (DID).
     * @param duration The duration of the token validity.
     * @return The recreated authentication token.
     */
    String RecreateAgentAuthToken(Identity agentIdentity, String userDid, Duration duration);

    /**
     * Checks if the specified token is allowed for the given resolver address.
     * @param resolverAddress The resolver address.
     * @param token The authentication token.
     * @return True if the token is allowed, otherwise false.
     */
    String IsAllowedFor(String resolverAddress, String token);

    /**
     * Delegates authentication from a user identity to an agent identity with the specified delegation name.
     * @param agentId The agent identity.
     * @param userId The user identity.
     * @param delegationName The name of the delegation.
     */
    void UserDelegatesAuthenticationToAgent(Identity agentId, Identity userId, String delegationName);

    /**
     * Delegates control from a twin identity to an agent identity with the specified delegation name.
     * @param agentId The agent identity.
     * @param twinId The twin identity.
     * @param delegationName The name of the delegation.
     */
    void TwinDelegatesControlToAgent(Identity agentId, Identity twinId, String delegationName);
}
