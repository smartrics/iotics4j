package smartrics.iotics.identity.resolver;

import java.io.IOException;

/**
 * Interface for resolving decentralized identifiers (DIDs).
 */
public interface ResolverClient {

    /**
     * Retrieves the document linked to the decentralized identifier (DID).
     *
     * @param did The decentralized identifier (DID) to discover.
     * @return A Result object representing the document.
     * @throws IOException If an I/O error occurs during the discovery process.
     */
    Result discover(String did) throws IOException;

    /**
     * Represents the result of a resolution operation, containing the content, content type, and error flag.
     */
    record Result(String content, String contentType, boolean isErr) {
    }
}
