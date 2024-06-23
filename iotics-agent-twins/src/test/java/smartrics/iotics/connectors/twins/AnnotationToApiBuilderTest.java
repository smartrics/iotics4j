package smartrics.iotics.connectors.twins;

import com.google.gson.Gson;
import com.iotics.api.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import smartrics.iotics.connectors.twins.annotations.*;
import smartrics.iotics.host.UriConstants;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;

class AnnotationToApiBuilderTest {

    private final AnnotationToApiBuilder annBuilder = new AnnotationToApiBuilder();

    public static Stream<Arguments> allTwinIDs() {
        return Stream.of(
                Arguments.of(null, "did:1234", "did:4321"),
                Arguments.of(TwinID.newBuilder().setId("did:abc").setHostId("did:def").build(), "did:abc", "did:def")
        );
    }

    @Test
    void buildsSharePayload() {
        List<AnnotationToApiBuilder.FeedValue> payload = annBuilder.buildShareFeedDataRequestJSONPayload(new TestClass());
        assertThat(payload.size(), is(equalTo(2)));
        String data = payload.stream().filter(feedValue -> feedValue.feedId().equals("myFeed"))
                .findFirst().orElseThrow().payload().getSample().getData().toStringUtf8();
        Gson gson = new Gson();
        TestPoint tp = gson.fromJson(data, TestPoint.class);
        assertThat(tp.dcat, is(equalTo("v1")));
        assertThat(tp.foma, is(equalTo("f1")));
    }

    @ParameterizedTest
    @MethodSource("allTwinIDs")
    void buildsUpsertRequestPayload(TwinID twinID, String expectedId, String expectedHostId) {
        UpsertTwinRequest.Payload payload = annBuilder.buildUpsertTwinRequestPayload(twinID, new TestClass());
        assertThat(payload.getTwinId().getId(), is(equalTo(expectedId)));
        assertThat(payload.getTwinId().getHostId(), is(equalTo(expectedHostId)));

        assertThat(payload.getLocation().getLat(), is(equalTo(10.2)));
        assertThat(payload.getLocation().getLon(), is(equalTo(20.1)));

        assertThat(payload.getPropertiesList().size(), is(equalTo(4)));
        assertThat(payload.getFeedsCount(), is(equalTo(2)));
        assertThat(payload.getFeedsList().stream().filter(u -> u.getId().equals("myFeed")).mapToLong(u -> u.getPropertiesList().size()).sum(), is(equalTo(2L)));
        assertThat(payload.getFeedsList().stream().filter(u -> u.getId().equals("myOtherFeed")).mapToLong(u -> u.getPropertiesList().size()).sum(), is(equalTo(1L)));

        assertThat(payload.getInputsCount(), is(equalTo(1)));
        assertThat(payload.getInputsList().stream().filter(u -> u.getId().equals("myInput")).mapToLong(u -> u.getPropertiesList().size()).sum(), is(equalTo(2L)));
    }

    @Test
    void buildsUpsertRequestPayloadWithNullID() {
        UpsertTwinRequest.Payload payload = annBuilder.buildUpsertTwinRequestPayload(null, new TestClass());
        assertThat(payload.getTwinId().getId(), is(equalTo("did:1234")));
        assertThat(payload.getTwinId().getHostId(), is(equalTo("did:4321")));

        assertThat(payload.getLocation().getLat(), is(equalTo(10.2)));
        assertThat(payload.getLocation().getLon(), is(equalTo(20.1)));

        assertThat(payload.getPropertiesList().size(), is(equalTo(4)));
        assertThat(payload.getFeedsCount(), is(equalTo(2)));
        assertThat(payload.getFeedsList().stream().filter(u -> u.getId().equals("myFeed")).mapToLong(u -> u.getPropertiesList().size()).sum(), is(equalTo(2L)));
        assertThat(payload.getFeedsList().stream().filter(u -> u.getId().equals("myOtherFeed")).mapToLong(u -> u.getPropertiesList().size()).sum(), is(equalTo(1L)));

        assertThat(payload.getInputsCount(), is(equalTo(1)));
        assertThat(payload.getInputsList().stream().filter(u -> u.getId().equals("myInput")).mapToLong(u -> u.getPropertiesList().size()).sum(), is(equalTo(2L)));
    }

