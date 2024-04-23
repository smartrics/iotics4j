package smartrics.iotics.connectors.twins;

import smartrics.iotics.identity.Identity;

/**
 * Interface for obtaining identity information.
 * This interface provides methods to access identity details for an twin and its associated agent.
 */
public interface Identifiable {

    /**
     * Returns the identity of the twin.
     *
     * @return the identity object representing the twin's own identity
     */
    Identity getMyIdentity();

    /**
     * Returns the identity of the agent with control delegation on the twin.
     *
     * @return the identity object representing the agent's identity
     */
    Identity getAgentIdentity();
}
