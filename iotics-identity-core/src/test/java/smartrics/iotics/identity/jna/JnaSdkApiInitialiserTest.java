package smartrics.iotics.identity.jna;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JnaSdkApiInitialiserTest {

    static {

        System.setProperty(JnaSdkApiInitialiser.pathPropertyName, "../../lib");

    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    public void libraryLoadingOnWindows() {
        OsLibraryPathResolver resolver = new OsLibraryPathResolver() {
        };
        JnaSdkApiInitialiser initialiser = new JnaSdkApiInitialiser("../lib", resolver);
        SdkApi api = initialiser.get();
        assertNotNull(api, "The SDK API should not be null on Windows");
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    public void failLibraryLoadingOnWindows() {
        OsLibraryPathResolver resolver = new OsLibraryPathResolver() {
        };
        assertThrows(IllegalArgumentException.class, () -> new JnaSdkApiInitialiser(".", resolver));
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    public void libraryLoadingOnLinux() {
        OsLibraryPathResolver resolver = new OsLibraryPathResolver() {
        };
        JnaSdkApiInitialiser initialiser = new JnaSdkApiInitialiser(resolver);
        SdkApi api = initialiser.get();
        assertNotNull(api, "The SDK API should not be null on Linux");
    }

}