    @Test
    void buildsUpsertRequestPayloadWithUriProperty() {
        UpsertTwinRequest.Payload payload = annBuilder.buildUpsertTwinRequestPayload(TwinID.newBuilder().setId("did:iotics:123").build(), new TestInterface() {

            @UriProperty(iri = UriConstants.IOTICSProperties.HostAllowListName)
            private final String visibility = UriConstants.IOTICSProperties.HostAllowListValues.ALL.toString();

        });

        assertThat(payload.getPropertiesList().getFirst().getUriValue().getValue(),
                is(equalTo(UriConstants.IOTICSProperties.HostAllowListValues.ALL.toString())));

    }

    @Test
    void buildStringLiteralProperty() {
        List<AnnotationData> data = GenericInvoker.collectAnnotatedMemberValues(new TestClass(), StringLiteralProperty.class);
        Property prop = annBuilder.buildProperty(data.getFirst());
        assertTrue(prop.hasStringLiteralValue());
        assertThat(prop.getKey(), is(equalTo("someIri")));
        assertThat(prop.getStringLiteralValue().getValue(), is(equalTo("prop1")));
    }

    @Test
    void buildLiteralProperty() {
        List<AnnotationData> data = GenericInvoker.collectAnnotatedMemberValues(new TestClass(), LiteralProperty.class);
        Property prop = annBuilder.buildProperty(data.getFirst());
        assertTrue(prop.hasLiteralValue());
        assertThat(prop.getKey(), is(equalTo("someIri")));
        assertThat(prop.getLiteralValue().getValue(), is(equalTo("prop0")));
        assertThat(prop.getLiteralValue().getDataType(), is(equalTo("byte")));
    }

    @Test
    void buildUriProperty() {
        List<AnnotationData> data = GenericInvoker.collectAnnotatedMemberValues(new TestClass(), UriProperty.class);
        Property prop = annBuilder.buildProperty(data.getFirst());
        assertTrue(prop.hasUriValue());
        assertThat(prop.getKey(), is(equalTo("someIri")));
        assertThat(prop.getUriValue().getValue(), is(equalTo("prop2")));
    }

    @Test
    void buildLangLiteralProperty() {
        List<AnnotationData> data = GenericInvoker.collectAnnotatedMemberValues(new TestClass(), LangLiteralProperty.class);
        Property prop = annBuilder.buildProperty(data.getFirst());
        assertTrue(prop.hasLangLiteralValue());
        assertThat(prop.getKey(), is(equalTo("someIri")));
        assertThat(prop.getLangLiteralValue().getValue(), is(equalTo("prop3")));
    }

    @Test
    void buildAnnotationOnStaticVariables() {
        TestInterface instance = newTestInterfaceWithStatic();
        List<AnnotationData> data = GenericInvoker.collectAnnotatedMemberValues(instance, StringLiteralProperty.class);
        Property prop = annBuilder.buildProperty(data.getFirst());
        assertTrue(prop.hasStringLiteralValue());
        assertThat(prop.getKey(), is(equalTo("http://blurb.com/foo")));
        assertThat(prop.getStringLiteralValue().getValue(), is(equalTo("foo")));
    }

    @Test
    void buildAnnotationOnStaticMethods() {
        TestInterface instance = newTestInterfaceWithStatic();
        List<AnnotationData> data = GenericInvoker.collectAnnotatedMemberValues(instance, LiteralProperty.class);
        Property prop = annBuilder.buildProperty(data.getFirst());
        assertTrue(prop.hasLiteralValue());
        assertThat(prop.getKey(), is(equalTo("http://blurb.com/bar")));
        assertThat(prop.getLiteralValue().getValue(), is(equalTo("false")));
        assertThat(prop.getLiteralValue().getDataType(), is(equalTo("boolean")));
    }

    private static TestInterface newTestInterfaceWithStatic() {
        return new TestInterface() {
            @StringLiteralProperty(iri = "http://blurb.com/foo")
            public static String something = "foo";

            @LiteralProperty(iri = "http://blurb.com/bar", dataType = XsdDatatype.boolean_)
            public static String method() {
                return "false";
            }

        };
    }

    @Test
    void throwsOnInvalidProperty() {
        List<AnnotationData> data = GenericInvoker.collectAnnotatedMemberValues(new TestClass(), Feed.class);
        assertThrows(IllegalArgumentException.class, () -> annBuilder.buildProperty(data.getFirst()));
    }

    @Test
    void buildFeedCheckStoreLast() {
        List<AnnotationData> data = GenericInvoker.collectAnnotatedMemberValues(new TestClass(), Feed.class);

        List<UpsertFeedWithMeta> feeds = data.stream().map(annBuilder::buildFeed).toList();
        UpsertFeedWithMeta feed = feeds.stream().filter(p -> p.getId().equals("myOtherFeed")).findFirst().orElseThrow();
        assertThat(feed.getStoreLast(), is(equalTo(false))); // default
    }

