package smartrics.iotics.identity.jna;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OsLibraryPathResolverTest {
    @Test
    @EnabledOnOs(OS.WINDOWS)
    public void testLibraryPathWin() {
        OsLibraryPathResolver resolver = new OsLibraryPathResolver(){};
        String path = resolver.resolveLibraryPath("../../lib");
        assertThat(path, containsString(".dll"));
    }
    @Test
    @EnabledOnOs(OS.LINUX)
    public void testLibraryPathLinux() {
        OsLibraryPathResolver resolver = new OsLibraryPathResolver(){};
        String path = resolver.resolveLibraryPath("../../lib");
        assertThat(path, containsString(".so"));
    }

    @Test
    @EnabledOnOs(OS.MAC)
    public void testLibraryPathMac() {
        OsLibraryPathResolver resolver = new OsLibraryPathResolver(){};
        String path = resolver.resolveLibraryPath("../../lib");
        assertThat(path, containsString(".dylib"));
    }

}
