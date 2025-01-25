package pl.bronikowski.springchat.backendmain.config.objectmapper.module;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.Serial;

public class TypePropertyAndExistingPropertiesModule extends SimpleModule {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void setupModule(Module.SetupContext context) {
        super.setupModule(context);
        context.insertAnnotationIntrospector(new TypePropertyAndExistingPropertiesAnnotationIntrospector());
    }
}
