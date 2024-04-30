package smartrics.iotics.connectors.twins.annotations;

public enum XsdDatatype {
    dateTime,
    time,
    date,
    boolean_,
    integer,
    decimal,
    float_,
    double_,
    nonPositiveInteger,
    negativeInteger,
    nonNegativeInteger,
    positiveInteger,
    long_,
    unsignedLong,
    int_,
    unsignedInt,
    short_,
    unsignedShort,
    byte_,
    unsignedByte,
    base64Binary,
    anyURI;

    @Override
    public String toString() {
        return super.toString().replaceAll("_", "");
    }
}
