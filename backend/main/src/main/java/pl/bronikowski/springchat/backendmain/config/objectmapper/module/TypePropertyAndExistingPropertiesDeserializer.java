package pl.bronikowski.springchat.backendmain.config.objectmapper.module;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import pl.bronikowski.springchat.backendmain.config.objectmapper.module.annotation.JsonSubTypeWithTypePropertyAndExistingProperties;
import pl.bronikowski.springchat.backendmain.config.objectmapper.module.annotation.JsonSubTypesWithTypePropertyAndExistingProperties;
import pl.bronikowski.springchat.backendmain.config.objectmapper.module.annotation.JsonTypeWithTypePropertyAndExistingProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypePropertyAndExistingPropertiesDeserializer extends StdDeserializer<Object> {
    private static final BitSet EMPTY_BITSET = new BitSet(0);
    private final String typeProperty;
    private final Map<String, Map<String, Integer>> typeToPropertiesBitIndex = new HashMap<>();
    private final Map<String, Map<BitSet, Class<?>>> typeToClassFingerprints = new HashMap<>();

    public TypePropertyAndExistingPropertiesDeserializer(Class<?> vc) {
        super(vc);
        this.typeProperty = getTypeProperty(vc);
        buildFingerprint(vc);
    }

    private String getTypeProperty(Class<?> vc) {
        var typeAnnotation = vc.getAnnotation(JsonTypeWithTypePropertyAndExistingProperties.class);
        assert typeAnnotation != null;
        return typeAnnotation.typeProperty();
    }

    private void buildFingerprint(Class<?> vc) {
        var subtypesAnnotation = vc.getAnnotation(JsonSubTypesWithTypePropertyAndExistingProperties.class);
        var typeToAnnotations = new HashMap<String, List<JsonSubTypeWithTypePropertyAndExistingProperties>>();
        var subTypeAnnotations = subtypesAnnotation != null
                ? Arrays.asList(subtypesAnnotation.value())
                : List.<JsonSubTypeWithTypePropertyAndExistingProperties>of();
        subTypeAnnotations.forEach(annotation -> typeToAnnotations
                .computeIfAbsent(annotation.typeValue(), ignored -> new ArrayList<>())
                .add(annotation));
        typeToAnnotations.forEach(this::buildFingerprint);
    }

    private void buildFingerprint(String typeValue, List<JsonSubTypeWithTypePropertyAndExistingProperties> annotations) {
        var allPossibleProperties = annotations.stream()
                .flatMap(annotation -> Arrays.stream(annotation.existingProperties()))
                .distinct()
                .toList();
        var propertyToBitIndex = new HashMap<String, Integer>();
        var classFingerprints = new HashMap<BitSet, Class<?>>();
        var nextField = 0;
        for (var attribute : allPossibleProperties) {
            propertyToBitIndex.put(attribute, nextField++);
        }
        for (var annotation : annotations) {
            var fingerprint = new BitSet(allPossibleProperties.size());
            for (var property : annotation.existingProperties()) {
                fingerprint.set(propertyToBitIndex.get(property));
            }
            classFingerprints.put(fingerprint, annotation.value());
        }
        this.typeToPropertiesBitIndex.put(typeValue, propertyToBitIndex);
        this.typeToClassFingerprints.put(typeValue, classFingerprints);
    }

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        var objectMapper = jsonParser.getCodec();
        if (!(objectMapper.readTree(jsonParser) instanceof ObjectNode object)) {
            return null;
        }
        if (!object.has(this.typeProperty)) {
            return null;
        }
        var value = object.get(this.typeProperty);
        if (!value.isTextual()) {
            return null;
        }
        var text = value.asText();
        var classFingerprints = this.typeToClassFingerprints.get(text);
        var propertyToBitIndex = this.typeToPropertiesBitIndex.get(text);
        if (classFingerprints == null || propertyToBitIndex == null) {
            return null;
        }
        var fingerprint = new BitSet(propertyToBitIndex.size());
        for (var propertyBitIndex : propertyToBitIndex.entrySet()) {
            if (object.has(propertyBitIndex.getKey())) {
                fingerprint.set(propertyBitIndex.getValue());
            }
        }
        var resolvedClass = classFingerprints.get(fingerprint);
        if (resolvedClass == null) {
            resolvedClass = classFingerprints.get(EMPTY_BITSET);
        }
        if (resolvedClass == null) {
            return null;
        }
        return objectMapper.treeToValue(object, resolvedClass);
    }
}
