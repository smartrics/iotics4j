package smartrics.iotics.host;

import com.google.gson.Gson;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static smartrics.iotics.host.DataFactory.INDEX_JS;
import static smartrics.iotics.host.DataFactory.newHostEndpoints;

public class HttpServiceRegistryTest {
    private MockWebServer mockWebServer;
    private HttpServiceRegistry serviceRegistry;

    @BeforeEach
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = mockWebServer.url("/").toString();
        serviceRegistry = new HttpServiceRegistry(baseUrl);
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void shouldFindEndpoints() throws IOException {

        HostEndpoints expectedEndpoints = newHostEndpoints();

        // Enqueue the response
        mockWebServer.enqueue(new MockResponse()
                .setBody(INDEX_JS)
                .addHeader("Content-Type", "application/json"));

        // Execute
        HostEndpoints result = serviceRegistry.find();

        // Verify
        assertEquals(expectedEndpoints, result);
    }

    @Test
    public void shouldThrowIfEndpointsNotFound() {
        // Enqueue a failure response
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        // Execute and verify
        assertThrows(IOException.class, serviceRegistry::find);
    }

    @Test
    void testValidHttpUri() {
        HttpServiceRegistry registry = new HttpServiceRegistry("http://example.com");
        assertEquals("http://example.com/index.json", registry.registryUrl());
    }

    @Test
    void testValidHttpsUri() {
        HttpServiceRegistry registry = new HttpServiceRegistry("https://example.com");
        assertEquals("https://example.com/index.json", registry.registryUrl());
    }

    @Test
    void testUriWithNoScheme() {
        HttpServiceRegistry registry = new HttpServiceRegistry("example.com");
        assertEquals("https://example.com/index.json", registry.registryUrl());
    }

    @Test
    void testUriWithPath() {
        HttpServiceRegistry registry = new HttpServiceRegistry("https://example.com/path");
        assertEquals("https://example.com/path/index.json", registry.registryUrl());
    }

    @Test
    void testUriWithPort() {
        HttpServiceRegistry registry = new HttpServiceRegistry("https://example.com:8080");
        assertEquals("https://example.com:8080/index.json", registry.registryUrl());
    }

    @Test
    void testUriWithTrailingSlash() {
        HttpServiceRegistry registry = new HttpServiceRegistry("https://example.com/");
        assertEquals("https://example.com/index.json", registry.registryUrl());
    }

    @Test
    void testEmptyUri() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new HttpServiceRegistry("");
        });
        assertEquals("URI cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testNullUri() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new HttpServiceRegistry(null);
        });
        assertEquals("URI cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testMalformedUri() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new HttpServiceRegistry("ht@tp://[invalid-url");
        });
        assertEquals("Error processing URI: ht@tp://[invalid-url", exception.getMessage());
    }
}