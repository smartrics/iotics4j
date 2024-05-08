package smartrics.iotics.iotics4j.identitycore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import smartrics.iotics.identity.Identity;
import smartrics.iotics.identity.SimpleIdentity;
import smartrics.iotics.identity.SimpleIdentityImpl;
import smartrics.iotics.identity.experimental.JWT;
import smartrics.iotics.identity.go.StringResult;
import smartrics.iotics.identity.jna.SdkApi;
import smartrics.iotics.identity.resolver.DocumentResult;
import smartrics.iotics.identity.resolver.HttpResolverClient;
import smartrics.iotics.identity.resolver.Resolver;
import smartrics.iotics.identity.resolver.model.ResolverDocument;
import smartrics.iotics.iotics4j.Configuration;
import smartrics.iotics.iotics4j.Factory;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class IdentityTest {

    private static Configuration configuration;
    private static SdkApi idApi;
    private static SimpleIdentity idSdk;
    private static Resolver resolver;
    private static Gson gson;

    @BeforeAll
    static void init() throws Exception {
        configuration = Configuration.load();
        idApi = Factory.newSdkApi();
        idSdk = new SimpleIdentityImpl(idApi, configuration.resolver(), configuration.seed());
        resolver = new Resolver(new HttpResolverClient(URI.create(configuration.resolver()).toURL()));
        gson = new GsonBuilder().setPrettyPrinting().create();

    }

    @Test
    void delegation() throws IOException {
        Identity userIdentity = idSdk.CreateUserIdentity(configuration.userKeyName(), configuration.userKeyID());
        Identity agentIdentity = idSdk.CreateAgentIdentity(configuration.agentKeyName(), configuration.agentKeyID());
        System.out.println("CreateUserIdentity: " + userIdentity);
        System.out.println("Agent identity: " + agentIdentity);
        idSdk.UserDelegatesAuthenticationToAgent(agentIdentity, userIdentity, "#deleg1");

        Identity twinIdentity = idSdk.CreateTwinIdentityWithControlDelegation(agentIdentity, "tKey1", "#tName");
        System.out.println("CreateTwinDidWithControlDelegation: " + twinIdentity);

        Identity anotherAgentIdentity = idSdk.CreateAgentIdentity("aKey2", "#app2");
        System.out.println("CreateAgentIdentity: " + anotherAgentIdentity);

        idSdk.TwinDelegatesControlToAgent(anotherAgentIdentity, twinIdentity, "#deleg2");

        DocumentResult uRes = resolver.discover(userIdentity.did());
        System.out.println(uRes.rawDocument());
        DocumentResult aRes = resolver.discover(agentIdentity.did());
        System.out.println(aRes.rawDocument());
        DocumentResult tRes = resolver.discover(twinIdentity.did());
        System.out.println(tRes.rawDocument());

        ResolverDocument uDoc = gson.fromJson(uRes.rawDocument(), ResolverDocument.class);
        assertThat(uDoc.doc().delegateAuthentication().getFirst().id(), is(equalTo("#deleg1")));

        ResolverDocument tDoc = gson.fromJson(tRes.rawDocument(), ResolverDocument.class);
        assertThat(tDoc.doc().delegateControl().stream().filter(p -> p.id().equals("#deleg2")).findFirst().orElseThrow().controller(), is(equalTo(anotherAgentIdentity.did() + "#app2")));
    }

    @Test
    void token() {
        Identity agentIdentity = idSdk.CreateUserIdentity(configuration.agentKeyName(), configuration.agentKeyID());
        Identity userIdentity = idSdk.CreateUserIdentity(configuration.userKeyName(), configuration.userKeyID());

        String token = idSdk.CreateAgentAuthToken(agentIdentity, userIdentity.did(), Duration.ofSeconds(11));
        System.out.println(token);
        System.out.println("CreateAgentAuthToken: " + JWT.parse(token).toNiceString());
        token = idSdk.CreateAgentAuthToken(agentIdentity, userIdentity.did(), "random", Duration.ofSeconds(9));
        System.out.println("CreateAgentAuthToken: " + JWT.parse(token).toNiceString());
    }

    @Test
    void seeds() {
        StringResult seed = idApi.CreateDefaultSeed();
        assertNull(seed.err);
        System.out.println("CreateDefaultSeed: " + seed.value);

        StringResult mnemonic = idApi.SeedBip39ToMnemonic(seed.value);
        assertNull(mnemonic.err);
        System.out.println("SeedBip39ToMnemonic: " + mnemonic.value);

        StringResult oldSeed = idApi.MnemonicBip39ToSeed(mnemonic.value);
        assertNull(mnemonic.err);
        System.out.println("MnemonicBip39ToSeed: " + oldSeed.value);
    }

    @Test
    void identities() {
        Identity agentIdentity = idSdk.CreateAgentIdentity("aKey1", "#app1");
        System.out.println("CreateAgentIdentity: " + agentIdentity);

        Identity userIdentity = idSdk.CreateUserIdentity("uKey1", "#user1");
        System.out.println("CreateUserIdentity: " + userIdentity);

        Identity twinIdentity = idSdk.CreateTwinIdentity("tKey1", "#tName");
        System.out.println("CreateTwinDidWithControlDelegation: " + twinIdentity);

        DocumentResult uRes = resolver.discover(userIdentity.did());
        System.out.println(uRes.rawDocument());
        DocumentResult aRes = resolver.discover(agentIdentity.did());
        System.out.println(aRes.rawDocument());
        DocumentResult tRes = resolver.discover(twinIdentity.did());
        System.out.println(tRes.rawDocument());

    }

}
