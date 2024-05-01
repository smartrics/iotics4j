package smartrics.iotics.connectors.twins;

import com.google.common.collect.Lists;
import com.iotics.api.*;
import smartrics.iotics.host.Builders;

import java.util.List;

public interface AnnotationMapper extends Mapper, Identifiable {

    default List<ShareFeedDataRequest> getShareFeedDataRequest() {
        AnnotationToApiBuilder builder = new AnnotationToApiBuilder();
        List<AnnotationToApiBuilder.FeedValue> feedPayloads = builder.buildShareFeedDataRequestJSONPayload(this);
        List<ShareFeedDataRequest> results = Lists.newArrayListWithCapacity(feedPayloads.size());
        feedPayloads.forEach(fp -> {
            ShareFeedDataRequest request = ShareFeedDataRequest.newBuilder().setHeaders(
                    Builders.newHeadersBuilder(getAgentIdentity()))
                    .setArgs(ShareFeedDataRequest.Arguments.newBuilder()
                            .setFeedId(FeedID.newBuilder().setId(fp.feedId())
                            .setTwinId(getMyIdentity().did())
                        .build())
                            .build()).setPayload(fp.payload()).build();
            results.add(request);

        });
        return results;
    }

    default UpsertTwinRequest getUpsertTwinRequest() {
        UpsertTwinRequest.Builder reqBuilder = UpsertTwinRequest.newBuilder();
        reqBuilder.setHeaders(Builders.newHeadersBuilder(getAgentIdentity()));
        reqBuilder.setPayload(new AnnotationToApiBuilder()
                .buildUpsertTwinRequestPayload(TwinID.newBuilder().setId(getMyIdentity().did()).build(), this));
        return reqBuilder.build();
    }

}
