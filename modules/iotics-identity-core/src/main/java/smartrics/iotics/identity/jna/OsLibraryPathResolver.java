package smartrics.iotics.identity.jna;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface OsLibraryPathResolver {
    default String resolveLibraryPath(String path) {
        String osName = System.getProperty("os.name").toLowerCase();
        String fileName = "lib-iotics-id-sdk";
        String file;
        if (osName.contains("win")) {
            file = fileName + ".dll";
        } else if (osName.contains("mac")) {
            file = fileName + ".dylib";
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            file = fileName + ".so";
        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + osName);
        }
        if(path == null) {
            throw new IllegalArgumentException("null path");
        }
        File p = Path.of(path).toFile();
        if(!p.exists() || !p.canRead()) {
            throw new IllegalArgumentException("invalid path or not accessible:  " + p.getAbsolutePath());
        }

        File libFile = Paths.get(path, file).toFile();
        if(libFile.exists() && libFile.canRead()) {
            return libFile.toPath().toString();
        }
        throw new IllegalArgumentException("Unable to find a library file at '" + path + "' for os '" + osName + "'");
    }
}
