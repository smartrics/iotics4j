package smartrics.iotics.identity;

import smartrics.iotics.identity.go.StringResult;

final class Validator {

    static String getValueOrThrow(StringResult ret) {
        if (ret.err != null) {
            throw new SimpleIdentityException(ret.err);
        }
        return ret.value;

    }

    static void throwIfNotNull(String err) {
        if (err != null) {
            throw new SimpleIdentityException(err);
        }
    }

}
