package pl.bronikowski.springchat.backendmain.config.objectmapper.module;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import pl.bronikowski.springchat.backendmain.config.objectmapper.module.annotation.JsonTypeWithTypePropertyAndExistingProperties;

import java.io.Serial;

public class TypePropertyAndExistingPropertiesAnnotationIntrospector extends NopAnnotationIntrospector {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public Object findDeserializer(Annotated am) {
        var rawType = am.getRawType();
        return rawType.isAnnotationPresent(JsonTypeWithTypePropertyAndExistingProperties.class)
                ? new TypePropertyAndExistingPropertiesDeserializer(rawType)
                : null;
    }
}
