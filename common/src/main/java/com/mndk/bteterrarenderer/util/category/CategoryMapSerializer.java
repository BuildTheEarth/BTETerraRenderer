package com.mndk.bteterrarenderer.util.category;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

class CategoryMapSerializer extends JsonSerializer<CategoryMap<Object>> {
    private static final int MAX_RECURSION_DEPTH = 4;

    @Override
    public void serialize(CategoryMap<Object> map, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject(); // main

        for (java.util.Map.Entry<String, Category<Object>> entry : map.getMap().entrySet()) {
            gen.writeFieldName(entry.getKey());
            serializeCategory(entry.getValue(), gen, 1);
        }

        gen.writeEndObject(); // main
    }

    private void serializeCategory(Category<Object> category, JsonGenerator gen, int depth) throws IOException {
        if (depth > MAX_RECURSION_DEPTH) {
            throw new IOException("category recursion depth exceeded max depth of " + MAX_RECURSION_DEPTH);
        }
        gen.writeStartObject();
        for (java.util.Map.Entry<String, Object> entry : category.getEntries().entrySet()) {
            gen.writeFieldName(entry.getKey());
            gen.writeObject(entry.getValue());
        }
        for (java.util.Map.Entry<String, Category<Object>> sub : category.getSubcategories().entrySet()) {
            gen.writeFieldName(sub.getKey());
            serializeCategory(sub.getValue(), gen, depth + 1);
        }
        gen.writeEndObject();
    }
}
