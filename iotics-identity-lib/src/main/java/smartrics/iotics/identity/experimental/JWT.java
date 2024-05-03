package smartrics.iotics.identity.experimental;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.TimeZone;

/**
 * Represents a JSON Web Token (JWT) consisting of header, payload, and signature components.
 */
public record JWT(String header, String payload, String signature) {

    /**
     * Parses a JWT token string into a JWT object.
     *
     * @param token The JWT token string to parse.
     * @return The parsed JWT object.
     * @throws IllegalArgumentException If the token is invalid.
     */
    public static JWT parse(String token) {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();

        try {
            String header = new String(decoder.decode(chunks[0]));
            String payload = new String(decoder.decode(chunks[1]));
            String signature = chunks[2];
            return new JWT(header, payload, signature);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    /**
     * Returns a nicely formatted string representation of the JWT.
     *
     * @return A string representation of the JWT with decoded timestamps.
     * @throws RuntimeException If the token is invalid or cannot be decoded.
     */
    public String toNiceString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject h = gson.fromJson(this.header, JsonObject.class);

        try {
            JsonObject p = gson.fromJson(this.payload, JsonObject.class);
            long exp = p.get("exp").getAsLong();
            long iat = p.get("iat").getAsLong();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            String sExp = simpleDateFormat.format(new Date(exp * 1000));
            String sIat = simpleDateFormat.format(new Date(iat * 1000));
            p.remove("exp");
            p.remove("iat");
            p.addProperty("exp", sExp);
            p.addProperty("iat", sIat);

            JsonObject obj = gson.fromJson("{}", JsonObject.class);
            obj.add("header", h);
            obj.add("payload", p);
            obj.addProperty("signature", this.signature);
            return obj.toString();
        } catch (Exception e) {
            throw new RuntimeException("Invalid token", e);
        }
    }
}
