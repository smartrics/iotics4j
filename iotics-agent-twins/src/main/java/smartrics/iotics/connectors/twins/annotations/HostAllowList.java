package smartrics.iotics.connectors.twins.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface HostAllowList {
    // http://data.iotics.com/public#allHosts
    // http://data.iotics.com/public#noHost
    String iri() default ("http://data.iotics.com/public#hostAllowList");
}
