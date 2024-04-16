package smartrics.iotics.identity.experimental;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JWTTest {

    @Test
    public void parsesValidTokens() {
        JWT jwt = JWT.parse("eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJodHRwczovL2RpZC5zdGcuaW90aWNzLmNvbSIsImV4cCI6MTcxMzI5Njg4NiwiaWF0IjoxNzEzMjk2ODQ1LCJpc3MiOiJkaWQ6aW90aWNzOmlvdEp4bjJBSEJrYUZYS2tCeW1iRlljVm9rR2hMU2hMdFVmMSNhcHAxIiwic3ViIjoiZGlkOmlvdGljczppb3RFQnVYcDJ3SE1SRVptd1lBeVBoRnpQWWZXdHQ5S2EyUjIifQ.4acLC5_1z_Vn3RMSFFkKezqqxI-4nwSyBtVLBXuLsEgp0mlt-FPpFKv3F80935fJozb3ng9du1Xwyh3QHjMTSA");

        Gson gson = new Gson();

        Map<?,?> header = gson.fromJson(jwt.header(), Map.class);
        assertTrue(header.containsKey("alg"));
        assertTrue(header.containsKey("typ"));

        Map<?,?> payload = gson.fromJson(jwt.payload(), Map.class);
        assertTrue(payload.containsKey("sub"));
        assertTrue(payload.containsKey("exp"));
        assertTrue(payload.containsKey("aud"));
        assertTrue(payload.containsKey("iss"));
        assertTrue(payload.containsKey("iat"));

        assertNotNull(jwt.signature());


    }

}