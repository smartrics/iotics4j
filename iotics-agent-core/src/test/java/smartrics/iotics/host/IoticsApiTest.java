package smartrics.iotics.host;

import io.grpc.ManagedChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import smartrics.iotics.host.grpc.HostConnection;

import java.time.Duration;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IoticsApiTest {

    @Mock
    private HostConnection mockConnection;

    @Mock
    private ManagedChannel mockChannel;

    private IoticsApi ioticsApi;

    @BeforeEach
    void setUp() {
        when(mockConnection.getGrpcChannel()).thenReturn(mockChannel);
        // Assume ManagedChannelBuilder is properly mocked to return mockChannel
        ioticsApi = new IoticsApi(mockConnection);
    }

    @Test
    void testStop() throws InterruptedException {
        Duration d = Duration.ofSeconds(10);
        ioticsApi.stop(d);
        verify(mockConnection).shutdown(d);
    }

    // Additional tests can be added to validate other functionalities.
}
