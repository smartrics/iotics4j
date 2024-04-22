package smartrics.iotics.host;

import java.io.IOException;

/**
 * Interface for a service registry that provides mechanisms to discover service endpoints.
 * Implementations of this interface are responsible for locating and returning the endpoints
 * of various services associated with an IOTICS host. This functionality is essential for
 * dynamic service discovery in distributed systems where service endpoints might change
 * frequently or be dynamically assigned.
 *
 * <p>Implementations should handle any necessary setup, querying, and error handling to
 * reliably return up-to-date endpoint information.</p>
 */
public interface ServiceRegistry {

    /**
     * Finds and returns the endpoints associated with the host.
     * This method abstracts the details of endpoint discovery, allowing the caller to obtain
     * up-to-date information about the service endpoints without knowing the specifics of
     * the underlying discovery mechanism.
     *
     * @return {@link HostEndpoints} containing the discovered service endpoints.
     * @throws IOException if an I/O error occurs during endpoint discovery. This might happen
     *         due to network issues, problems accessing a discovery database, or configuration
     *         errors in the discovery process.
     */
    HostEndpoints find() throws IOException;
}
