package smartrics.iotics.connectors.twins.annotations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XsdDatatypeTest {

    @Test
    public void basicTypesToStringHandledWithoutUnderscore() {
        assertEquals("int", XsdDatatype.int_.toString());
        assertEquals("float", XsdDatatype.float_.toString());
        assertEquals("byte", XsdDatatype.byte_.toString());
        assertEquals("double", XsdDatatype.double_.toString());
        assertEquals("boolean", XsdDatatype.boolean_.toString());
        // another one for completeness
        assertEquals("positiveInteger", XsdDatatype.positiveInteger.toString());
    }

}