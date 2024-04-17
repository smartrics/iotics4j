package smartrics.iotics.host;

import com.google.gson.Gson;

public class DataFactory {
    static final String INDEX_JS = """
            {
              "resolver": "https://resolver.iotics.com",
              "stomp": "wss://myspace.iotics.space/ws",
              "qapi": "https://myspace.iotics.space/qapi",
              "grpc": "myspace.iotics.space:443",
              "grpc-web": "https://myspace.iotics.space",
              "version": {
                "space": "1.0.0",
                "host": "1.0.0"
              }
            }""";


    public static HostEndpoints newHostEndpoints() {
        return new Gson().fromJson(INDEX_JS, HostEndpoints.class);
    }

}
