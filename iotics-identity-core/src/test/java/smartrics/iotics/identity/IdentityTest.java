package smartrics.iotics.identity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdentityTest {

    @Test
    void shouldCreateIdentitySuccessfully() {
        // Test for successful creation with non-null values
        assertDoesNotThrow(() -> new Identity("key123", "John Doe", "did:example:123"));
    }

    @Test
    void shouldFailToCreateIdentityWithNullName() {
        // Test for failure when name is null
        Exception exception = assertThrows(NullPointerException.class, () -> new Identity("key123", null, "did:example:123"));
        assertEquals("Name cannot be null", exception.getMessage());
    }

    @Test
    void shouldFailToCreateIdentityWithNullKeyName() {
        // Test for failure when keyName is null
        Exception exception = assertThrows(NullPointerException.class, () -> new Identity(null, "John Doe", "did:example:123"));
        assertEquals("Key name cannot be null", exception.getMessage());
    }

    @Test
    void shouldFailToCreateIdentityWithNullDid() {
        // Test for failure when DID is null
        Exception exception = assertThrows(NullPointerException.class, () -> new Identity("key123", "John Doe", null));
        assertEquals("DID cannot be null", exception.getMessage());
    }
}
