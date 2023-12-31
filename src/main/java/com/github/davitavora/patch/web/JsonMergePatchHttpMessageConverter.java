package com.github.davitavora.patch.web;

import jakarta.json.Json;
import jakarta.json.JsonMergePatch;
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
public class JsonMergePatchHttpMessageConverter extends AbstractHttpMessageConverter<JsonMergePatch> {

    public JsonMergePatchHttpMessageConverter() {
        super(MediaType.valueOf("application/merge-patch+json"));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return JsonMergePatch.class.isAssignableFrom(clazz);
    }

    @Override
    protected JsonMergePatch readInternal(Class<? extends JsonMergePatch> aClass, HttpInputMessage httpInputMessage)
        throws HttpMessageNotReadableException {
        try (JsonReader reader = Json.createReader(httpInputMessage.getBody())) {
            return Json.createMergePatch(reader.readValue());
        } catch (IOException ioException) {
            throw new HttpMessageNotReadableException(ioException.getMessage(), httpInputMessage);
        }
    }

    @Override
    protected void writeInternal(JsonMergePatch jsonMergePatch, HttpOutputMessage httpOutputMessage)
        throws HttpMessageNotWritableException {
        try (JsonWriter writer = Json.createWriter(httpOutputMessage.getBody())) {
            writer.write(jsonMergePatch.toJsonValue());
        } catch (IOException ioException) {
            throw new HttpMessageNotWritableException(ioException.getMessage(), ioException);
        }
    }

}
