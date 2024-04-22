package smartrics.iotics.host;

import java.io.IOException;

/**
 * Represents a host in an IOTICSpace, providing access to various API endpoints.
 * The {@code Host} class uses a {@code ServiceRegistry} to dynamically locate and store
 * host endpoints upon initialization. This abstraction allows clients to interact with
 * the underlying IOTICS host without needing to know the specifics of the endpoint discovery.
 *
 * The class is designed to be initialized with a specific service registry which is responsible
 * for locating the service endpoints associated with the host. Once initialized, the host
 * endpoints can be retrieved and used for further operations.
 */
public final class Host {

    /**
     * The {@code ServiceRegistry} used to find the host's endpoints. This registry is
     * assumed to be configured externally and passed to the {@code Host} during
     * instantiation.
     */
    private final ServiceRegistry serviceRegistry;

    /**
     * Container for the endpoints associated with this host. These endpoints are
     * discovered and populated during the initialization phase and can be used
     * subsequently to interact with services offered by the host.
     */
    private HostEndpoints endpoints;

    /**
     * Constructs a new {@code Host} with the specified {@code ServiceRegistry}.
     * The service registry provided here is used later during the initialization
     * to find the relevant service endpoints.
     *
     * @param serviceRegistry the service registry to be used for finding host endpoints
     */
    public Host(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    /**
     * Initializes the host by discovering and setting up its service endpoints.
     * This method queries the configured {@code ServiceRegistry} to find the host's
     * endpoints and stores them internally.
     *
     * @throws IOException if there is a problem accessing the service registry or
     * reading the endpoints.
     */
    public void initialise() throws IOException {
        this.endpoints = this.serviceRegistry.find();
    }

    /**
     * Returns the endpoints associated with the host. These endpoints are expected
     * to be initialized prior to calling this method. If the endpoints are not
     * initialized (i.e., {@link #initialise()} has not been called), this method
     * may return {@code null}.
     *
     * @return the {@code HostEndpoints} discovered during initialization, or
     * {@code null} if not initialized.
     */
    public HostEndpoints endpoints() {
        return this.endpoints;
    }
}
