package smartrics.iotics.connectors.twins;

import io.grpc.testing.GrpcCleanupRule;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.*;

class AbstractTwinTest {

    @RegisterExtension
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();


}