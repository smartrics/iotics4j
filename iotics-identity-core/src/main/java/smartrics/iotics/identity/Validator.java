package smartrics.iotics.identity;

import smartrics.iotics.identity.go.StringResult;

/**
 * Utility class for validating and handling errors.
 */
final class Validator {

    /**
     * Retrieves the value from a StringResult or throws a SimpleIdentityException if an error is present.
     *
     * @param ret The StringResult object containing the value and error.
     * @return The value extracted from StringResult.
     * @throws SimpleIdentityException If an error is present in StringResult.
     */
    static String getValueOrThrow(StringResult ret) {
        if (ret.err != null) {
            throw new SimpleIdentityException(ret.err);
        }
        return ret.value;
    }

    /**
     * Throws a SimpleIdentityException if the provided error string is not null.
     *
     * @param err The error string to check.
     * @throws SimpleIdentityException If the error string is not null.
     */
    static void throwIfNotNull(String err) {
        if (err != null) {
            throw new SimpleIdentityException(err);
        }
    }
}