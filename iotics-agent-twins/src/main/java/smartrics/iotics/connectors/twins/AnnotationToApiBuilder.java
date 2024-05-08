package smartrics.iotics.connectors.twins;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.iotics.api.*;
import smartrics.iotics.connectors.twins.annotations.*;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Stream;

class AnnotationToApiBuilder {

    private static <A extends Annotation> Optional<String> getValidIdentifierValue(Object instance, Class<A> annotationClass) {
        List<String> v = GenericInvoker.collectAnnotatedMemberValues(instance, annotationClass).stream().map(annotationData -> annotationData.annotatedElementValue().toString()).toList();
        if (v.size() > 1) {
            throw new IllegalArgumentException("multiple " + annotationClass.getSimpleName() + " annotations");
        }
        if (v.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(v.getFirst());
    }

    List<FeedValue> buildShareFeedDataRequestJSONPayload(Object instance) {
        List<AnnotationData> feeds = GenericInvoker.collectAnnotatedMemberValues(instance, Feed.class);
        List<FeedValue> results = Lists.newArrayListWithCapacity(feeds.size());
        feeds.forEach(feedAnnotationData -> {
            String feedId = feedAnnotationData.annotationKvp().get("id").toString();
            List<ValueObject> feedValues = buildValues(feedAnnotationData.annotatedElementValue());
            ShareFeedDataRequest.Payload.Builder payloadBuilder = ShareFeedDataRequest.Payload.newBuilder();
            Map<String, Object> samples = new HashMap<>();
            Gson gson = new Gson();
            feedValues.forEach(ve -> {
                String v = null;
                if (ve.valueObject != null) {
                    v = ve.valueObject.toString();
                }
                samples.put(ve.value.getLabel(), v);
            });
            payloadBuilder.setSample(FeedData.newBuilder().setData(ByteString.copyFromUtf8(gson.toJson(samples))));
            results.add(new FeedValue(feedId, payloadBuilder.build()));
        });
        return results;
    }

    UpsertTwinRequest.Payload buildUpsertTwinRequestPayload(TwinID twinID, Object instance) {
        UpsertTwinRequest.Payload.Builder builder = UpsertTwinRequest.Payload.newBuilder();
        builder.setTwinId(Optional.ofNullable(twinID).orElseGet(() -> buildId(instance)));
        GeoLocation l = buildLocation(instance);
        if (l != null) {
            builder.setLocation(l);
        }
        builder.addAllProperties(buildProperties(instance));
        builder.addAllFeeds(buildFeeds(instance));
        builder.addAllInputs(buildInputs(instance));
        return builder.build();
    }

    List<Property> buildProperties(Object instance) {
        return Stream.of(StringLiteralProperty.class, LiteralProperty.class, UriProperty.class, LangLiteralProperty.class).flatMap(aClass -> GenericInvoker.collectAnnotatedMemberValues(instance, aClass).stream()).map(this::buildProperty).toList();
    }

    List<Value> buildValuesMeta(Object instance) {
        return GenericInvoker.collectAnnotatedMemberValues(instance, PayloadValue.class)
                .stream()
                .map(annotationData -> {
                    Value.Builder vb = Value.newBuilder();
                    String dataType = annotationData.annotationKvp().get("dataType").toString();
                    if(!dataType.isEmpty()) {
                        vb.setDataType(dataType);
                    }
                    String comment = annotationData.annotationKvp().get("comment").toString();
                    if(!comment.isEmpty()) {
                        vb.setComment(comment);
                    }
                    String unit = annotationData.annotationKvp().get("unit").toString();
                    if(!unit.isEmpty()) {
                        vb.setUnit(unit);
                    }
                    String label = annotationData.annotationKvp().get("label").toString();
                    if(!label.isEmpty()) {
                        vb.setLabel(label);
                    } else {
                        vb.setLabel(annotationData.methodOrFieldName());
                    }
                    return vb.build();
                })
                .toList();
    }

    List<ValueObject> buildValues(Object instance) {
        List<ValueObject> result = new ArrayList<>();
        List<AnnotationData> ad = GenericInvoker.collectAnnotatedMemberValues(instance, PayloadValue.class);
        ad.forEach(annotationData -> {
            Value.Builder vb = Value.newBuilder();
            // don't need the others
            String label = annotationData.annotationKvp().get("label").toString();
            if(!label.isEmpty()) {
                vb.setLabel(label);
            } else {
                vb.setLabel(annotationData.methodOrFieldName());
            }
            result.add(new ValueObject(vb.build(), annotationData.annotatedElementValue()));
        });
        return result;
    }

    GeoLocation buildLocation(Object instance) {
        List<GeoLocation> v = GenericInvoker.collectAnnotatedMemberValues(instance, Location.class).stream().map(annotationData -> {
            Object o = annotationData.annotatedElementValue();
            if (o instanceof GeoLocation) {
                return (GeoLocation) o;
            }
            throw new IllegalArgumentException("invalid GeoLocation for this annotation: " + annotationData);
        }).toList();
        if (v.isEmpty()) {
            return null;
        }
        if (v.size() > 1) {
            throw new IllegalArgumentException("multiple GeoLocation attributes for this object");
        }
        return v.getFirst();
    }

    TwinID buildId(Object instance) {
        Optional<String> twinId = getValidIdentifierValue(instance, Identifier.class);
        TwinID.Builder builder = TwinID.newBuilder();
        builder.setId(twinId.orElseThrow(() -> new IllegalArgumentException("missing Identifier annotation")));
        Optional<String> hostId = getValidIdentifierValue(instance, HostIdentifier.class);
        hostId.ifPresent(builder::setHostId);
        return builder.build();
    }

    List<UpsertFeedWithMeta> buildFeeds(Object instance) {
        return GenericInvoker.collectAnnotatedMemberValues(instance, Feed.class).stream().map(this::buildFeed).toList();
    }

    List<UpsertInputWithMeta> buildInputs(Object instance) {
        return GenericInvoker.collectAnnotatedMemberValues(instance, Input.class).stream().map(this::buildInput).toList();
    }

    Property buildProperty(AnnotationData data) {
        Object annotation = data.annotation();
        String iri = (String) data.annotationKvp().get("iri");
        if (iri == null) {
            throw new IllegalArgumentException("null iri for this annotation: " + data);
        }
        Property.Builder builder = Property.newBuilder().setKey(iri);
        String annotatedElementValue = data.annotatedElementValue().toString();
        switch (annotation) {
            case StringLiteralProperty slp -> {
                StringLiteral slValue = StringLiteral.newBuilder().setValue(annotatedElementValue).build();
                builder.setStringLiteralValue(slValue);
            }
            case LiteralProperty literalProperty -> {
                Literal lValue = Literal.newBuilder().setDataType(literalProperty.dataType().toString()).setValue(annotatedElementValue).build();
                builder.setLiteralValue(lValue);
            }
            case UriProperty up -> builder.setUriValue(Uri.newBuilder().setValue(annotatedElementValue).build());
            case LangLiteralProperty llp -> {
                LangLiteral llValue = LangLiteral.newBuilder().setLang(llp.lang()).setValue(annotatedElementValue).build();
                builder.setLangLiteralValue(llValue);
            }
            default -> throw new IllegalArgumentException("invalid annotation data: " + data);
        }
        return builder.build();
    }

    UpsertFeedWithMeta buildFeed(AnnotationData data) {
        UpsertFeedWithMeta.Builder builder = UpsertFeedWithMeta.newBuilder();
        builder.setId(data.annotationKvp().get("id").toString());

        boolean storeLast = Boolean.parseBoolean(data.annotationKvp().get("storeLast").toString());
        builder.setStoreLast(storeLast);

        List<Property> allProps = buildProperties(data.annotatedElementValue());
        builder.addAllProperties(allProps);

        List<Value> allValues = buildValuesMeta(data.annotatedElementValue());
        builder.addAllValues(allValues);

        return builder.build();
    }

    UpsertInputWithMeta buildInput(AnnotationData data) {
        UpsertInputWithMeta.Builder builder = UpsertInputWithMeta.newBuilder();
        builder.setId(data.annotationKvp().get("id").toString());

        List<Property> allProps = buildProperties(data.annotatedElementValue());
        builder.addAllProperties(allProps);

        List<Value> allValues = buildValuesMeta(data.annotatedElementValue());
        builder.addAllValues(allValues);

        return builder.build();
    }

    record ValueObject(Value value, Object valueObject) {
    }

    record FeedValue(String feedId, ShareFeedDataRequest.Payload payload) {
    }
}
