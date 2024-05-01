package smartrics.iotics.connectors.twins.annotations;

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

public class GenericInvokerTest {
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
                .forEach(data -> assertThat(data.annotationKvp().get("dataType"), is(equalTo(XsdDatatype.decimal))));
        result.stream().filter(annotationData -> annotationData.annotation() instanceof LangLiteralProperty)
                .forEach(data -> assertThat(data.annotationKvp().get("lang"), is(equalTo("en"))));
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
        List<String> values = result.stream().flatMap((Function<AnnotationData, Stream<String>>) annotationData -> ((Set<String>) annotationData.annotatedElementValue()).stream()).toList();
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
        assertThat(feedValue.getFirst().annotationKvp().get("label"), is(equalTo("theName")));
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
        assertThat(feedValue.getFirst().annotationKvp().get("label"), is(equalTo("theName")));
        assertThat(feedValue.getFirst().annotatedElementValue(), is(equalTo("received")));
    }

}