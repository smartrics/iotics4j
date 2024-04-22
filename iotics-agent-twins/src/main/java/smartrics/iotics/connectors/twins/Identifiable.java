package smartrics.iotics.connectors.twins;

import smartrics.iotics.identity.Identity;

public interface Identifiable {
    Identity getMyIdentity();

    Identity getAgentIdentity();
}
