package com.mndk.bteterrarenderer.core.tile;

import com.fasterxml.jackson.core.JsonGenerator;
import com.mndk.bteterrarenderer.BTETerraRenderer;
import com.mndk.bteterrarenderer.core.BTETerraRendererCore;
import com.mndk.bteterrarenderer.mcconnector.client.TestEnvironmentDummyMinecraft;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

public class TileMapServiceCommonPropertiesTest {

    @Test
    public void givenSingleTileUrl_whenParsed_thenSingleUrlOption() throws Exception {
        String json = "{\"name\": {\"en_us\": \"Test\"}, \"tile_url\": \"https://example.com/{z}/{x}/{y}.png\"}";
        TileMapServiceCommonProperties properties =
                BTETerraRenderer.JSON_MAPPER.readValue(json, TileMapServiceCommonProperties.class);

        Assertions.assertEquals(1, properties.getTileUrls().size());
        Assertions.assertEquals("https://example.com/{z}/{x}/{y}.png", properties.getTileUrl());
        Assertions.assertEquals("https://example.com/{z}/{x}/{y}.png", properties.getTileUrls().get(0).getUrl());
    }

    @Test
    public void givenMultipleTileUrls_whenParsed_thenAllUrlOptionsKept() throws Exception {
        String json = "{\"name\": {\"en_us\": \"Test\"}, \"tile_url\": [\"https://a/{z}/{x}/{y}.png\", \"https://b/{z}/{x}/{y}.png\"]}";
        TileMapServiceCommonProperties properties =
                BTETerraRenderer.JSON_MAPPER.readValue(json, TileMapServiceCommonProperties.class);

        Assertions.assertEquals(2, properties.getTileUrls().size());
        Assertions.assertEquals("https://a/{z}/{x}/{y}.png", properties.getTileUrl());
        Assertions.assertEquals("https://a/{z}/{x}/{y}.png", properties.getTileUrls().get(0).getUrl());
        Assertions.assertEquals("https://b/{z}/{x}/{y}.png", properties.getTileUrls().get(1).getUrl());
    }

    @Test
    public void givenNoTileUrl_whenParsed_thenError() {
        String json = "{\"name\": {\"en_us\": \"Test\"}}";
        try {
            BTETerraRenderer.JSON_MAPPER.readValue(json, TileMapServiceCommonProperties.class);
            Assertions.fail("Expected exception for missing tile_url");
        } catch (Exception ignored) {}
    }

    @Test
    public void givenSingleTileUrl_whenWriteCalled_thenWrittenAsString() throws Exception {
        String input = "{\"name\": {\"en_us\": \"Test\"}, \"tile_url\": \"https://example.com/{z}/{x}/{y}.png\"}";
        TileMapServiceCommonProperties properties =
                BTETerraRenderer.JSON_MAPPER.readValue(input, TileMapServiceCommonProperties.class);

        String json = writeJson(properties);
        Assertions.assertTrue(json.contains("\"tile_url\":\"https://example.com/{z}/{x}/{y}.png\""));
        Assertions.assertFalse(json.contains("tile_urls"));
    }

    @Test
    public void givenMultipleTileUrls_whenWriteCalled_thenWrittenAsArray() throws Exception {
        String input = "{\"name\": {\"en_us\": \"Test\"}, \"tile_url\": [\"https://a/{z}/{x}/{y}.png\", \"https://b/{z}/{x}/{y}.png\"]}";
        TileMapServiceCommonProperties properties =
                BTETerraRenderer.JSON_MAPPER.readValue(input, TileMapServiceCommonProperties.class);

        String json = writeJson(properties);
        Assertions.assertTrue(json.contains("\"tile_url\":[\"https://a/{z}/{x}/{y}.png\",\"https://b/{z}/{x}/{y}.png\"]"));
        Assertions.assertFalse(json.contains("tile_urls"));
    }

    private static String writeJson(TileMapServiceCommonProperties properties) throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = BTETerraRenderer.JSON_MAPPER.createGenerator(writer);
        gen.writeStartObject();
        properties.write(gen);
        gen.writeEndObject();
        gen.close();
        return writer.toString();
    }

    static {
        BTETerraRendererCore.initialize(TestEnvironmentDummyMinecraft.getInstance());
    }
}
