package smartrics.iotics.connectors.twins.annotations;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import smartrics.iotics.host.UriConstants;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GenericInvokerTest {
    @ParameterizedTest
    @ValueSource(classes = {
            StringLiteralProperty.class,
            LangLiteralProperty.class,
            LiteralProperty.class,
            UriProperty.class})
    void testRetrievesFromPropAndMethod(Class<Annotation> c) {
        List<AnnotationData> result = GenericInvoker.collectAnnotatedMemberValues(new TestClass(), c);
        assertThat(result.size(), is(equalTo(2)));
        List<String> values = result.stream().map(annotationData -> annotationData.annotatedElementValue().toString()).toList();
        assertThat(values, contains("myFeedLabelProp", "myFeedLabelMethod"));
        result.forEach(data -> assertThat(data.annotationKvp().get("iri"), is(equalTo(UriConstants.ON_RDFS_LABEL_PROP))));

        result.stream().filter(annotationData -> annotationData.annotation() instanceof LiteralProperty)
                .forEach(data -> {
                    assertThat(data.annotationKvp().get("dataType"), is(equalTo(XsdDatatype.decimal)));
                });
        result.stream().filter(annotationData -> annotationData.annotation() instanceof LangLiteralProperty)
                .forEach(data -> {
                    assertThat(data.annotationKvp().get("lang"), is(equalTo("en")));
                });
    }

    @ParameterizedTest
    @ValueSource(classes = {
            StringLiteralProperty.class,
            LangLiteralProperty.class,
            LiteralProperty.class,
            UriProperty.class})
    void testRetrievesFromPropAndMethodWithSet(Class<Annotation> c) {
        List<AnnotationData> result = GenericInvoker.collectAnnotatedMemberValues(new TestWithSet(), c);
        assertThat(result.size(), is(equalTo(2)));
        List<String> values = result.stream().flatMap((Function<AnnotationData, Stream<String>>) annotationData -> ((Set<String>)annotationData.annotatedElementValue()).stream()).toList();
        assertThat(values, containsInAnyOrder("uri:1", "uri:2", "uri:3", "uri:4"));
        result.forEach(data -> assertThat(data.annotationKvp().get("iri"), is(equalTo(UriConstants.ON_RDF_TYPE_PROP))));
    }

    @Test
    void testNonExistingProperty() {
        List<AnnotationData> result = GenericInvoker.collectAnnotatedMemberValues(new Object(), LangLiteralProperty.class);
        assertThat(result.size(), is(equalTo(0)));
    }

    @Test
    void testMethodWithException() {

        class TestWithException {
            @StringLiteralProperty(iri = UriConstants.ON_RDFS_LABEL_PROP)
            String method() {
                throw new RuntimeException();
            }
        }
        assertThrows(RuntimeException.class,
                () -> GenericInvoker.collectAnnotatedMemberValues(new TestWithException(), StringLiteralProperty.class));
    }

    @Test
    void testParsingFeed() {
        List<AnnotationData> result = GenericInvoker.collectAnnotatedMemberValues(new TestClass(), Feed.class);
        AnnotationData feed = result.getFirst();
        assertThat(feed.annotationKvp().get("id"), is(equalTo("myFeed")));
        Object instance = feed.annotatedElementValue();
        List<AnnotationData> feedResult = GenericInvoker.collectAnnotatedMemberValues(instance, StringLiteralProperty.class);
        assertThat(feedResult.getFirst().annotatedElementValue(), is(equalTo("myLabel")));
        List<AnnotationData> feedValue = GenericInvoker.collectAnnotatedMemberValues(instance, PayloadValue.class);
        assertThat(feedValue.getFirst().annotationKvp ().get("id"), is(equalTo("theName")));
        assertThat(feedValue.getFirst().annotatedElementValue(), is(equalTo("published")));
    }

    @Test
    void testParsingInput() {
        List<AnnotationData> result = GenericInvoker.collectAnnotatedMemberValues(new TestClass(), Input.class);
        AnnotationData feed = result.getFirst();
        assertThat(feed.annotationKvp().get("id"), is(equalTo("myInput")));
        Object instance = feed.annotatedElementValue();
        List<AnnotationData> feedResult = GenericInvoker.collectAnnotatedMemberValues(instance, StringLiteralProperty.class);
        assertThat(feedResult.getFirst().annotatedElementValue(), is(equalTo("myLabel")));
        List<AnnotationData> feedValue = GenericInvoker.collectAnnotatedMemberValues(instance, PayloadValue.class);
        assertThat(feedValue.getFirst().annotationKvp ().get("id"), is(equalTo("theName")));
        assertThat(feedValue.getFirst().annotatedElementValue(), is(equalTo("received")));
    }

    static class TestWithSet {
        @UriProperty(iri = UriConstants.ON_RDF_TYPE_PROP)
        private Set<String> uri1 = Sets.newHashSet("uri:1", "uri:2");
        @StringLiteralProperty(iri = UriConstants.ON_RDF_TYPE_PROP)
        private Set<String> uri2 = Sets.newHashSet("uri:1", "uri:2");
        @LangLiteralProperty(iri = UriConstants.ON_RDF_TYPE_PROP)
        private Set<String> uri3 = Sets.newHashSet("uri:1", "uri:2");
        @LiteralProperty(dataType = XsdDatatype.long_, iri = UriConstants.ON_RDF_TYPE_PROP)
        private Set<String> uri4 = Sets.newHashSet("uri:1", "uri:2");

        @UriProperty(iri = UriConstants.ON_RDF_TYPE_PROP)
        private Set<String> uri1() { return Sets.newHashSet("uri:3", "uri:4"); }
        @StringLiteralProperty(iri = UriConstants.ON_RDF_TYPE_PROP)
        private Set<String> uri2() { return Sets.newHashSet("uri:3", "uri:4"); }
        @LangLiteralProperty(iri = UriConstants.ON_RDF_TYPE_PROP)
        private Set<String> uri3() { return Sets.newHashSet("uri:3", "uri:4"); }
        @LiteralProperty(dataType = XsdDatatype.long_, iri = UriConstants.ON_RDF_TYPE_PROP)
        private Set<String> uri4() { return Sets.newHashSet("uri:3", "uri:4"); }
    }

    static class TestPoint {
        @StringLiteralProperty(iri = UriConstants.ON_RDFS_LABEL_PROP)
        private String label = "myLabel";

        @PayloadValue(id = "theName", unit = "")
        private final String value1;

        public TestPoint(String v1) {
            this.value1 = v1;
        }

    }

    static class TestClass {
        @StringLiteralProperty(iri = UriConstants.ON_RDFS_LABEL_PROP)
        private final String prop1 = "myFeedLabelProp";

        @LangLiteralProperty(iri = UriConstants.ON_RDFS_LABEL_PROP)
        private final String prop2 = "myFeedLabelProp";

        @LiteralProperty(dataType = XsdDatatype.decimal, iri = UriConstants.ON_RDFS_LABEL_PROP)
        private final String prop3 = "myFeedLabelProp";

        @UriProperty(iri = UriConstants.ON_RDFS_LABEL_PROP)
        private final String prop4 = "myFeedLabelProp";


        @StringLiteralProperty(iri = UriConstants.ON_RDFS_LABEL_PROP)
        private String prop1() {
            return "myFeedLabelMethod";
        }

        @LangLiteralProperty(iri = UriConstants.ON_RDFS_LABEL_PROP)
        private String prop2() {
            return "myFeedLabelMethod";
        }

        @LiteralProperty(dataType = XsdDatatype.decimal, iri = UriConstants.ON_RDFS_LABEL_PROP)
        private String prop3() {
            return "myFeedLabelMethod";
        }

        @UriProperty(iri = UriConstants.ON_RDFS_LABEL_PROP)
        private String prop4() {
            return "myFeedLabelMethod";
        }


        @Feed(id="myFeed")
        TestPoint getFeed() {
            return new TestPoint("published");
        }

        @Input(id="myInput")
        TestPoint getInput() {
            return new TestPoint("received");
        }

    }

}