package smartrics.iotics.connectors.twins;

import smartrics.iotics.host.IoticsApi;
import smartrics.iotics.identity.Identity;

public class BaseTwin implements Identifiable, ApiUser {

    private final IoticsApi api;

    BaseTwin(IoticsApi api) {
        this.api = api;
    }

    @Override
    public IoticsApi ioticsApi() {
        return api;
    }

    @Override
    public Identity getMyIdentity() {
        return new Identity("keyName", "Twin123", "did123");
    }

    @Override
    public Identity getAgentIdentity() {
        return new Identity("keyName", "Agent Smith", "agentDid123");
    }
}
