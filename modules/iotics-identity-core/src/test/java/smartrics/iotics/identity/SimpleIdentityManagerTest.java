package smartrics.iotics.identity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SimpleIdentityManagerTest {

    @Mock
    private SimpleIdentityImpl mockIdSdk;

    @InjectMocks
    private SimpleIdentityManager manager;

    private final String userKeyName = "userKey";
    private final String userKeyID = "userKeyID";
    private final String agentKeyName = "agentKey";
    private final String agentKeyID = "agentKeyID";
    private final String authDelegationID = "authDelegationID";

    private Identity userIdentity = new Identity("userKey", "user", "did:iotics:user");
    private Identity agentIdentity = new Identity("agentKey", "agent", "did:iotics:agent");

    @BeforeEach
    void setUp() {

        when(mockIdSdk.CreateUserIdentity(any(), any())).thenReturn(userIdentity);
        when(mockIdSdk.CreateAgentIdentity(any(), any())).thenReturn(agentIdentity);

        // Constructing the manager manually to include additional constructor logic
        manager = SimpleIdentityManager.Builder.anIdentityManager()
                .withSimpleIdentity(mockIdSdk)
                .withUserKeyName(userKeyName)
                .withUserKeyID(userKeyID)
                .withAgentKeyName(agentKeyName)
                .withAgentKeyID(agentKeyID)
                .withAuthDelegationID(authDelegationID)
                .build();

    }

    @Test
    void testNewAuthenticationToken() {
        Duration expiry = Duration.ofHours(1);
        when(mockIdSdk.CreateAgentAuthToken(any(), anyString(), anyString(), any())).thenReturn("token");
        String token = manager.newAuthenticationToken(expiry);
        assertEquals("token", token);
        verify(mockIdSdk).CreateAgentAuthToken(any(), anyString(), anyString(), any());
    }

    @Test
    void testNewAuthenticationTokenWithAudience() {
        Duration expiry = Duration.ofHours(1);
        String audience = "testAudience";
        when(mockIdSdk.CreateAgentAuthToken(any(), anyString(), anyString(), any())).thenReturn("token");
        String token = manager.newAuthenticationToken(expiry, audience);
        assertEquals("token", token);
        verify(mockIdSdk).CreateAgentAuthToken(any(), anyString(), anyString(), any());
    }

    @Test
    void testNewTwinIdentityWithControlDelegation() {
        String twinKeyName = "twinKey";
        String twinKeyID = "twinKeyID";
        Identity mockTwinIdentity = new Identity(twinKeyName, twinKeyID, "did:iotics:twin1");
        String controlDelegationID = "controlID";
        when(mockIdSdk.CreateTwinIdentityWithControlDelegation(any(), anyString(), anyString())).thenReturn(mockTwinIdentity);

        Identity result = manager.newTwinIdentityWithControlDelegation(twinKeyName, controlDelegationID);
        assertSame(mockTwinIdentity, result);
        verify(mockIdSdk).CreateTwinIdentityWithControlDelegation(any(), anyString(), anyString());
    }

    @Test
    void testNewTwinIdentity() {
        String twinKeyName = "twinKey";
        String twinKeyID = "twinKeyID";
        Identity mockTwinIdentity = new Identity(twinKeyName, twinKeyID, "did:iotics:twin1");
        when(mockIdSdk.CreateTwinIdentity(twinKeyName, twinKeyID)).thenReturn(mockTwinIdentity);

        Identity result = manager.newTwinIdentity(twinKeyName, twinKeyID);
        assertSame(mockTwinIdentity, result);
        verify(mockIdSdk).CreateTwinIdentity(twinKeyName, twinKeyID);
    }

    @Test
    void testGetAgentIdentity() {
        Identity result = manager.agentIdentity();
        assertNotNull(result); // As it is instantiated in setup
    }

    @Test
    void testGetUserIdentity() {
        Identity result = manager.userIdentity();
        assertNotNull(result); // As it is instantiated in setup
    }
}
