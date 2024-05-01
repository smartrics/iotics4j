package smartrics.iotics.connectors.twins.annotations;

import com.google.common.collect.Sets;
import smartrics.iotics.host.UriConstants;

import java.util.Set;

public class TestWithSet {
    @UriProperty(iri = UriConstants.RDFProperty.Type)
    private Set<String> uri1 = Sets.newHashSet("uri:1", "uri:2");
    @StringLiteralProperty(iri = UriConstants.RDFProperty.Type)
    private Set<String> uri2 = Sets.newHashSet("uri:1", "uri:2");
    @LangLiteralProperty(iri = UriConstants.RDFProperty.Type)
    private Set<String> uri3 = Sets.newHashSet("uri:1", "uri:2");
    @LiteralProperty(dataType = XsdDatatype.long_, iri = UriConstants.RDFProperty.Type)
    private Set<String> uri4 = Sets.newHashSet("uri:1", "uri:2");

    @UriProperty(iri = UriConstants.RDFProperty.Type)
    public Set<String> uri1() {
        return Sets.newHashSet("uri:3", "uri:4");
    }

    @StringLiteralProperty(iri = UriConstants.RDFProperty.Type)
    public Set<String> uri2() {
        return Sets.newHashSet("uri:3", "uri:4");
    }

    @LangLiteralProperty(iri = UriConstants.RDFProperty.Type)
    public Set<String> uri3() {
        return Sets.newHashSet("uri:3", "uri:4");
    }

    @LiteralProperty(dataType = XsdDatatype.long_, iri = UriConstants.RDFProperty.Type)
    public Set<String> uri4() {
        return Sets.newHashSet("uri:3", "uri:4");
    }
}
