package smartrics.iotics.host;

import java.io.IOException;

public class Host {

    private final ServiceRegistry serviceRegistry;
    private HostEndpoints endpoints;

    public Host(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void initialise() throws IOException {
        this.endpoints = this.serviceRegistry.find();
    }

    public HostEndpoints endpoints() {
        return this.endpoints;
    }
}
