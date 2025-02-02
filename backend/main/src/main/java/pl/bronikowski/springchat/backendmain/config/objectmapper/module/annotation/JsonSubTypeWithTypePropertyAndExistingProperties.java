package pl.bronikowski.springchat.backendmain.config.objectmapper.module.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(JsonSubTypesWithTypePropertyAndExistingProperties.class)
public @interface JsonSubTypeWithTypePropertyAndExistingProperties {
    String typeValue() default "";

    String[] existingProperties() default {};

    Class<?> value();
}
