package smartrics.iotics.connectors.twins.annotations;

import com.google.common.collect.Maps;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class GenericInvoker {

    public static <A extends Annotation> List<AnnotationData> collectAnnotatedMemberValues(
            Object instance, Class<A> annotationClass) {
        List<AnnotationData> collectedValues = new ArrayList<>();

        // Check fields
        for (Field field : instance.getClass().getDeclaredFields()) {
            A annotation = field.getAnnotation(annotationClass);
            if (annotation != null) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(instance);
                    Map<String, Object> keys = Maps.newHashMap();
                    Arrays.stream(annotation.annotationType().getDeclaredMethods()).forEach(method -> {
                        String key = method.getName();
                        Object val = extractKeyFromAnnotation(key, annotation);
                        keys.put(key, val);
                    });
                    collectedValues.add(new AnnotationData(annotation, keys, value));
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("unable to check field ", e);
                }
            }
        }

        // Check methods
        for (Method method : instance.getClass().getDeclaredMethods()) {
            A annotation = method.getAnnotation(annotationClass);
            if (annotation != null && method.getParameterCount() == 0) {
                try {
                    method.setAccessible(true);
                    Object value = method.invoke(instance);
                    Map<String, Object> keys = Maps.newHashMap();
                    Arrays.stream(annotation.annotationType().getDeclaredMethods()).forEach(annMethod -> {
                        String key = annMethod.getName();
                        Object val = extractKeyFromAnnotation(key, annotation);
                        keys.put(key, val);
                    });
                    collectedValues.add(new AnnotationData(annotation, keys, value));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalStateException("unable to check method ", e);
                }
            }
        }

        return collectedValues;
    }

    private static <A extends Annotation> Object extractKeyFromAnnotation(String methodName, A annotation) {
        // This method assumes the annotation has a method named "uri" that returns a String.
        // Adjust accordingly for different annotations or structures.
        try {
            Method method = annotation.getClass().getMethod(methodName);
            return method.invoke(annotation);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("invalid method '" + methodName + "' for annotation" + annotation.getClass());
        }
    }
}


