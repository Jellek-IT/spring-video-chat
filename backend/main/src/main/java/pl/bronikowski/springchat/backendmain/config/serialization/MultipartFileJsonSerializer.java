package pl.bronikowski.springchat.backendmain.config.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@JsonComponent
public class MultipartFileJsonSerializer extends JsonSerializer<MultipartFile> {
    @Override
    public void serialize(MultipartFile file, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("contentType", file.getContentType());
        jsonGenerator.writeStringField("filename", file.getName());
        jsonGenerator.writeStringField("originalFilename", file.getOriginalFilename());
        jsonGenerator.writeEndObject();
    }
}
