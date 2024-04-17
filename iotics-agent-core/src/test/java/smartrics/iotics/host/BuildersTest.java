package smartrics.iotics.host;

import com.iotics.api.Headers;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

class BuildersTest {

    @Test
    void testNewHeadersBuilder() {
        String did = "dummyDID";
        Headers.Builder builder = Builders.newHeadersBuilder(did);

        assertNotNull(builder);
        assertEquals(did, builder.getClientAppId());
        assertTrue(builder.getTransactionRef(0).startsWith("txRef-"));
    }

    @Test
    void testSUUIDProducesUniqueValues() {
        Set<String> ids = new HashSet<>();
        int sampleSize = 1000; // Generate 1000 UUIDs to test uniqueness

        for (int i = 0; i < sampleSize; i++) {
            String uuid = Builders.sUUID();
            assertNotNull(uuid);
            assertFalse(ids.contains(uuid)); // Ensure no duplicates are generated
            ids.add(uuid);
        }

        assertEquals(sampleSize, ids.size()); // Confirm all generated UUIDs are unique
    }

}
