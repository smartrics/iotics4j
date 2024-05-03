package smartrics.iotics.identity;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Objects of this class can be used by clients to load IdentityConfig.
 */
public record SimpleConfig(String seed, String keyName, String keyId) {

    /**
     * Constructs a SimpleConfig object with the specified seed and key name.
     *
     * @param seed    The seed value.
     * @param keyName The key name.
     */
    public SimpleConfig(String seed, String keyName) {
        this(seed, requireNonNull(keyName), null);
    }

    /**
     * Constructs a SimpleConfig object with the specified seed, key name, and key ID.
     *
     * @param seed    The seed value.
     * @param keyName The key name.
     * @param keyId   The key ID.
     */
    public SimpleConfig(String seed, String keyName, String keyId) {
        this.seed = requireNonNull(seed);
        this.keyName = requireNonNull(keyName);
        this.keyId = Optional.ofNullable(keyId).orElse("#id-" + keyName.hashCode());
    }

    /**
     * Reads the config from the environment variables.
     * The environment variable names are obtained by concatenating a prefix with "SEED" for the seed,
     * "KEYNAME" for the key name, and "KEYID" for the key ID.
     *
     * @param prefix The prefix for the environment variables.
     * @return The config loaded from the environment variables.
     */
    public static SimpleConfig fromEnv(String prefix) {
        return new SimpleConfig(
                System.getenv(prefix + "SEED"),
                System.getenv(prefix + "KEYNAME"),
                System.getenv(prefix + "KEYID"));
    }

    /**
     * Reads the config from a file specified by the provided path.
     *
     * @param p The path to the config file.
     * @return The config loaded from the file.
     * @throws FileNotFoundException If the file is not found.
     */
    public static SimpleConfig readConf(Path p) throws FileNotFoundException {
        Gson gson = new Gson();
        Reader reader = Files.newReader(p.toFile(), StandardCharsets.UTF_8);
        return gson.fromJson(reader, SimpleConfig.class);
    }

    /**
     * Reads the config from a file located in the user's home directory under the specified directory and name.
     *
     * @param name The name of the config file.
     * @return The config loaded from the file.
     * @throws FileNotFoundException If the file is not found.
     */
    public static SimpleConfig readConfFromHome(String name) throws FileNotFoundException {
        Path p = Paths.get(System.getProperty("user.home"), ".config", "iotics", name);
        return readConf(p);
    }

    /**
     * Retrieves the seed value.
     *
     * @return The seed value.
     */
    public String seed() {
        return seed;
    }

    /**
     * Retrieves the key name.
     *
     * @return The key name.
     */
    public String keyName() {
        return keyName;
    }

    /**
     * Retrieves the key ID.
     *
     * @return The key ID.
     */
    public String keyId() {
        return keyId;
    }

    /**
     * Checks if the config is valid, i.e., if none of the seed, key name, or key ID is null or empty.
     *
     * @return true if the config is valid; otherwise, false.
     */
    public boolean isValid() {
        return !Strings.isNullOrEmpty(this.seed) && !Strings.isNullOrEmpty(this.keyName) && !Strings.isNullOrEmpty(this.keyId);
    }
}
