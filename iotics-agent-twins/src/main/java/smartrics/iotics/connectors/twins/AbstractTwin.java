package smartrics.iotics.connectors.twins;

import smartrics.iotics.host.IoticsApi;
import smartrics.iotics.identity.Identity;
import smartrics.iotics.identity.IdentityManager;

public abstract class AbstractTwin implements Identifiable, Maker {

    private final Identity myIdentity;
    private final IoticsApi api;
    private final IdentityManager sim;

    public AbstractTwin(IoticsApi api, IdentityManager sim, Identity myIdentity) {
        this.api = api;
        this.myIdentity = myIdentity;
        this.sim = sim;
    }

    @Override
    public Identity getMyIdentity() {
        return this.myIdentity;
    }

    public Identity getAgentIdentity() {
        return this.sim.agentIdentity();
    }

    @Override
    public IoticsApi ioticsApi() {
        return api;
    }

}
