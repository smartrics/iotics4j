package smartrics.iotics.identity;

import java.time.Duration;

public interface SimpleIdentity {
    Identity CreateAgentIdentity(String keyName, String name);

    Identity RecreateAgentIdentity(String keyName, String name);

    Identity CreateTwinIdentity(String keyName, String name);

    Identity RecreateTwinIdentity(String keyName, String name);

    Identity CreateUserIdentity(String keyName, String name);

    Identity RecreateUserIdentity(String keyName, String name);

    Identity CreateTwinIdentityWithControlDelegation(Identity agentIdentity, String twinKeyName, String twinName);

    String CreateAgentAuthToken(Identity agentIdentity, String userDid, String audience, Duration duration);

    String CreateAgentAuthToken(Identity agentIdentity, String userDid, Duration duration);

    String RecreateAgentAuthToken(Identity agentIdentity, String userDid, String audience, Duration duration);

    String RecreateAgentAuthToken(Identity agentIdentity, String userDid, Duration duration);

    String IsAllowedFor(String resolverAddress, String token);

    void UserDelegatesAuthenticationToAgent(Identity agentId, Identity userId, String delegationName);

    void TwinDelegatesControlToAgent(Identity agentId, Identity twinId, String delegationName);
}
