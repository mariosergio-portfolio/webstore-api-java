package com.mycompany.webstore.infrastructure.persistence.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.webstore.domain.model.ImageUrl;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

@Converter
public class ImageUrlListJsonConverter implements AttributeConverter<List<ImageUrl>, Object> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public Object convertToDatabaseColumn(List<ImageUrl> attribute) {
        String json;
        if (attribute == null || attribute.isEmpty()) {
            json = "[]";
        } else {
            try {
                json = MAPPER.writeValueAsString(attribute);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Could not serialize imageUrls to JSON", e);
            }
        }
        try {
            Object pgo = Class.forName("org.postgresql.util.PGobject").getDeclaredConstructor().newInstance();
            Method setType = pgo.getClass().getMethod("setType", String.class);
            Method setValue = pgo.getClass().getMethod("setValue", String.class);
            setType.invoke(pgo, "jsonb");
            setValue.invoke(pgo, json);
            return pgo;
        } catch (Exception e) {
            return json;
        }
    }

    @Override
    public List<ImageUrl> convertToEntityAttribute(Object dbData) {
        if (dbData == null) {
            return Collections.emptyList();
        }
        String raw;
        try {
            Method getValue = dbData.getClass().getMethod("getValue");
            raw = (String) getValue.invoke(dbData);
        } catch (Exception e) {
            raw = dbData.toString();
        }
        if (raw == null || raw.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return MAPPER.readValue(raw, new TypeReference<List<ImageUrl>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Could not deserialize imageUrls from JSON: " + raw, e);
        }
    }
}
