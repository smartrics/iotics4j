package smartrics.iotics.host;

public record HostEndpoints(
        String resolver,
        String stomp,
        String qapi,
        String grpc,
        String grpcWeb,
        HostEndpointVersion version) {
}

