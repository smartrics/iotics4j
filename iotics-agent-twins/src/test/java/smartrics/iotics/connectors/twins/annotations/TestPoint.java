package smartrics.iotics.connectors.twins.annotations;

import smartrics.iotics.host.UriConstants;

public class TestPoint {
    @PayloadValue(label = "theName")
    private final String value1;
    @StringLiteralProperty(iri = UriConstants.RDFSProperty.Label)
    private String label = "myLabel";

    public TestPoint(String v1) {
        this.value1 = v1;
    }

}
