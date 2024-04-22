package smartrics.iotics.host;

/**
 * Immutable record that holds various service endpoint URLs and their version for a host in an IOTICSpace.
 * This record is typically used to store the network addresses of different services provided by an host,
 * including both standard and web interfaces for gRPC, among others.
 *
 * <p>Fields:</p>
 * <ul>
 *   <li>{@code resolver}: URL for the resolver endpoint.</li>
 *   <li>{@code stomp}: URL for the STOMP protocol service endpoint.</li>
 *   <li>{@code qapi}: URL for the IOTICS API REST HTTP/1.1.</li>
 *   <li>{@code grpc}: URL for the gRPC API.</li>
 *   <li>{@code grpcWeb}: URL for the gRPC-Web service endpoint.</li>
 *   <li>{@code version}: Version of the endpoints encapsulated by this record.</li>
 * </ul>
 */
public record HostEndpoints(
        String resolver,
        String stomp,
        String qapi,
        String grpc,
        String grpcWeb,
        HostEndpointVersion version) {
}

