package smartrics.iotics.identity.resolver;

import java.net.URI;

public class HttpResolverClientApp {
    public static void main(String[] args) throws Exception {
        HttpResolverClient c = new HttpResolverClient(URI.create(args[0]).toURL());
        ResolverClient.Result agent = c.discover(args[1]);
        ResolverClient.Result user = c.discover(args[2]);

        System.out.println("AGENT ------");
        System.out.println(agent);
        System.out.println("USER ------");
        System.out.println(user);
    }

}
