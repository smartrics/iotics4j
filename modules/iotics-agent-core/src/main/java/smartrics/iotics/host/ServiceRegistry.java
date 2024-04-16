package smartrics.iotics.host;

import java.io.IOException;

public interface ServiceRegistry {
    HostEndpoints find() throws IOException;
}
