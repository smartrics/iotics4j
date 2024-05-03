package smartrics.iotics.identity.resolver;

import okhttp3.*;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Base64;

/**
 * HTTP resolver client for resolving decentralized identifiers (DIDs) over HTTP.
 */
public class HttpResolverClient implements ResolverClient {

    private final URL base;
    private final OkHttpClient client;

    /**
     * Constructs an HttpResolverClient with the specified base URL and default OkHttpClient.
     *
     * @param base The base URL of the resolver.
     */
    public HttpResolverClient(URL base) {
        this(base, new OkHttpClient());
    }

    /**
     * Constructs an HttpResolverClient with the specified base URL and OkHttpClient.
     *
     * @param base   The base URL of the resolver.
     * @param client The OkHttpClient to use for HTTP requests.
     */
    public HttpResolverClient(URL base, OkHttpClient client) {
        this.base = base;
        this.client = client;
    }

    /**
     * Retrieves the OkHttpClient used by this resolver client.
     *
     * @return The OkHttpClient instance.
     */
    protected OkHttpClient getClient() {
        return this.client;
    }

    /**
     * Retrieves the document pointed by the DID by making an HTTP request to the resolver.
     *
     * @param did The decentralized identifier (DID) to discover.
     * @return The result of the discovery operation.
     * @throws IOException If an I/O error occurs during the HTTP request.
     */
    public Result discover(String did) throws IOException {
        if (did == null || did.isBlank()) {
            throw new IllegalArgumentException("Invalid input string");
        }
        URL url;
        try {
            url = URI.create(base + "/1.0/discover/" + URI.create(did)).toURL();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid input DID");
        }
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = null;
        try {
            Call call = getClient().newCall(request);
            response = call.execute();
            if (response.code() > 299) {
                if (response.code() == 404) {
                    return new Result("DID not found", "application/text", true);
                }
                try (ResponseBody body = response.body()) {
                    if (body != null) {
                        return new Result(body.string(), "application/xml", true);
                    } else {
                        return new Result("No result found", "application/text", true);
                    }
                }
            }
            try (ResponseBody body = response.body()) {
                if (body == null) {
                    return new Result("Invalid response", "application/text", true);
                }
                try {
                    String bodyString = body.string();
                    String[] parts = bodyString.split("\"");
                    String token = parts[3];
                    Base64.Decoder decoder = Base64.getDecoder();
                    String payload = new String(decoder.decode(token.split("\\.")[1]));
                    return new Result(payload, "application/json", false);
                } catch (Exception e) {
                    return new Result("Parsing error: " + e.getMessage(), "application/text", true);
                }
            }
        } finally {
            try {
                if (response != null) {
                    response.close(); // Ensure the response is closed if not done automatically
                }
            } catch (Exception e) {
                // Ignore
            }
        }
    }
}
