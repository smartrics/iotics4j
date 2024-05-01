package smartrics.iotics.connectors.twins.annotations;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record AnnotationData(@NotNull Object annotation, @NotNull Map<String, Object> annotationKvp,
                             @NotNull Object annotatedElementValue, @NotNull String methodOrFieldName) {
    public AnnotationData(Object annotation, Map<String, Object> annotationKvp, Object annotatedElementValue, String methodOrFieldName) {
        this.annotation = annotation;
        if (this.annotation == null) {
            throw new IllegalArgumentException("null annotation");
        }
        this.annotationKvp = annotationKvp;
        if (this.annotationKvp == null) {
            throw new IllegalArgumentException("null annotationKvp");
        }
        this.annotatedElementValue = annotatedElementValue;
        if (this.annotatedElementValue == null) {
            throw new IllegalArgumentException("null annotatedElementValue");
        }
        this.methodOrFieldName = methodOrFieldName;
        if (this.methodOrFieldName == null) {
            throw new IllegalArgumentException("null methodOrFieldName");
        }
    }
}
