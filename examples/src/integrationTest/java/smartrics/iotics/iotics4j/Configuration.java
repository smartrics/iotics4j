package smartrics.iotics.iotics4j;

import com.iotics.api.Property;
import smartrics.iotics.identity.jna.JnaSdkApiInitialiser;
import smartrics.iotics.identity.jna.OsLibraryPathResolver;
import smartrics.iotics.identity.jna.SdkApi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;

public record Configuration(String hostDNS, String resolver, String seed, String agentKeyName, String agentKeyID, String userKeyName, String userKeyID, String tokenDurationSec) {

    public static Configuration load() {
        Properties conf = new Properties();
        try {
            conf.load(Files.newBufferedReader(Path.of("./my.properties")));
        } catch (IOException e) {
            throw new IllegalStateException("properties file not found", e);
        }
        return new Configuration(
                conf.getProperty("host"),
                conf.getProperty("resolver"),
                conf.getProperty("seed"),
                conf.getProperty("agentKeyName"),
                conf.getProperty("agentKeyID"),
                conf.getProperty("userKeyName"),
                conf.getProperty("userKeyID"),
                conf.getProperty("tokenDurationSec")
        );
    }

    public Duration tokenDuration() {
        return Duration.ofSeconds(Long.parseLong(tokenDurationSec()));
    }

}