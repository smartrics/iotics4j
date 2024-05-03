package smartrics.iotics.identity;

import java.time.Duration;

/**
 * High level class to manage user and agent identities, providing wrapper methods to create authentication tokens and twins.
 */
public class SimpleIdentityManager implements IdentityManager {

    private final Identity agentIdentity;
    private final Identity userIdentity;
    private final SimpleIdentity idSdk;

    /**
     * Constructs a SimpleIdentityManager instance.
     *
     * @param idSdk            The SimpleIdentity instance used for identity operations.
     * @param userKeyName      The key name for the user identity.
     * @param userKeyID        The key ID for the user identity.
     * @param agentKeyName     The key name for the agent identity.
     * @param agentKeyID       The key ID for the agent identity.
     * @param authDelegationID The authentication delegation ID.
     */
    private SimpleIdentityManager(SimpleIdentity idSdk, String userKeyName, String userKeyID, String agentKeyName, String agentKeyID, String authDelegationID) {
        this.idSdk = idSdk;
        userIdentity = idSdk.CreateUserIdentity(userKeyName, userKeyID);
        agentIdentity = idSdk.CreateAgentIdentity(agentKeyName, agentKeyID);
        idSdk.UserDelegatesAuthenticationToAgent(agentIdentity, userIdentity, authDelegationID);
    }

    /**
     * Generates a new authentication token with the default expiry duration and audience.
     *
     * @param expiry The duration until the token expires.
     * @return The generated authentication token.
     */
    @Override
    public String newAuthenticationToken(Duration expiry) {
        return newAuthenticationToken(expiry, "undefined");
    }

    /**
     * Generates a new authentication token with the specified expiry duration and audience.
     *
     * @param expiry   The duration until the token expires.
     * @param audience The intended audience for the token.
     * @return The generated authentication token.
     */
    @Override
    public String newAuthenticationToken(Duration expiry, String audience) {
        return idSdk.CreateAgentAuthToken(this.agentIdentity, this.userIdentity.did(), audience, expiry);
    }

    /**
     * Creates a new twin identity with control delegation from the agent identity.
     *
     * @param twinKeyName         The key name for the twin identity.
     * @param controlDelegationID The ID for control delegation.
     * @return The created twin identity with control delegation.
     */
    @Override
    public Identity newTwinIdentityWithControlDelegation(String twinKeyName, String controlDelegationID) {
        return idSdk.CreateTwinIdentityWithControlDelegation(this.agentIdentity, twinKeyName, controlDelegationID);
    }

    /**
     * Creates a new twin identity with the specified key name and ID.
     *
     * @param twinKeyName The key name for the twin identity.
     * @param twinKeyID   The key ID for the twin identity.
     * @return The created twin identity.
     */
    @Override
    public Identity newTwinIdentity(String twinKeyName, String twinKeyID) {
        return idSdk.CreateTwinIdentity(twinKeyName, twinKeyID);
    }

    /**
     * Retrieves the agent identity managed by this SimpleIdentityManager.
     *
     * @return The agent identity.
     */
    @Override
    public Identity agentIdentity() {
        return agentIdentity;
    }

    /**
     * Retrieves the user identity managed by this SimpleIdentityManager.
     *
     * @return The user identity.
     */
    @Override
    public Identity userIdentity() {
        return userIdentity;
    }

    /**
     * Provides access to the underlying SimpleIdentity API for advanced use cases.
     *
     * @return The SimpleIdentity API.
     */
    public SimpleIdentity simpleIdentity() {
        return this.idSdk;
    }

    /**
     * Builder class for constructing instances of SimpleIdentityManager.
     */
    public static final class Builder {
        private String userKeyName;
        private String agentKeyName;
        private String userKeyID;
        private String agentKeyID;
        private String authDelegationID;
        private SimpleIdentity simpleIdentity;

        private Builder() {
            authDelegationID = "#deleg-0";
            userKeyID = "#user-0";
            agentKeyID = "agent-0";
        }

        /**
         * Initializes a new builder instance.
         *
         * @return A new instance of Builder.
         */
        public static Builder anIdentityManager() {
            return new Builder();
        }

        /**
         * Sets the key name for the user identity.
         *
         * @param userKeyName The key name for the user identity.
         * @return The Builder instance.
         */
        public Builder withUserKeyName(String userKeyName) {
            this.userKeyName = userKeyName;
            return this;
        }

        /**
         * Sets the key ID for the user identity.
         *
         * @param userKeyID The key ID for the user identity.
         * @return The Builder instance.
         */
        public Builder withUserKeyID(String userKeyID) {
            this.userKeyID = userKeyID;
            return this;
        }

        /**
         * Sets the key name for the agent identity.
         *
         * @param agentKeyName The key name for the agent identity.
         * @return The Builder instance.
         */
        public Builder withAgentKeyName(String agentKeyName) {
            this.agentKeyName = agentKeyName;
            return this;
        }

        /**
         * Sets the key ID for the agent identity.
         *
         * @param agentKeyID The key ID for the agent identity.
         * @return The Builder instance.
         */
        public Builder withAgentKeyID(String agentKeyID) {
            this.agentKeyID = agentKeyID;
            return this;
        }

        /**
         * Sets the SimpleIdentity implementation to be used.
         *
         * @param simpleIdentity The SimpleIdentity implementation.
         * @return The Builder instance.
         */
        public Builder withSimpleIdentity(SimpleIdentity simpleIdentity) {
            this.simpleIdentity = simpleIdentity;
            return this;
        }

        /**
         * Sets the authentication delegation ID.
         *
         * @param authDelegationID The authentication delegation ID.
         * @return The Builder instance.
         */
        public Builder withAuthDelegationID(String authDelegationID) {
            this.authDelegationID = authDelegationID;
            return this;
        }

        /**
         * Constructs a new SimpleIdentityManager instance based on the configured parameters.
         *
         * @return The constructed SimpleIdentityManager instance.
         */
        public SimpleIdentityManager build() {
            return new SimpleIdentityManager(simpleIdentity, userKeyName, userKeyID, agentKeyName, agentKeyID, authDelegationID);
        }
    }
}
