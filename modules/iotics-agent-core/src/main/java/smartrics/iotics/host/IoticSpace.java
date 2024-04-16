package smartrics.iotics.host;

import java.io.IOException;


public class IoticSpace {

    private final ServiceRegistry serviceRegistry;
    private HostEndpoints endpoints;

    public IoticSpace(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void initialise() throws IOException {
        this.endpoints = this.serviceRegistry.find();
    }

    public HostEndpoints endpoints() {
        return this.endpoints;
    }

    @Override
    public String toString() {
        return "IOTICSpace{" +
                "serviceRegistry=" + serviceRegistry +
                ", endpoints=" + endpoints +
                '}';
    }
}
