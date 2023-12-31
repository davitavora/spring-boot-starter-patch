package com.github.davitavora.patch.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.davitavora.patch.exception.UnprocessableEntityException;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonPatch;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Patcher {

    private final ObjectMapper mapper;
    private final Validator validator;

    public <T> T patch(JsonPatch patch, T targetBean, Class<T> beanClass) {
        final var target = mapper.convertValue(targetBean, JsonStructure.class);
        final var patched = applyPatch(patch, target);
        return convertAndValidate(patched, beanClass);
    }

    public <T> T mergePatch(JsonMergePatch mergePatch, T targetBean, Class<T> beanClass) {
        final var target = mapper.convertValue(targetBean, JsonValue.class);
        final var patched = applyMergePatch(mergePatch, target);
        return convertAndValidate(patched, beanClass);
    }

    private JsonValue applyPatch(JsonPatch patch, JsonStructure target) {
        try {
            return patch.apply(target);
        } catch (Exception e) {
            throw new UnprocessableEntityException(e);
        }
    }

    private JsonValue applyMergePatch(JsonMergePatch mergePatch, JsonValue target) {
        try {
            return mergePatch.apply(target);
        } catch (Exception e) {
            throw new UnprocessableEntityException(e);
        }
    }

    private <T> T convertAndValidate(JsonValue jsonValue, Class<T> beanClass) {
        final var bean = mapper.convertValue(jsonValue, beanClass);
        validate(bean);
        return bean;
    }

    private <T> void validate(T bean) {
        final Set<ConstraintViolation<T>> violations = validator.validate(bean);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

}
