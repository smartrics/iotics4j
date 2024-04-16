package smartrics.iotics.host;

import com.iotics.api.Headers;
import org.bitcoinj.core.Base58;

import java.util.concurrent.ThreadLocalRandom;

public class Builders {

    public static Headers.Builder newHeadersBuilder(String did) {
        return Headers.newBuilder()
                .addTransactionRef("txRef-" + sUUID())
                .setClientAppId(did);
    }

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
