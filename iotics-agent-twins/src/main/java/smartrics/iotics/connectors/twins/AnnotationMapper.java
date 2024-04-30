package smartrics.iotics.connectors.twins;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.iotics.api.*;
import smartrics.iotics.connectors.twins.annotations.Feed;
import smartrics.iotics.connectors.twins.annotations.GenericInvoker;
import smartrics.iotics.connectors.twins.annotations.PayloadValue;

import smartrics.iotics.connectors.twins.annotations.AnnotationData;

import java.util.*;

public interface AnnotationMapper extends Mapper, Identifiable {

    default List<ShareFeedDataRequest> getShareFeedDataRequest() {
        List<AnnotationData> feeds = GenericInvoker.collectAnnotatedMemberValues(this, Feed.class);

        List<ShareFeedDataRequest> results = Lists.newArrayListWithCapacity(feeds.size());
        Gson gson = new Gson();
        feeds.forEach(feedEntry -> {
            List<AnnotationData> feedValues = GenericInvoker.collectAnnotatedMemberValues(feedEntry.annotatedElementValue(), PayloadValue.class);
            ShareFeedDataRequest.Payload.Builder payloadBuilder = ShareFeedDataRequest.Payload.newBuilder();
            Map<String, Object> values = new HashMap<>();
            feedValues.forEach(ve -> values.put(ve.annotationKvp().get("id").toString(), ve.annotatedElementValue()));
            payloadBuilder.setSample(FeedData.newBuilder().setData(ByteString.copyFromUtf8(gson.toJson(values))));
            ShareFeedDataRequest request = ShareFeedDataRequest.newBuilder()
                            .setHeaders(Headers.newBuilder().setClientAppId(getAgentIdentity().did()).build())
                            .setArgs(ShareFeedDataRequest.Arguments.newBuilder()
                                    .setFeedId(FeedID.newBuilder()
                                            .setId(feedEntry.annotationKvp().get("id").toString())
                                            .setTwinId(getMyIdentity().did())
                                            .build())
                                    .build())
                            .setPayload(payloadBuilder.build())
                            .build();
                    results.add(request);
        });
        return results;
    }

    default UpsertTwinRequest getUpsertTwinRequest() {
        UpsertTwinRequest.Builder reqBuilder = UpsertTwinRequest.newBuilder();

        System.out.println("----");
        List<AnnotationData> feeds = GenericInvoker.collectAnnotatedMemberValues(this, Feed.class);
        feeds.forEach(stringObjectSimpleEntry -> {
//            System.out.println(stringObjectSimpleEntry.annotationKvp() + " ===> ");
//            System.out.println("  props:");
//            List<AnnotationData> feedProps = GenericInvoker.collectAnnotatedMemberValues(stringObjectSimpleEntry.annotatedElementValue(), UriProperty.class);
//            feedProps.forEach(stringObjectSimpleEntry1 -> System.out.println("   " + stringObjectSimpleEntry1.annotationKvp().getKey() + "===> " + stringObjectSimpleEntry1.getValue()));
//            System.out.println("  values:");
//            List<AnnotationData> feedVals = GenericInvoker.collectAnnotatedMemberValues(stringObjectSimpleEntry.annotatedElementValue(), PayloadValue.class);
//            feedVals.forEach(stringObjectSimpleEntry1 -> System.out.println("   " + stringObjectSimpleEntry1.getKey() + "===> " + stringObjectSimpleEntry1.getValue()));
        });
        return reqBuilder.build();

    }


}
