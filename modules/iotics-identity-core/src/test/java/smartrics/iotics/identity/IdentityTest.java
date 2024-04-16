package smartrics.iotics.identity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class IdentityTest {

    @Test
    public void wontBuildWithNullInputs() {
        assertThrows(NullPointerException.class, () -> new Identity(null, "", ""));
        assertThrows(NullPointerException.class, () -> new Identity("", null, ""));
        assertThrows(NullPointerException.class, () -> new Identity("", "", null));
    }

}