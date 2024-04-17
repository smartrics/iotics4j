package smartrics.iotics.host;

import com.iotics.api.Headers;
import org.bitcoinj.core.Base58;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class containing methods for building headers and generating secure, unique identifiers.
 * This class supports creating header objects and provides a method for generating a random
 * Base58 encoded UUID.
 */
public class Builders {

    /**
     * Creates a new {@link Headers.Builder} instance with a specific client application ID and a
     * transaction reference.
     *
     * @param did The decentralized identifier (DID) that will be set as the client application ID.
     * @return A {@link Headers.Builder} initialized with a transaction reference and client application ID.
     *         The transaction reference is generated using the {@link #sUUID()} method prefixed with "txRef-".
     */
    public static Headers.Builder newHeadersBuilder(String did) {
        return Headers.newBuilder()
                .addTransactionRef("txRef-" + sUUID())
                .setClientAppId(did);
    }

    /**
     * Generates a secure, unique identifier using random bytes that are Base58 encoded.
     * This method uses a {@link ThreadLocalRandom} to generate a byte array, which is then
     * encoded into a Base58 string.
     *
     * @return A unique Base58-encoded string representing the generated identifier.
     * @throws IllegalStateException If there is an error initializing the secure random generator
     *         or during the generation process.
     */
    public static String sUUID() {
        try {
            final byte[] randomBytes = new byte[10];
            ThreadLocalRandom.current().nextBytes(randomBytes);
            return Base58.encode(randomBytes);
        } catch (Exception e) {
            throw new IllegalStateException("unable to init secure random", e);
        }
    }
}