    @Test
    void buildFeed() {
        List<AnnotationData> data = GenericInvoker.collectAnnotatedMemberValues(new TestClass(), Feed.class);

        List<UpsertFeedWithMeta> feeds = data.stream().map(annBuilder::buildFeed).toList();
        UpsertFeedWithMeta feed = feeds.stream().filter(p -> p.getId().equals("myFeed")).findFirst().orElseThrow();
        assertThat(feed.getId(), is(equalTo("myFeed")));
        assertThat(feed.getStoreLast(), is(equalTo(true))); // default
        assertThat(feed.getPropertiesList().size(), is(equalTo(2)));

        List<String> propKeys = feed.getPropertiesList().stream().map(Property::getKey).toList();
        assertThat(propKeys, containsInAnyOrder("someIri1", "someIri2"));

        List<String> valueLabels = feed.getValuesList().stream().map(Value::getLabel).toList();
        assertThat(valueLabels, containsInAnyOrder("dcat", "foma"));
    }

    @Test
    void buildFeedValuesWithLabelMatchingTheFieldOrAttributeName() {
        List<AnnotationToApiBuilder.ValueObject> values = annBuilder.buildValues(new TestInterface() {

            @PayloadValue
            private String foo = "fooValue";

            @PayloadValue
            private String bar() {
                return "barValue";
            }

        });
        List<String> valueLabels = values.stream().map(valueObject -> valueObject.value().getLabel()).toList();
        assertThat(valueLabels, containsInAnyOrder("foo", "bar"));
        List<String> valueObjects = values.stream().map(valueObject -> valueObject.valueObject().toString()).toList();
        assertThat(valueObjects, containsInAnyOrder("fooValue", "barValue"));
    }

    @Test
    void buildFeedValues() {
        List<AnnotationToApiBuilder.ValueObject> values = annBuilder.buildValues(new TestInterface() {

            @PayloadValue(label = "jack")
            private String foo = "fooValue";

            @PayloadValue(label = "john")
            private String bar() {
                return "barValue";
            }

        });
        List<String> valueLabels = values.stream().map(valueObject -> valueObject.value().getLabel()).toList();
        assertThat(valueLabels, containsInAnyOrder("jack", "john"));
        List<String> valueObjects = values.stream().map(valueObject -> valueObject.valueObject().toString()).toList();
        assertThat(valueObjects, containsInAnyOrder("fooValue", "barValue"));
    }

    @Test
    void buildFeedValueMeta() {
        List<Value> values = annBuilder.buildValuesMeta(new TestInterface() {

            @PayloadValue(label = "fooLabel", comment = "fooComment", dataType = XsdDatatype.string, unit = "fooUnit")
            private String foo = "fooValue";

        });
        Value v = values.getFirst();
        assertThat(v.getLabel(), is(equalTo("fooLabel")));
        assertThat(v.getComment(), is(equalTo("fooComment")));
        assertThat(v.getDataType(), is(equalTo(XsdDatatype.string.toString())));
        assertThat(v.getUnit(), is(equalTo("fooUnit")));
    }


    @Test
    void buildInput() {
        List<AnnotationData> data = GenericInvoker.collectAnnotatedMemberValues(new TestClass(), Input.class);
        UpsertInputWithMeta input = annBuilder.buildInput(data.getFirst());
        assertThat(input.getId(), is(equalTo("myInput")));
        assertThat(input.getPropertiesList().size(), is(equalTo(2)));

        List<String> propKeys = input.getPropertiesList().stream().map(Property::getKey).toList();
        assertThat(propKeys, containsInAnyOrder("someIri1", "someIri2"));

        List<String> valueLabels = input.getValuesList().stream().map(Value::getLabel).toList();
        assertThat(valueLabels, containsInAnyOrder("dcat", "foma"));
    }

    @Test
    void buildIdentifier() {
        TwinID id = annBuilder.buildId(new TestClass());
        assertThat(id.getId(), is(equalTo("did:1234")));
        assertThat(id.getHostId(), is(equalTo("did:4321")));
    }

    @Test
    void buildWithMissingHostIdentifier() {
        TwinID id = annBuilder.buildId(new TestInterface() {
            @Identifier
            private String id = "did:1234";

        });
        assertTrue(id.getHostId().isEmpty());
    }

