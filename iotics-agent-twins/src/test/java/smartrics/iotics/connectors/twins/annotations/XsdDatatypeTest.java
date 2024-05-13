package smartrics.iotics.connectors.twins.annotations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XsdDatatypeTest {

    @Test
    public void otherTypes() {
        assertEquals("string", XsdDatatype.string.toString());
        assertEquals("dateTime", XsdDatatype.dateTime.toString());
        assertEquals("anyURI", XsdDatatype.anyURI .toString());
        assertEquals("date", XsdDatatype.date .toString());
        assertEquals("integer", XsdDatatype.integer .toString());
        assertEquals("negativeInteger", XsdDatatype.negativeInteger .toString());
        assertEquals("nonNegativeInteger", XsdDatatype.nonNegativeInteger .toString());
        assertEquals("positiveInteger", XsdDatatype.positiveInteger .toString());
        assertEquals("nonPositiveInteger", XsdDatatype.nonPositiveInteger .toString());
        assertEquals("unsignedByte", XsdDatatype.unsignedByte .toString());
        assertEquals("unsignedLong", XsdDatatype.unsignedLong .toString());
        assertEquals("unsignedShort", XsdDatatype.unsignedShort .toString());
        assertEquals("base64Binary", XsdDatatype.base64Binary .toString());
    }

    @Test
    public void basicTypesToStringHandledWithoutUnderscore() {
        assertEquals("int", XsdDatatype.int_.toString());
        assertEquals("float", XsdDatatype.float_.toString());
        assertEquals("byte", XsdDatatype.byte_.toString());
        assertEquals("double", XsdDatatype.double_.toString());
        assertEquals("boolean", XsdDatatype.boolean_.toString());
        assertEquals("short", XsdDatatype.short_.toString());
    }

}