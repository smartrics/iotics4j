package smartrics.iotics.connectors.twins.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface PayloadValue {

    String label() default "";

    String unit() default "";

    XsdDatatype dataType() default XsdDatatype.string;

    String comment() default "";
}
