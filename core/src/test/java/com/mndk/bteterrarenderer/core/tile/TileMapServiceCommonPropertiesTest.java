package com.mndk.bteterrarenderer.core.tile;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.mndk.bteterrarenderer.BTETerraRenderer;
import com.mndk.bteterrarenderer.core.BTETerraRendererCore;
import com.mndk.bteterrarenderer.mcconnector.client.TestEnvironmentDummyMinecraft;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringWriter;

public class TileMapServiceCommonPropertiesTest {

    @Test
    public void givenSingleTileUrl_whenParsed_thenSingleUrlOption() throws Exception {
        String json = "{\"name\": {\"en_us\": \"Test\"}, \"tile_url\": \"https://example.com/{z}/{x}/{y}.png\"}";
        TileMapServiceCommonProperties properties =
                BTETerraRenderer.JSON_MAPPER.readValue(json, TileMapServiceCommonProperties.class);

        Assert.assertEquals(1, properties.getTileUrls().size());
        Assert.assertEquals("https://example.com/{z}/{x}/{y}.png", properties.getTileUrl());
        Assert.assertEquals("https://example.com/{z}/{x}/{y}.png", properties.getTileUrls().get(0).getUrl());
    }

    @Test
    public void givenMultipleTileUrls_whenParsed_thenAllUrlOptionsKept() throws Exception {
        String json = "{\"name\": {\"en_us\": \"Test\"}, \"tile_url\": [\"https://a/{z}/{x}/{y}.png\", \"https://b/{z}/{x}/{y}.png\"]}";
        TileMapServiceCommonProperties properties =
                BTETerraRenderer.JSON_MAPPER.readValue(json, TileMapServiceCommonProperties.class);

        Assert.assertEquals(2, properties.getTileUrls().size());
        Assert.assertEquals("https://a/{z}/{x}/{y}.png", properties.getTileUrl());
        Assert.assertEquals("https://a/{z}/{x}/{y}.png", properties.getTileUrls().get(0).getUrl());
        Assert.assertEquals("https://b/{z}/{x}/{y}.png", properties.getTileUrls().get(1).getUrl());
    }

    @Test
    public void givenNoTileUrl_whenParsed_thenError() {
        String json = "{\"name\": {\"en_us\": \"Test\"}}";
        try {
            BTETerraRenderer.JSON_MAPPER.readValue(json, TileMapServiceCommonProperties.class);
            Assert.fail("Expected exception for missing tile_url");
        } catch (Exception ignored) {}
    }

    @Test
    public void givenSingleTileUrl_whenWriteCalled_thenWrittenAsString() throws Exception {
        String input = "{\"name\": {\"en_us\": \"Test\"}, \"tile_url\": \"https://example.com/{z}/{x}/{y}.png\"}";
        TileMapServiceCommonProperties properties =
                BTETerraRenderer.JSON_MAPPER.readValue(input, TileMapServiceCommonProperties.class);

        String json = writeJson(properties);
        Assert.assertTrue(json.contains("\"tile_url\":\"https://example.com/{z}/{x}/{y}.png\""));
        Assert.assertFalse(json.contains("tile_urls"));
    }

    @Test
    public void givenMultipleTileUrls_whenWriteCalled_thenWrittenAsArray() throws Exception {
        String input = "{\"name\": {\"en_us\": \"Test\"}, \"tile_url\": [\"https://a/{z}/{x}/{y}.png\", \"https://b/{z}/{x}/{y}.png\"]}";
        TileMapServiceCommonProperties properties =
                BTETerraRenderer.JSON_MAPPER.readValue(input, TileMapServiceCommonProperties.class);

        String json = writeJson(properties);
        Assert.assertTrue(json.contains("\"tile_url\":[\"https://a/{z}/{x}/{y}.png\",\"https://b/{z}/{x}/{y}.png\"]"));
        Assert.assertFalse(json.contains("tile_urls"));
    }

    private static String writeJson(TileMapServiceCommonProperties properties) throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = new JsonFactory().createGenerator(writer);
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
