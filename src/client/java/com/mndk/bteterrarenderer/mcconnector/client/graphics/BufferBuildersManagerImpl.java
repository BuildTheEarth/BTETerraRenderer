package com.mndk.bteterrarenderer.mcconnector.client.graphics;

import com.mndk.bteterrarenderer.mcconnector.client.graphics.shape.GraphicsQuad;
import com.mndk.bteterrarenderer.mcconnector.client.graphics.shape.GraphicsTriangle;
import com.mndk.bteterrarenderer.mcconnector.client.graphics.vertex.PosTex;
import com.mndk.bteterrarenderer.mcconnector.client.graphics.vertex.PosTexNorm;
import com.mndk.bteterrarenderer.mcconnector.util.math.McCoord;
import com.mojang.blaze3d.pipeline.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
//? if >=1.21.11 {
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
//? } else {
/*import net.minecraft.Util;
*///? }
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources./*? if >=1.21.11 {*/Identifier/*? } else {*//*ResourceLocation*//*? }*/;
import net.minecraft.util.*;
import org.joml.Vector2f;

import java.util.function.BiFunction;

public class BufferBuildersManagerImpl implements BufferBuildersManager {

//? if >=1.21.11 {
    /**
     * Minecraft 1.21.11 switched RenderType creation to the RenderSetup API.
     */
    private static RenderSetup generateSetup(RenderPipeline pipeline, Identifier texture) {
        return RenderSetup.builder(pipeline)
                // Sampler name must match what the pipeline declares via withSampler(...)
                .withTexture("Sampler0", texture)
                .useLightmap()
                .useOverlay()
                .sortOnUpload()
                .bufferSize(1536)
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .createRenderSetup();
    }

    private static final BiFunction<VertexFormat.Mode, Boolean, RenderPipeline> PIPELINE = Util.memoize(
            (drawMode, cull) -> RenderPipelines.register(RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
                    .withLocation("pipeline/entity_translucent")
                    .withSampler("Sampler1")
                    .withVertexFormat(/*? if >=26.1 {*/DefaultVertexFormat.ENTITY/*? } else {*//*DefaultVertexFormat.NEW_ENTITY*//*? }*/, drawMode)
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .withCull(cull)
                    .build()
            )
    );

    private static final BiFunction</*? if >=1.21.11 {*/Identifier/*? } else {*//*ResourceLocation*//*? }*/, Boolean, RenderType> QUADS = Util.memoize(
            (texture, cull) -> {
                RenderPipeline pipeline = PIPELINE.apply(VertexFormat.Mode.QUADS, cull);
                return RenderType.create("bteterrarenderer-quads", generateSetup(pipeline, texture));
            }
    );

    private static final BiFunction</*? if >=1.21.11 {*/Identifier/*? } else {*//*ResourceLocation*//*? }*/, Boolean, RenderType> TRIS = Util.memoize(
            (texture, cull) -> {
                RenderPipeline pipeline = PIPELINE.apply(VertexFormat.Mode.TRIANGLES, cull);
                return RenderType.create("bteterrarenderer-tris", generateSetup(pipeline, texture));
            }
    );
//? } else if >=1.21.5 {
    /*private static RenderType.CompositeState generateParameters(/^? if >=1.21.11 {^/Identifier/^? } else {^//^ResourceLocation^//^? }^/ texture) {
        return RenderType.CompositeState.builder()
//? if >=1.21.6 {
                .setTextureState(new RenderStateShard.TextureStateShard(texture, true))
//? } else {
                /^.setTextureState(new RenderStateShard.TextureStateShard(texture, TriState.TRUE, true))
^///? }
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(true);
    }

    private static final BiFunction<VertexFormat.Mode, Boolean, RenderPipeline> PIPELINE = Util.memoize(
            (drawMode, cull) -> RenderPipelines.register(RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
                    .withLocation("pipeline/entity_translucent")
                    .withSampler("Sampler1")
                    .withVertexFormat(DefaultVertexFormat.NEW_ENTITY, drawMode)
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .withCull(cull)
                    .build()
            )
    );

    private static final BiFunction</^? if >=1.21.11 {^/Identifier/^? } else {^//^ResourceLocation^//^? }^/, Boolean, RenderType> QUADS = Util.memoize(
            (texture, cull) -> RenderType.create(
                    "bteterrarenderer-quads", 1536, true, true,
                    PIPELINE.apply(VertexFormat.Mode.QUADS, cull), generateParameters(texture)
            )
    );

    private static final BiFunction</^? if >=1.21.11 {^/Identifier/^? } else {^//^ResourceLocation^//^? }^/, Boolean, RenderType> TRIS = Util.memoize(
            (texture, cull) -> RenderType.create(
                    "bteterrarenderer-tris", 1536, true, true,
                    PIPELINE.apply(VertexFormat.Mode.TRIANGLES, cull), generateParameters(texture)
            )
    );
*///? } else {
    /*private static RenderType.CompositeState generateParameters(ResourceLocation texture, boolean cull) {
        return RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, /^? if >=1.21.2 {^/TriState.TRUE/^? } else {^//^true^//^? }^/, true))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(cull ? RenderStateShard.CULL : RenderStateShard.NO_CULL)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(true);
    }

    private static final BiFunction<ResourceLocation, Boolean, RenderType> QUADS = Util.memoize((texture, cull) -> RenderType.create(
            "bteterrarenderer-quads", DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS, 1536, true, true, generateParameters(texture, cull)
    ));

    private static final BiFunction<ResourceLocation, Boolean, RenderType> TRIS = Util.memoize((texture, cull) -> RenderType.create(
            "bteterrarenderer-tris", DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.TRIANGLES, 1536, true, true, generateParameters(texture, cull)
    ));
*///? }

