package smartrics.iotics.identity.resolver.model;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResolverDocumentTest {

    @Test
    void parse() {
        String json = """
                {
                  "doc": {
                    "@context": "https://w3id.org/did/v1",
                    "id": "did:iotics:iotWAY8e6jTtYxxS6wD8QMtJUd4qcv2kRebv",
                    "ioticsSpecVersion": "0.0.1",
                    "ioticsDIDType": "user",
                    "updateTime": 1715170080698,
                    "proof": "MEUCIDtabhNa22wdnvY7xPP5reiacIB9AaNEcTUkS/fkOWRdAiEAzqYLooBQEQDEZKzkOxhDhN0BRtofDYUeu1PUOUm8QNo=",
                    "publicKey": [
                      {
                        "id": "#user1",
                        "type": "Secp256k1VerificationKey2018",
                        "publicKeyBase58": "NqaBVKRSXzh6KifoD2mJeuEVYQjMyKpjQk6HumXagmxGWmh17QtqEb2gdYczq23X91BQBWGgeV1CFioXQBUaHCue"
                      },
                      {
                        "id": "#user2",
                        "type": "Secp256k1VerificationKey2018",
                        "publicKeyBase58": "NqaBVKRSXzh6KifoD2mJeuEVYQjMyKpjQk6HumXagmxGWmh17QtqEb2gdYczq23X91BQBWGgeV1CFioXQBUaHCue"
                      }
                    ],
                    "delegateAuthentication": [
                      {
                        "id": "#deleg1",
                        "controller": "did:iotics:iotVYnCNVbrwfP6zSWBM2BDFzf8SFKorwEiH#app1",
                        "proof": "MEUCIQC+ZiHEg2qm00RZ/pOYZ/vdGVEYM/rxGAzktQmm0s9G/gIgXA2kC4NuDcqlL9CO7l+ODvfWxTjbzrhBWXe6H9iHqgU=",
                        "proofType": "did"
                      },
                      {
                        "id": "#deleg2",
                        "controller": "did:iotics:iotVYnCNVbrwfP6zSWBM2BDFzf8SFKorwEiH#app1",
                        "proof": "MEUCIQC+ZiHEg2qm00RZ/pOYZ/vdGVEYM/rxGAzktQmm0s9G/gIgXA2kC4NuDcqlL9CO7l+ODvfWxTjbzrhBWXe6H9iHqgU=",
                        "proofType": "did"
                      }
                    ],
                    "metadata": {
                      "key": "value"
                    }
                  },
                  "aud": "https://did.stg.iotics.com",
                  "iss": "did:iotics:iotWAY8e6jTtYxxS6wD8QMtJUd4qcv2kRebv#user1"
                }
                """;
        Gson gson = new Gson();
        ResolverDocument root = gson.fromJson(json, ResolverDocument.class);

        // Now you can use root object to access any part of your JSON data
        assertEquals(root.doc().id(), "did:iotics:iotWAY8e6jTtYxxS6wD8QMtJUd4qcv2kRebv");
    }
}