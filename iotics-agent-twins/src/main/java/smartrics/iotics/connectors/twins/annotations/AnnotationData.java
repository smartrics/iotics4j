package smartrics.iotics.connectors.twins.annotations;

import com.google.protobuf.DescriptorProtos;

import java.util.Map;

public record AnnotationData(Object annotation, Map<String, Object> annotationKvp, Object annotatedElementValue) {
}
