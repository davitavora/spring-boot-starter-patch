package com.github.davitavora.patch.web;

import jakarta.json.Json;
import jakarta.json.JsonPatch;
import jakarta.json.JsonReader;
import jakarta.json.JsonWriter;
import java.io.IOException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

@Component
public class JsonPatchHttpMessageConverter extends AbstractHttpMessageConverter<JsonPatch> {

    public JsonPatchHttpMessageConverter() {
        super(MediaType.valueOf("application/json-patch+json"));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return JsonPatch.class.isAssignableFrom(clazz);
    }

    @Override
    protected JsonPatch readInternal(Class<? extends JsonPatch> clazz, HttpInputMessage inputMessage)
        throws HttpMessageNotReadableException {

        try (JsonReader reader = Json.createReader(inputMessage.getBody())) {
            return Json.createPatch(reader.readArray());
        } catch (IOException ioException) {
            throw new HttpMessageNotReadableException(ioException.getMessage(), inputMessage);
        }
    }

    @Override
    protected void writeInternal(JsonPatch jsonPatch, HttpOutputMessage httpOutputMessage) throws HttpMessageNotWritableException {

        try (JsonWriter writer = Json.createWriter(httpOutputMessage.getBody())) {
            writer.write(jsonPatch.toJsonArray());
        } catch (IOException ioException) {
            throw new HttpMessageNotWritableException(ioException.getMessage(), ioException);
        }
    }

}
