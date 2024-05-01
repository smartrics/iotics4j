package smartrics.iotics.connectors.twins.annotations;

import smartrics.iotics.host.UriConstants;

public class TestClass {
    @StringLiteralProperty(iri = UriConstants.RDFSProperty.Label)
    private final String prop1 = "myFeedLabelProp";

    @LangLiteralProperty(iri = UriConstants.RDFSProperty.Label)
    private final String prop2 = "myFeedLabelProp";

    @LiteralProperty(dataType = XsdDatatype.decimal, iri = UriConstants.RDFSProperty.Label)
    private final String prop3 = "myFeedLabelProp";

    @UriProperty(iri = UriConstants.RDFSProperty.Label)
    private final String prop4 = "myFeedLabelProp";


    @StringLiteralProperty(iri = UriConstants.RDFSProperty.Label)
    private String prop1() {
        return "myFeedLabelMethod";
    }

    @LangLiteralProperty(iri = UriConstants.RDFSProperty.Label)
    private String prop2() {
        return "myFeedLabelMethod";
    }

    @LiteralProperty(dataType = XsdDatatype.decimal, iri = UriConstants.RDFSProperty.Label)
    private String prop3() {
        return "myFeedLabelMethod";
    }

    @UriProperty(iri = UriConstants.RDFSProperty.Label)
    private String prop4() {
        return "myFeedLabelMethod";
    }


    @Feed(id = "myFeed")
    public TestPoint getFeed() {
        return new TestPoint("published");
    }

    @Input(id = "myInput")
    public TestPoint getInput() {
        return new TestPoint("received");
    }

}