    @Test
    void buildGeoLocation() {
        GeoLocation l = annBuilder.buildLocation(new TestClass());
        assertThat(l.getLat(), is(equalTo(10.2)));
        assertThat(l.getLon(), is(equalTo(20.1)));
    }

    @Test
    void buildWithoutLocation() {
        GeoLocation l = annBuilder.buildLocation(new Object());
        assertNull(l);
    }

    @Test
    void buildFailsForNoTwinID() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> annBuilder.buildId(new Object()));
        assertThat(thrown.getMessage(), is(equalTo("missing Identifier annotation")));
    }

    @Test
    void buildFailsForMultipleTwinID() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> annBuilder.buildId(new TestInterface() {

            @Identifier
            private String one = "did:123";
            @Identifier
            private String two = "did:321";
        }));
        assertThat(thrown.getMessage(), containsString("multiple Identifier annotations"));
    }

    @Test
    void buildFailsForMultipleHostID() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> annBuilder.buildId(new TestInterface() {

            @Identifier
            private String one = "did:123";
            @HostIdentifier
            private String two = "did:321";

            @HostIdentifier
            private String three = "did:321";

        }));
        assertThat(thrown.getMessage(), containsString("multiple HostIdentifier annotations"));
    }


    @Test
    void buildFailsForInvalidGeoLocation() {
        assertThrows(IllegalArgumentException.class, () -> annBuilder.buildLocation(new TestInterface() {

            @Location
            private String two = "did:321";

        }));
    }

    @Test
    void buildsVisibilityProperty() {
        List<AnnotationData> data = GenericInvoker.collectAnnotatedMemberValues(new TestInterface() {

            @UriProperty(iri = UriConstants.IOTICSProperties.HostAllowListName)
            private String visibility = UriConstants.IOTICSProperties.HostAllowListValues.ALL.toString();

            }, UriProperty.class);

        assertThat(data.getFirst().annotatedElementValue().toString(), is(equalTo( UriConstants.IOTICSProperties.HostAllowListValues.ALL.toString())));
    }

    @Test
    void buildFailsForMultipleLocations() {
        assertThrows(IllegalArgumentException.class, () -> annBuilder.buildLocation(new TestInterface() {

            @Location
            private GeoLocation one = GeoLocation.newBuilder().build();
            @Location
            private GeoLocation two = GeoLocation.newBuilder().build();

        }));
    }

    @Test
    void buildFeedsByInstance() {
        List<UpsertFeedWithMeta> feeds = annBuilder.buildFeeds(new TestClass());
        assertThat(feeds.size(), is(equalTo(2)));
    }

    @Test
    void buildInputsByInstance() {
        List<UpsertInputWithMeta> inputs = annBuilder.buildInputs(new TestClass());
        assertThat(inputs.size(), is(equalTo(1)));
    }

    interface TestInterface {
    }

    static class TestPoint {

        @PayloadValue(label = "dcat")
        private final String dcat;
        @PayloadValue(label = "foma")
        private final String foma;

        @StringLiteralProperty(iri = "someIri1")
        private String prop1 = "pointProp1";
        @LiteralProperty(dataType = XsdDatatype.unsignedInt, iri = "someIri2")
        private String prop2 = "pointProp2";

        TestPoint(String s1, String s2) {
            this.dcat = s1;
            this.foma = s2;
        }
    }

    record Test2Point(@PayloadValue(label = "plop") String plop) {

        @UriProperty(iri = "someIri1")
        private String prop1() {
            return "pointProp1";
        }
    }

    static class TestClass {
        @Identifier
        private String value = "did:1234";
        @HostIdentifier
        private String hostValue = "did:4321";

        @Location
        private GeoLocation loc = GeoLocation.newBuilder().setLat(10.2).setLon(20.1).build();

        @LiteralProperty(dataType = XsdDatatype.byte_, iri = "someIri")
        private String prop0 = "prop0";
        @StringLiteralProperty(iri = "someIri")
        private String prop1 = "prop1";
        @UriProperty(iri = "someIri")
        private String prop2 = "prop2";
        @LangLiteralProperty(iri = "someIri")
        private String prop3 = "prop3";

        @Feed(id = "myFeed")
        TestPoint myFeed() {
            return new TestPoint("v1", "f1");
        }

        @Feed(id = "myOtherFeed", storeLast = false)
        Test2Point myOtherFeed() {
            return new Test2Point("v1");
        }

        @Input(id = "myInput")
        TestPoint myInput() {
            return new TestPoint("v2", "f2");
        }

    }

}