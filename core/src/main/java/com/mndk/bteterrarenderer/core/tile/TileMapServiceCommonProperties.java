package com.mndk.bteterrarenderer.core.tile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.mndk.bteterrarenderer.dep.terraplusplus.projection.GeographicProjection;
import com.mndk.bteterrarenderer.mcconnector.i18n.Translatable;
import com.mndk.bteterrarenderer.util.concurrent.CacheStorage;
import com.mndk.bteterrarenderer.util.json.JsonString;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TileMapServiceCommonProperties {

    @Getter
    @NoArgsConstructor
    public static class UrlOption {
        private String url;

        @JsonCreator
        public UrlOption(@JsonProperty(value = "url", required = true) String url) {
            this.url = url;
        }
    }

    private Translatable<String> name;
    private List<UrlOption> tileUrls;
    private int nThreads;
    @Nullable
    private URL hudImageUrl;
    @Nullable
    private URL iconUrl;
    @Nullable
    private Translatable<JsonString> copyrightTextJson;
    @Nullable
    private GeographicProjection hologramProjection;
    @Nullable
    private String apiKey;
    @Nullable
    private CacheStorage.Config cacheConfig;

    @JsonCreator
    public TileMapServiceCommonProperties(
            @JsonProperty(value = "name", required = true) Translatable<String> name,
            @JsonProperty(value = "tile_url", required = true) Object tileUrl,
            @Nullable @JsonProperty("max_thread") Integer nThreads,
            @Nullable @JsonProperty("copyright") Translatable<JsonString> copyrightTextJson,
            @Nullable @JsonProperty("icon_url") URL iconUrl,
            @Nullable @JsonProperty("hud_image") URL hudImageUrl,
            @Nullable @JsonProperty("hologram_projection") GeographicProjection hologramProjection,
            @Nullable @JsonProperty("api_key") String apiKey,
            @Nullable @JsonProperty("cache") CacheStorage.Config cacheConfig
    ) {
        this.name = name;
        this.tileUrls = parseTileUrls(tileUrl);
        this.copyrightTextJson = copyrightTextJson;
        this.iconUrl = iconUrl;
        this.hudImageUrl = hudImageUrl;
        this.nThreads = nThreads != null ? nThreads : AbstractTileMapService.DEFAULT_MAX_THREAD;
        this.hologramProjection = hologramProjection;
        this.apiKey = apiKey;
        this.cacheConfig = cacheConfig;
    }

    private static List<UrlOption> parseTileUrls(Object tileUrl) {
        if (tileUrl instanceof String) {
            return Collections.singletonList(new UrlOption((String) tileUrl));
        }
        if (tileUrl instanceof List<?>) {
            List<UrlOption> urls = new ArrayList<>();
            for (Object element : (List<?>) tileUrl) {
                if (!(element instanceof String)) {
                    throw new IllegalArgumentException("'tile_url' list elements must be strings");
                }
                urls.add(new UrlOption((String) element));
            }
            if (urls.isEmpty()) {
                throw new IllegalArgumentException("'tile_url' must not be an empty list");
            }
            return urls;
        }
        throw new IllegalArgumentException("'tile_url' must be a string or a list of strings");
    }

    public String getTileUrl() {
        return tileUrls == null || tileUrls.isEmpty() ? null : tileUrls.get(0).getUrl();
    }

    void write(JsonGenerator gen) throws IOException {
        gen.writeObjectField("name", this.name);
        this.writeTileUrl(gen);
        if (this.iconUrl != null) {
            gen.writeStringField("icon_url", this.iconUrl.toString());
        }
        if (this.hudImageUrl != null) {
            gen.writeStringField("hud_image", this.hudImageUrl.toString());
        }
        gen.writeNumberField("max_thread", this.nThreads);
        gen.writeObjectField("copyright", this.copyrightTextJson);
        gen.writeObjectField("hologram_projection", this.hologramProjection);
        if (this.apiKey != null) {
            gen.writeStringField("api_key", this.apiKey);
        }
    }

    private void writeTileUrl(JsonGenerator gen) throws IOException {
        if (tileUrls == null || tileUrls.isEmpty()) return;
        if (tileUrls.size() == 1) {
            gen.writeStringField("tile_url", tileUrls.get(0).getUrl());
        } else {
            gen.writeFieldName("tile_url");
            gen.writeStartArray();
            for (UrlOption option : tileUrls) {
                gen.writeString(option.getUrl());
            }
            gen.writeEndArray();
        }
    }

    static TileMapServiceCommonProperties from(AbstractTileMapService<?> tms) {
        TileMapServiceCommonProperties result = new TileMapServiceCommonProperties();
        result.name = tms.getName();
        result.iconUrl = tms.getIconUrl();
        result.hudImageUrl = tms.getHudImageUrl();
        result.nThreads = tms.getNThreads();
        result.copyrightTextJson = Optional.ofNullable(tms.getCopyrightTextJson())
                .map(json -> json.map(JsonString::fromUnsafe))
                .orElse(null);
        result.hologramProjection = tms.getHologramProjection();
        if (tms instanceof com.mndk.bteterrarenderer.core.tile.flat.FlatTileMapService) {
            result.tileUrls = ((com.mndk.bteterrarenderer.core.tile.flat.FlatTileMapService) tms).getUrlOptions();
        } else {
            result.tileUrls = Collections.singletonList(new UrlOption(tms.getDummyTileUrl()));
        }

        return result;
    }
}
