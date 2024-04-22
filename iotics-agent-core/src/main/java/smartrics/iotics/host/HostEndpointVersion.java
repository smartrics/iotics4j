package smartrics.iotics.host;

/**
 * Immutable record that holds version information for different components or services associated with an IOTICS host and IOTICSpace.
 *
 * <p>Fields:</p>
 * <ul>
 *   <li>{@code space}: Version of the space component of the host.</li>
 *   <li>{@code host}: Version of the host component of the host.</li>
 * </ul>
 */
public record HostEndpointVersion(
        String space,
        String host
) {
}