    @Override
    public BufferBuilderWrapper<GraphicsQuad<PosTex>> begin3dQuad(NativeTextureWrapper texture, float alpha, boolean cull) {
        var id = ((NativeTextureWrapperImpl) texture).delegate;
        RenderType renderLayer = QUADS.apply(id, cull);

        // DrawMode.QUADS
        // DefaultVertexFormat.NEW_ENTITY
        return new QuadBufferBuilderWrapper<>() {
            private PoseStack.Pose entry;
            private VertexConsumer consumer;
            public void setContext(WorldDrawContextWrapper context) {
                this.entry = ((WorldDrawContextWrapperImpl) context).stack().last();
                this.consumer = ((WorldDrawContextWrapperImpl) context).provider().getBuffer(renderLayer);
            }
            public void next(PosTex vertex) {
                McCoord tp = this.getTransformer().transform(vertex.pos);
                nextVertex(entry, consumer, tp, vertex.tex, new McCoord(0, 1, 0), alpha);
            }
        };
    }

    @Override
    public BufferBuilderWrapper<GraphicsTriangle<PosTexNorm>> begin3dTri(NativeTextureWrapper texture,
                                                                         float alpha, boolean enableNormal, boolean cull) {
        var id = ((NativeTextureWrapperImpl) texture).delegate;
        RenderType renderLayer = TRIS.apply(id, cull);

        // DrawMode.QUADS
        // DefaultVertexFormat.NEW_ENTITY
        return new TriangleBufferBuilderWrapper<>() {
            private PoseStack.Pose entry;
            private VertexConsumer consumer;
            public void setContext(WorldDrawContextWrapper context) {
                this.entry = ((WorldDrawContextWrapperImpl) context).stack().last();
                this.consumer = ((WorldDrawContextWrapperImpl) context).provider().getBuffer(renderLayer);
            }
            public void next(PosTexNorm vertex) {
                PosTexNorm tv = vertex.transform(this.getTransformer());
                nextVertex(entry, consumer, tv.pos, tv.tex, enableNormal ? tv.normal : new McCoord(0, 1, 0), alpha);
            }
        };
    }

    private static void nextVertex(PoseStack.Pose entry, VertexConsumer consumer,
                                   McCoord pos, Vector2f tex, McCoord normal, float alpha) {
//? if >=1.21 {
        consumer.addVertex(entry, (float) pos.getX(), pos.getY(), (float) pos.getZ())
                .setColor(1, 1, 1, alpha)
                .setUv(tex.x, tex.y)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0x00F000F0)
                .setNormal(entry, (float) normal.getX(), normal.getY(), (float) normal.getZ());
//? } else {
        /*consumer.vertex(entry/^? if <1.20.5 {^//^.pose()^//^? }^/, (float) pos.getX(), pos.getY(), (float) pos.getZ())
                .color(1, 1, 1, alpha)
                .uv(tex.x, tex.y)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .color(0x00F000F0)
                .normal(entry/^? if <1.20.5 {^//^.normal()^//^? }^/, (float) normal.getX(), normal.getY(), (float) normal.getZ());
*///? }
    }
}
