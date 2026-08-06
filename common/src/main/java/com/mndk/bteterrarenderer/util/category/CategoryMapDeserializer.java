package com.mndk.bteterrarenderer.util.category;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

class CategoryMapDeserializer extends JsonDeserializer<CategoryMap<?>> implements ContextualDeserializer {
    private static final int MAX_RECURSION_DEPTH = 4;

    private JavaType valueType;

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        JavaType categoryMapType = property != null ? property.getType() : ctxt.getContextualType();
        JavaType valueType = categoryMapType.containedType(0);
        CategoryMapDeserializer deserializer = new CategoryMapDeserializer();
        deserializer.valueType = valueType;
        return deserializer;
    }

    @Override
    public CategoryMap<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.currentToken() == JsonToken.START_OBJECT)
            p.nextToken();

        JsonNode node = ctxt.readTree(p);
        CategoryMap<Object> result = new CategoryMap<>();

        for (Iterator<Map.Entry<String, JsonNode>> categoryIt = node.fields(); categoryIt.hasNext(); ) {
            Map.Entry<String, JsonNode> categoryEntry = categoryIt.next();
            String categoryName = categoryEntry.getKey();
            JsonNode categoryNode = categoryEntry.getValue();

            if (!categoryNode.isObject())
                throw JsonMappingException.from(p, "category should be an object");

            result.getMap().put(categoryName, deserializeCategory(categoryNode, ctxt, 1));
        }

        return result;
    }

    private Category<Object> deserializeCategory(JsonNode node, DeserializationContext ctxt, int depth) throws IOException {
        if (depth > MAX_RECURSION_DEPTH) {
            throw JsonMappingException.from(ctxt, "category recursion depth exceeded max depth of " + MAX_RECURSION_DEPTH);
        }
        Category<Object> category = new Category<>();
        for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            String key = entry.getKey();
            JsonNode childNode = entry.getValue();

            if (isLeafNode(childNode)) {
                category.put(key, ctxt.readTreeAsValue(childNode, this.valueType));
            } else {
                category.getSubcategories().put(key, deserializeCategory(childNode, ctxt, depth + 1));
            }
        }
        return category;
    }

    private boolean isLeafNode(JsonNode node) {
        if (!node.isObject()) return true;

        if (this.valueType != null) {
            Class<?> rawClass = this.valueType.getRawClass();
            if (Map.class.isAssignableFrom(rawClass)) {
                for (Iterator<JsonNode> it = node.elements(); it.hasNext(); ) {
                    JsonNode child = it.next();
                    if (child.isObject()) {
                        return false;
                    }
                }
                return true;
            }
        }

        return node.has("type");
    }
}
