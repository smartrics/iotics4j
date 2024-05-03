package smartrics.iotics.identity.resolver;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URL;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class HttpResolverClientTest {
    private MockWebServer mockWebServer;
    private HttpResolverClient client;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        URL baseUrl = mockWebServer.url("/").url();
        client = new HttpResolverClient(baseUrl, new OkHttpClient());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testDiscoverValidDIDNotFound() throws IOException {
        // Setup Mock Web Server Response
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        ResolverClient.Result result = client.discover("validDid");
        assertEquals("DID not found", result.content());
        assertTrue(result.isErr());
    }

    @Test
    void testDiscoverValidResponse() throws IOException {
        // Encode the payload
        String encodedPayload = Base64.getEncoder().encodeToString("you are validated".getBytes());
        String jsonResponse = "{\"token\":\"eyJhbGciOiJIUzI1NiJ9." + encodedPayload + ".dummySignature\"}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json"));

        ResolverClient.Result result = client.discover("validDid");
        assertNotNull(result);
        assertFalse(result.isErr());
        assertEquals("you are validated", result.content());
    }


    @Test
    void testDiscoverWithNullDID() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> client.discover(null));
        assertEquals("Invalid input string", exception.getMessage());
    }

    @Test
    void testDiscoverWithBlankDID() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> client.discover(" "));
        assertEquals("Invalid input string", exception.getMessage());
    }

    @Test
    void testDiscoverWithInvalidDID() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> client.discover("::"));
        assertEquals("Invalid input DID", exception.getMessage());
    }
}
