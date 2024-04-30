package smartrics.iotics.connectors.twins.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface HostMetadataAllowList {
    // http://data.iotics.com/public#allHosts if return list is null
    // http://data.iotics.com/public#noHost if return list is empty

    String uri() default ("http://data.iotics.com/public#hostAllowList");
}
