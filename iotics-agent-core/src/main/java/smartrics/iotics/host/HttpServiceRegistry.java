package smartrics.iotics.host;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Provides an implementation of {@link ServiceRegistry} for accessing service registry information
 * over HTTP. This class uses the OkHttpClient to make HTTP requests and Gson to parse the
 * responses into {@link HostEndpoints} objects.
 */
public class HttpServiceRegistry implements ServiceRegistry {
    private final OkHttpClient httpClient;
    private final String registryUrl;


    /**
     * Constructs a new HttpServiceRegistry with a specified URI.
     * Initializes an {@link OkHttpClient} for HTTP requests and constructs the registry URL
     * based on the provided URI.
     *
     * @param uri The DNS name where the service registry is hosted, for example {@code myspace.iotics.space}.
     *            This DNS should point to the provided IOTICSpace which returns a JSON representation of {@link HostEndpoints}.
     */
    public HttpServiceRegistry(String uri) {
        this.httpClient = new OkHttpClient();
        this.registryUrl = makeRegistryURI(uri);
    }

    /**
     * Fetches and parses the service registry data from the predefined URL.
     * Uses OkHttpClient to execute a GET request and Gson to parse the JSON response
     * into a {@link HostEndpoints} object.
     *
     * @return The {@link HostEndpoints} object representing service endpoints.
     * @throws IOException If there is an error in fetching or parsing the service registry data.
     */
    @Override
    public HostEndpoints find() throws IOException {
        Request request = new Request.Builder()
                .url(this.registryUrl)
                .build();

        try (Response response = this.httpClient.newCall(request).execute()) {
            if(response.code()>399) {
                throw new IOException("not found");
            }
            Gson gson = new Gson();
            assert response.body() != null;
            return gson.fromJson(response.body().string(), HostEndpoints.class);
        }
    }

    /**
     * Returns the full URL to the service registry.
     *
     * @return The URL as a string.
     */
    public String registryUrl() {
        return registryUrl;
    }

    /**
     * Provides a string representation of the HttpServiceRegistry, including the DNS and
     * registry URL.
     *
     * @return A string representation of this HttpServiceRegistry.
     */
    @Override
    public String toString() {
        return "HttpServiceRegistry{" +
                "registryUrl='" + registryUrl + '\'' +
                '}';
    }

    private static String makeRegistryURI(String uri) {

        if (uri == null || uri.trim().isEmpty()) {
            throw new IllegalArgumentException("URI cannot be null or empty.");
        }

        try {
            URI parsedUri = URI.create(uri.trim());
            if (parsedUri.getScheme() == null) {
                // Assume HTTPs if no scheme is present
                parsedUri = URI.create(String.format("https://%s", parsedUri));
            }
            String path = parsedUri.getPath();
            String end;
            if (path == null || !path.endsWith("/")) {
                end = "/index.json"; // Default to root if no path.
            } else {
                end = "index.json"; // Append directly if there's already a trailing slash
            }
            // Use resolve to append paths correctly
            return parsedUri + end;
        } catch (IllegalArgumentException e) {
            // Handle specific URI creation issues
            throw new IllegalArgumentException("Error processing URI: " + uri, e);
        }
    }
}
