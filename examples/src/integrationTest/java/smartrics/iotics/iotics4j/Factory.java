package smartrics.iotics.iotics4j;

import smartrics.iotics.host.IoticsApi;
import smartrics.iotics.host.IoticsApiImpl;
import smartrics.iotics.host.grpc.HostConnection;
import smartrics.iotics.host.grpc.HostConnectionImpl;
import smartrics.iotics.identity.IdentityManager;
import smartrics.iotics.identity.SimpleIdentityImpl;
import smartrics.iotics.identity.SimpleIdentityManager;
import smartrics.iotics.identity.jna.JnaSdkApiInitialiser;
import smartrics.iotics.identity.jna.OsLibraryPathResolver;
import smartrics.iotics.identity.jna.SdkApi;

import java.time.Duration;

public final class Factory {

    public static SdkApi newSdkApi() {
        OsLibraryPathResolver pathResolver = new OsLibraryPathResolver() {
        };
        return new JnaSdkApiInitialiser("../lib", pathResolver).get();
    }

    public static IdentityManager newSIM(Configuration configuration) {
        SimpleIdentityImpl idSdk = new SimpleIdentityImpl(newSdkApi(), configuration.resolver(), configuration.seed());
        return SimpleIdentityManager.Builder.anIdentityManager()
                .withSimpleIdentity(idSdk)
                .withAgentKeyID(configuration.agentKeyID())
                .withUserKeyID(configuration.userKeyID())
                .withAgentKeyName(configuration.agentKeyName())
                .withUserKeyName(configuration.userKeyName())
                .build();
    }

    public static IoticsApi newIoticsApi(Configuration configuration) {
        HostConnection conn = new HostConnectionImpl(configuration.hostDNS(), newSIM(configuration), configuration.tokenDuration());
        return new IoticsApiImpl(conn);
    }

    public static IoticsApi newIoticsApi(Configuration configuration, IdentityManager sim) {
        HostConnection conn = new HostConnectionImpl(configuration.hostDNS(), sim, configuration.tokenDuration());
        return new IoticsApiImpl(conn);
    }

}
