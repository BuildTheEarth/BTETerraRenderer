package com.mndk.bteterrarenderer.mcconnector.client.graphics;

import com.mndk.bteterrarenderer.mcconnector.client.graphics.shape.GraphicsQuad;
import com.mndk.bteterrarenderer.mcconnector.client.graphics.shape.GraphicsTriangle;
import com.mndk.bteterrarenderer.mcconnector.client.graphics.vertex.PosTex;
import com.mndk.bteterrarenderer.mcconnector.client.graphics.vertex.PosTexNorm;
import com.mndk.bteterrarenderer.mcconnector.util.math.McCoord;
//? if >=1.21.5 {
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.RenderPipelines;
//? }
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.*;
import org.joml.Vector2f;

import java.util.function.BiFunction;

public class BufferBuildersManagerImpl implements BufferBuildersManager {

//? if >=1.21.11 {
    /**
     * Minecraft 1.21.11 switched RenderLayer creation to the RenderSetup API.
     */
    private static RenderSetup generateSetup(RenderPipeline pipeline, Identifier texture) {
        return RenderSetup.builder(pipeline)
                // Sampler name must match what the pipeline declares via withSampler(...)
                .texture("Sampler0", texture)
                .useLightmap()
                .useOverlay()
                .translucent()
                .expectedBufferSize(1536)
                .outlineMode(RenderSetup.OutlineMode.AFFECTS_OUTLINE)
                .build();
    }

    private static final BiFunction<VertexFormat.DrawMode, Boolean, RenderPipeline> PIPELINE = Util.memoize(
            (drawMode, cull) -> RenderPipelines.register(RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
                    .withLocation("pipeline/entity_translucent")
                    .withSampler("Sampler1")
                    .withVertexFormat(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, drawMode)
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .withCull(cull)
                    .build()
            )
    );

    private static final BiFunction<Identifier, Boolean, RenderLayer> QUADS = Util.memoize(
            (texture, cull) -> {
                RenderPipeline pipeline = PIPELINE.apply(VertexFormat.DrawMode.QUADS, cull);
                return RenderLayer.of("bteterrarenderer-quads", generateSetup(pipeline, texture));
            }
    );

    private static final BiFunction<Identifier, Boolean, RenderLayer> TRIS = Util.memoize(
            (texture, cull) -> {
                RenderPipeline pipeline = PIPELINE.apply(VertexFormat.DrawMode.TRIANGLES, cull);
                return RenderLayer.of("bteterrarenderer-tris", generateSetup(pipeline, texture));
            }
    );
//? } else if >=1.21.5 {
    /*private static RenderLayer.MultiPhaseParameters generateParameters(Identifier texture) {
        return RenderLayer.MultiPhaseParameters.builder()
//? if >=1.21.6 {
                .texture(new RenderPhase.Texture(texture, true))
//? } else {
                /^.texture(new RenderPhase.Texture(texture, TriState.TRUE, true))
^///? }
                .lightmap(RenderPhase.ENABLE_LIGHTMAP)
                .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
                .build(true);
    }

    private static final BiFunction<VertexFormat.DrawMode, Boolean, RenderPipeline> PIPELINE = Util.memoize(
            (drawMode, cull) -> RenderPipelines.register(RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
                    .withLocation("pipeline/entity_translucent")
                    .withSampler("Sampler1")
                    .withVertexFormat(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, drawMode)
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .withCull(cull)
                    .build()
            )
    );

    private static final BiFunction<Identifier, Boolean, RenderLayer> QUADS = Util.memoize(
            (texture, cull) -> RenderLayer.of(
                    "bteterrarenderer-quads", 1536, true, true,
                    PIPELINE.apply(VertexFormat.DrawMode.QUADS, cull), generateParameters(texture)
            )
    );

    private static final BiFunction<Identifier, Boolean, RenderLayer> TRIS = Util.memoize(
            (texture, cull) -> RenderLayer.of(
                    "bteterrarenderer-tris", 1536, true, true,
                    PIPELINE.apply(VertexFormat.DrawMode.TRIANGLES, cull), generateParameters(texture)
            )
    );
*///? } else {
    /*private static RenderLayer.MultiPhaseParameters generateParameters(Identifier texture, boolean cull) {
        return RenderLayer.MultiPhaseParameters.builder()
//? if >=1.19.3 {
                .program(RenderPhase.ENTITY_TRANSLUCENT_PROGRAM)
//? } else {
                /^.shader(RenderPhase.ENTITY_TRANSLUCENT_SHADER)
^///? }
                .texture(new RenderPhase.Texture(texture, /^? if >=1.21.2 {^/TriState.TRUE/^? } else {^//^true^//^? }^/, true))
                .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                .cull(cull ? RenderPhase.ENABLE_CULLING : RenderPhase.DISABLE_CULLING)
                .lightmap(RenderPhase.ENABLE_LIGHTMAP)
                .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
                .build(true);
    }

    private static final BiFunction<Identifier, Boolean, RenderLayer> QUADS = Util.memoize((texture, cull) -> RenderLayer.of(
            "bteterrarenderer-quads", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS, 1536, true, true, generateParameters(texture, cull)
    ));

    private static final BiFunction<Identifier, Boolean, RenderLayer> TRIS = Util.memoize((texture, cull) -> RenderLayer.of(
            "bteterrarenderer-tris", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
            VertexFormat.DrawMode.TRIANGLES, 1536, true, true, generateParameters(texture, cull)
    ));
*///? }

    @Override
    public BufferBuilderWrapper<GraphicsQuad<PosTex>> begin3dQuad(NativeTextureWrapper texture, float alpha, boolean cull) {
        Identifier id = ((NativeTextureWrapperImpl) texture).delegate;
        RenderLayer renderLayer = QUADS.apply(id, cull);

        // DrawMode.QUADS
        // VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
        return new QuadBufferBuilderWrapper<>() {
            private MatrixStack.Entry entry;
            private VertexConsumer consumer;
            public void setContext(WorldDrawContextWrapper context) {
                this.entry = ((WorldDrawContextWrapperImpl) context).stack().peek();
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
        Identifier id = ((NativeTextureWrapperImpl) texture).delegate;
        RenderLayer renderLayer = TRIS.apply(id, cull);

        // DrawMode.QUADS
        // VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
        return new TriangleBufferBuilderWrapper<>() {
            private MatrixStack.Entry entry;
            private VertexConsumer consumer;
            public void setContext(WorldDrawContextWrapper context) {
                this.entry = ((WorldDrawContextWrapperImpl) context).stack().peek();
                this.consumer = ((WorldDrawContextWrapperImpl) context).provider().getBuffer(renderLayer);
            }
            public void next(PosTexNorm vertex) {
                PosTexNorm tv = vertex.transform(this.getTransformer());
                nextVertex(entry, consumer, tv.pos, tv.tex, enableNormal ? tv.normal : new McCoord(0, 1, 0), alpha);
            }
        };
    }

    private static void nextVertex(MatrixStack.Entry entry, VertexConsumer consumer,
                                   McCoord pos, Vector2f tex, McCoord normal, float alpha) {
        consumer.vertex(entry/*? if <1.20.5 {*//*.getPositionMatrix()*//*? }*/, (float) pos.getX(), pos.getY(), (float) pos.getZ())
                .color(1, 1, 1, alpha)
                .texture(tex.x, tex.y)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(0x00F000F0)
                .normal(entry/*? if <1.20.5 {*//*.getNormalMatrix()*//*? }*/, (float) normal.getX(), normal.getY(), (float) normal.getZ());
    }
}
