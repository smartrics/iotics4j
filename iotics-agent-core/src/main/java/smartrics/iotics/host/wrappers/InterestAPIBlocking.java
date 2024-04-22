package smartrics.iotics.host.wrappers;

import com.iotics.api.*;

import java.util.Iterator;

/**
 * Internalised interface of IOTICS Sync Interest API gRPC Service
 */
public interface InterestAPIBlocking {
    Iterator<FetchInterestResponse> fetchInterests(FetchInterestRequest request);

    FetchInterestResponse fetchLastStored(FetchLastStoredRequest request);

    SendInputMessageResponse sendInputMessage(SendInputMessageRequest request);
}
