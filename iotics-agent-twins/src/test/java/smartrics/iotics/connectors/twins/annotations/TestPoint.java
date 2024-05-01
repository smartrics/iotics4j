package smartrics.iotics.connectors.twins.annotations;

import smartrics.iotics.host.UriConstants;

public class TestPoint {
    @PayloadValue(label = "theName")
    private final String value1;
    @StringLiteralProperty(iri = UriConstants.ON_RDFS_LABEL_PROP)
    private String label = "myLabel";

    public TestPoint(String v1) {
        this.value1 = v1;
    }

}
