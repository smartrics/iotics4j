package smartrics.iotics.identity.jna;

import com.sun.jna.Native;


/**
 * Native library loader
 */
public class JnaSdkApiInitialiser implements SdkApiInitialiser {
    public static String pathPropertyName = "ioticsIdentityLibraryFile";

    private final SdkApi idProxy;

    public JnaSdkApiInitialiser(String path, OsLibraryPathResolver resolver) {
        this.idProxy = load(path, resolver);
    }

    /**
     * Initialiser with path from system property "ioticsIdentityLibraryFile" or, if that's null,
     */
    public JnaSdkApiInitialiser(OsLibraryPathResolver resolver) {
        String path = System.getProperty(pathPropertyName, "./lib");
        if (path != null) {
            this.idProxy = load(path, resolver);
        } else {
            throw new IllegalArgumentException("unable to load library. Missing system property 'ioticsIdentityLibraryFile', or './lib' directory");
        }
    }

    private static SdkApi load(String path, OsLibraryPathResolver resolver) {
        String libPath = resolver.resolveLibraryPath(path);
        SdkApi proxy;
        try {
            proxy = Native.load(libPath, SdkApi.class);
        } catch (UnsatisfiedLinkError e) {
            throw new IllegalStateException("unable to load library from path " + libPath);
        }
        return proxy;
    }

    /**
     * An instance of the library interface is created at construction and set as a reference in this object.
     * Not thread safe.
     *
     * @return the library interface
     */
    @Override
    public final SdkApi get() {
        return idProxy;
    }

}
