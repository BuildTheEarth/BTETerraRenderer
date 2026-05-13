package com.mndk.bteterrarenderer.mcconnector.client.gui;

import com.mndk.bteterrarenderer.mcconnector.McConnector;
import com.mndk.bteterrarenderer.mcconnector.client.WindowDimension;
import com.mndk.bteterrarenderer.mcconnector.client.graphics.NativeTextureWrapper;
import com.mndk.bteterrarenderer.mcconnector.client.graphics.NativeTextureWrapperImpl;
import com.mndk.bteterrarenderer.mcconnector.client.graphics.shape.GraphicsQuad;
import com.mndk.bteterrarenderer.mcconnector.client.graphics.vertex.PosXY;
import com.mndk.bteterrarenderer.mcconnector.client.gui.screen.AbstractGuiScreenImpl;
import com.mndk.bteterrarenderer.mcconnector.client.gui.widget.AbstractWidgetCopy;
import com.mndk.bteterrarenderer.mcconnector.client.text.FontWrapper;
import com.mndk.bteterrarenderer.mcconnector.client.text.FontWrapperImpl;
import com.mndk.bteterrarenderer.mcconnector.client.text.StyleWrapper;
import com.mndk.bteterrarenderer.mcconnector.client.text.StyleWrapperImpl;
import com.mndk.bteterrarenderer.mcconnector.util.ResourceLocationWrapper;
import com.mndk.bteterrarenderer.mcconnector.util.ResourceLocationWrapperImpl;
import com.mndk.bteterrarenderer.mod.util.IdUtil;
//? if >=1.21.5 {
import com.mojang.blaze3d.pipeline.RenderPipeline;
//? }
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.*;
//? if >=1.21.5 {
import net.minecraft.client.gui.navigation.ScreenRectangle;
//? }
//? if >=1.21.6 {
import net.minecraft.client.gui.render.TextureSetup;
//? if >=26.1 {
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
//? } else {
/*import net.minecraft.client.gui.render.state.GuiElementRenderState;
*///? }
//? }
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.*;
import net.minecraft.network.chat.Style;
import net.minecraft.resources./*? if >=1.21.11 {*/Identifier/*? } else {*//*ResourceLocation*//*? }*/;
import net.minecraft.util.*;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.Math;

@RequiredArgsConstructor
public class GuiDrawContextWrapperImpl extends AbstractGuiDrawContextWrapper {

//? if >=1.20.2 {
    private static final /*? if >=1.21.11 {*/Identifier/*? } else {*//*ResourceLocation*//*? }*/ CHECKBOX_SELECTED_HIGHLIGHTED = IdUtil.withDefaultNamespace("widget/checkbox_selected_highlighted");
    private static final /*? if >=1.21.11 {*/Identifier/*? } else {*//*ResourceLocation*//*? }*/ CHECKBOX_SELECTED = IdUtil.withDefaultNamespace("widget/checkbox_selected");
    private static final /*? if >=1.21.11 {*/Identifier/*? } else {*//*ResourceLocation*//*? }*/ CHECKBOX_HIGHLIGHTED = IdUtil.withDefaultNamespace("widget/checkbox_highlighted");
    private static final /*? if >=1.21.11 {*/Identifier/*? } else {*//*ResourceLocation*//*? }*/ CHECKBOX = IdUtil.withDefaultNamespace("widget/checkbox");

    private static final WidgetSprites BUTTON_TEXTURES = new WidgetSprites(
            IdUtil.withDefaultNamespace("widget/button"),
            IdUtil.withDefaultNamespace("widget/button_disabled"),
            IdUtil.withDefaultNamespace("widget/button_highlighted")
    );
//? } else {
    /*private static final /^? if >=1.21.11 {^/Identifier/^? } else {^//^ResourceLocation^//^? }^/ CHECKBOX_TEXTURE = IdUtil.withDefaultNamespace("textures/gui/checkbox.png");
*///? }

//? if >=26.1 {
    @Nonnull public final GuiGraphicsExtractor delegate;
//? } else if >=1.20 {
    /*@Nonnull public final GuiGraphics delegate;
*///? } else {
    /*@Nonnull public final PoseStack delegate;
*///? }

//? if >=1.21.6 {
    private int scissorDepth;

    public void translate(float x, float y, float z) {
        // GUI matrices are 2D in this version
        delegate.pose().translate(x, y);
    }

    public void pushMatrix() {
        delegate.pose().pushMatrix();
    }

    public void popMatrix() {
        delegate.pose().popMatrix();
    }

    @Override
    protected boolean usesNativeScissorStack() {
        return true;
    }

    protected int[] getAbsoluteScissorDimension(int relX, int relY, int relWidth, int relHeight) {
        WindowDimension window = McConnector.client().getWindowSize();
        if (window.getScaledWidth() == 0 || window.getScaledHeight() == 0) {
            return new int[] { 0, 0, 0, 0 };
        }

        // DrawContext.enableScissor applies the current matrix transform.
        return new int[] { relX, relY, relWidth, relHeight };
    }

    protected void glEnableScissor(int x, int y, int width, int height) {
        // DrawContext scissor uses x1,y1,x2,y2
        delegate.enableScissor(x, y, x + width, y + height);
        scissorDepth++;
    }

    protected void glDisableScissor() {
        if (scissorDepth > 0) {
            delegate.disableScissor();
            scissorDepth--;
        }
    }

    // Vertex system changed in 1.21.6
    public record QuadRenderState(
            RenderPipeline pipeline,
            TextureSetup textureSetup,
            /*? if >=1.21.11 {*/Matrix3x2fc/*? } else {*//*Matrix3x2f*//*? }*/ pose,
            GraphicsQuad<PosXY> quad,
            int color,
            @Nullable ScreenRectangle scissorArea,
            @Nullable ScreenRectangle bounds
    ) implements GuiElementRenderState {
        public QuadRenderState(
                RenderPipeline pipeline, TextureSetup textureSetup, /*? if >=1.21.11 {*/Matrix3x2fc/*? } else {*//*Matrix3x2f*//*? }*/ pose, GraphicsQuad<PosXY> quad,
                int color, @Nullable ScreenRectangle scissorArea
        ) {
            this(pipeline, textureSetup, pose, quad, color, scissorArea, createBounds(quad, pose, scissorArea));
        }

//? if >=1.21.9 {
        // Depth removed in 1.21.9
        public void buildVertices(VertexConsumer vertices) {
            vertices.addVertexWith2DPose(pose(), quad.v0.x, quad.v0.y).setColor(color);
            vertices.addVertexWith2DPose(pose(), quad.v1.x, quad.v1.y).setColor(color);
            vertices.addVertexWith2DPose(pose(), quad.v2.x, quad.v2.y).setColor(color);
            vertices.addVertexWith2DPose(pose(), quad.v3.x, quad.v3.y).setColor(color);
        }
//? } else {
        /*public void buildVertices(VertexConsumer vertices, float depth) {
            vertices.addVertexWith2DPose(pose(), quad.v0.x, quad.v0.y, depth).setColor(color);
            vertices.addVertexWith2DPose(pose(), quad.v1.x, quad.v1.y, depth).setColor(color);
            vertices.addVertexWith2DPose(pose(), quad.v2.x, quad.v2.y, depth).setColor(color);
            vertices.addVertexWith2DPose(pose(), quad.v3.x, quad.v3.y, depth).setColor(color);
        }
*///? }

        private static ScreenRectangle createBounds(GraphicsQuad<PosXY> quad, /*? if >=1.21.11 {*/Matrix3x2fc/*? } else {*//*Matrix3x2f*//*? }*/ pose, @Nullable ScreenRectangle scissorArea) {
            int l = Mth.floor(Math.min(Math.min(quad.v0.x, quad.v1.x), Math.min(quad.v2.x, quad.v3.x)));
            int t = Mth.floor(Math.min(Math.min(quad.v0.y, quad.v1.y), Math.min(quad.v2.y, quad.v3.y)));
            int r = Mth.ceil(Math.max(Math.max(quad.v0.x, quad.v1.x), Math.max(quad.v2.x, quad.v3.x)));
            int b = Mth.ceil(Math.max(Math.max(quad.v0.y, quad.v1.y), Math.max(quad.v2.y, quad.v3.y)));
            ScreenRectangle screenRect = new ScreenRectangle(l, t, r - l, b - t).transformMaxBounds(pose);
            return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
        }
    }

    public void fillQuad(GraphicsQuad<PosXY> quad, int color, float z) {
        QuadRenderState renderState = new QuadRenderState(
                RenderPipelines.GUI, TextureSetup.noTexture(), new Matrix3x2f(delegate.pose()), quad, color,
                delegate.scissorStack.peek());
//? if >=26.1 {
        delegate.guiRenderState.addGuiElement(renderState);
//? } else {
        /*delegate.guiRenderState.submitGuiElement(renderState);
*///? }
    }
//? } else {
    /*public void translate(float x, float y, float z) {
        getPose().translate(x, y, z);
    }
    public void pushMatrix() {
        getPose().pushPose();
    }
    public void popMatrix() {
        getPose().popPose();
    }

    protected int[] getAbsoluteScissorDimension(int relX, int relY, int relWidth, int relHeight) {
        WindowDimension window = McConnector.client().getWindowSize();
        if (window.getScaledWidth() == 0 || window.getScaledHeight() == 0) { // Division by zero handling
            return new int[] { 0, 0, 0, 0 };
        }
        float scaleFactorX = window.getScaleFactorX();
        float scaleFactorY = window.getScaleFactorY();

        var matrix = getPose().last().pose();
//? if >=1.19.3 {
        var start = new org.joml.Vector4f(relX, relY, 0, 1);
        var end = new org.joml.Vector4f(relX + relWidth, relY + relHeight, 0, 1);
        start = matrix.transform(start);
        end = matrix.transform(end);
//? } else {
        /^var start = new com.mojang.math.Vector4f(relX, relY, 0, 1);
        var end = new com.mojang.math.Vector4f(relX + relWidth, relY + relHeight, 0, 1);
        start.transform(matrix);
        end.transform(matrix);
^///? }

        int scissorX = (int) (scaleFactorX * Math.min(start.x(), end.x()));
        int scissorY = (int) (window.getPixelHeight() - scaleFactorY * Math.max(start.y(), end.y()));
        int scissorWidth = (int) (scaleFactorX * Math.abs(start.x() - end.x()));
        int scissorHeight = (int) (scaleFactorY * Math.abs(start.y() - end.y()));
        return new int[] { scissorX, scissorY, scissorWidth, scissorHeight };
    }
    protected void glEnableScissor(int x, int y, int width, int height) {
//? if >=1.20 {
        delegate.flush();
//? }
        RenderSystem.enableScissor(x, y, width, height);
    }
    protected void glDisableScissor() {
//? if >=1.20 {
        delegate.flush();
//? }
        RenderSystem.disableScissor();
    }

    public void fillQuad(GraphicsQuad<PosXY> quad, int color, float z) {
        var matrix4f = getPose().last().pose();
//? if >=1.20 {
        VertexConsumer vertexConsumer = delegate
//? if >=1.21.2 {
                .bufferSource // Must use AccessWidener
//? } else {
                /^.bufferSource()
^///? }
                .getBuffer(RenderType.gui());
//? if >=1.21 {
        quad.forEach(v -> vertexConsumer.addVertex(matrix4f, v.x, v.y, z).setColor(color));
//? } else {
        /^quad.forEach(v -> vertexConsumer.vertex(matrix4f, v.x, v.y, z).color(color));
^///? }
//? } else if >=1.19.3 {
        /^BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        quad.forEach(v -> bufferBuilder.vertex(matrix4f, v.x, v.y, z).color(color).endVertex());
        BufferUploader.drawWithShader(bufferBuilder.end());
        RenderSystem.disableBlend();
^///? } else {
        /^Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        quad.forEach(v -> bufferBuilder.vertex(matrix4f, v.x, v.y, z).color(color).endVertex());
        tessellator.end();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
^///? }
    }
*///? }

    public void drawButton(int x, int y, int width, int height, AbstractWidgetCopy.HoverState hoverState) {
//? if >=1.20.2 {
        boolean enabled = hoverState != AbstractWidgetCopy.HoverState.DISABLED;
        boolean focused = hoverState == AbstractWidgetCopy.HoverState.MOUSE_OVER;
        var buttonTexture = BUTTON_TEXTURES.get(enabled, focused);

//? if >=1.21.2 {
        delegate.blitSprite(
                /*? if >=1.21.6 {*/RenderPipelines.GUI_TEXTURED/*? } else {*//*RenderType::guiTextured*//*? }*/,
                buttonTexture, x, y, width, height);
//? } else {
        /*delegate.blitSprite(buttonTexture, x, y, width, height);
*///? }
//? } else {
        /*int i = switch (hoverState) {
            case DISABLED -> 0;
            case DEFAULT -> 1;
            case MOUSE_OVER -> 2;
        };
//? if >=1.20 {
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        delegate.setColor(1, 1, 1, 1);
        delegate.blitNineSliced(
                AbstractWidget.WIDGETS_LOCATION, x, y, width, height, 20, 4, 200, 20,
                0, 46 + i * 20);
//? } else if >=1.19.4 {
        /^RenderSystem.setShaderTexture(0, AbstractWidget.WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        GuiComponent.blitNineSliced(delegate, x, y, width, height, 20, 4, 200, 20, 0, 46 + i * 20);
^///? } else {
        /^RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, AbstractWidget.WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        GuiComponent.blit(delegate, x, y, 0, 0, 46 + i * 20, width / 2, height, 256, 256);
        GuiComponent.blit(delegate, x + width / 2, y, 0, 200 - (float) width / 2, 46 + i * 20, width / 2, height, 256, 256);
^///? }
*///? }
    }

    public void drawCheckBox(int x, int y, int width, int height, boolean focused, boolean checked) {
//? if >=1.20.2 {
        var identifier = checked
                ? (focused ? CHECKBOX_SELECTED_HIGHLIGHTED : CHECKBOX_SELECTED)
                : (focused ? CHECKBOX_HIGHLIGHTED : CHECKBOX);

//? if >=1.21.2 {
        delegate.blitSprite(
                /*? if >=1.21.6 {*/RenderPipelines.GUI_TEXTURED/*? } else {*//*RenderType::guiTextured*//*? }*/,
                identifier, x, y, width, height, ARGB.white(1.0f));
//? } else {
        /*RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        delegate.setColor(1, 1, 1, 1);
        delegate.blitSprite(identifier, x, y, width, height);
*///? }
//? } else {
        /*float size = 20 / 64f;
        float u0 = focused ? size : 0, v0 = checked ? size : 0;
        float u1 = u0 + size, v1 = v0 + size;
//? if >=1.20 {
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        delegate.setColor(1, 1, 1, 1);
        delegate.innerBlit(CHECKBOX_TEXTURE, x, x+width, y, y+height, 0, u0, u1, v0, v1);
//? } else {
        /^var matrix4f = delegate.last().pose();
        RenderSystem.setShaderTexture(0, CHECKBOX_TEXTURE);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        GuiComponent.innerBlit(matrix4f, x, x+width, y, y+height, 0, u0, u1, v0, v1);
^///? }
*///? }
    }

    public void drawTextHighlight(int startX, int startY, int endX, int endY) {
//? if >=1.21.9 {
        delegate.textHighlight(startX, startY, endX, endY/*? if >=1.21.11 {*/, true/*? }*/);
//? } else if >=1.21.6 {
        /*delegate.fill(RenderPipelines.GUI_TEXT_HIGHLIGHT, startX, startY, endX, endY, 0xff0000ff);
*///? } else if >=1.20 {
        /*delegate.fill(RenderType.guiTextHighlight(), startX, startY, endX, endY, 0xff0000ff);
*///? } else if >=1.19 {
        /*RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(com.mojang.blaze3d.platform.GlStateManager.LogicOp.OR_REVERSE);
        GuiComponent.fill(delegate, startX, startY, endX, endY, 0xff0000ff);
        RenderSystem.disableColorLogicOp();
*///? } else {
        /*Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(0.0F, 0.0F, 1.0F, 1.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(com.mojang.blaze3d.platform.GlStateManager.LogicOp.OR_REVERSE);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        bufferBuilder.vertex(startX, endY, 0).endVertex();
        bufferBuilder.vertex(endX, endY, 0).endVertex();
        bufferBuilder.vertex(endX, startY, 0).endVertex();
        bufferBuilder.vertex(startX, startY, 0).endVertex();
        tessellator.end();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
*///? }
    }

//? if >=1.21.6 {
    public void drawImage(ResourceLocationWrapper res, int x, int y, int w, int h,
                          float u1, float u2, float v1, float v2) {
        var texture = ((ResourceLocationWrapperImpl) res).delegate();
        delegate.blit(texture, x, y, x+w, y+h, u1, u2, v1, v2);
    }

    public void drawWholeNativeImage(@Nonnull NativeTextureWrapper allocatedTextureObject, int x, int y, int w, int h) {
        var texture = ((NativeTextureWrapperImpl) allocatedTextureObject).delegate;
        delegate.blit(texture, x, y, x+w, y+h, 0, 1, 0, 1);
    }
//? } else if >=1.21.2 {
    /*public void drawImage(ResourceLocationWrapper res, int x, int y, int w, int h, float u1, float u2, float v1, float v2) {
        var texture = ((ResourceLocationWrapperImpl) res).delegate();
        delegate.innerBlit(RenderType::guiTextured, texture, x, x+w, y, y+h, u1, u2, v1, v2, ARGB.white(1.0f));
    }

    public void drawWholeNativeImage(@Nonnull NativeTextureWrapper allocatedTextureObject, int x, int y, int w, int h) {
        var texture = ((NativeTextureWrapperImpl) allocatedTextureObject).delegate;
        delegate.blit(RenderType::guiTextured, texture, x, x+w, y, y+h, 0, 1, 0, 1, ARGB.white(1.0f));
    }
*///? } else if >=1.20 {
    /*public void drawImage(ResourceLocationWrapper res, int x, int y, int w, int h, float u1, float u2, float v1, float v2) {
        var texture = ((ResourceLocationWrapperImpl) res).delegate();
        delegate.innerBlit(texture, x, x+w, y, y+h, 0, u1, u2, v1, v2);
    }

    public void drawWholeNativeImage(@Nonnull NativeTextureWrapper allocatedTextureObject, int x, int y, int w, int h) {
        var texture = ((NativeTextureWrapperImpl) allocatedTextureObject).delegate;
        delegate.blit(texture, x, x+w, y, y+h, 0, 0, 1, 0, 1);
    }*/
//? } else {
    /*public void drawImage(ResourceLocationWrapper res, int x, int y, int w, int h, float u1, float u2, float v1, float v2) {
        var matrix4f = delegate.last().pose();
        RenderSystem.setShaderTexture(0, ((ResourceLocationWrapperImpl) res).delegate());
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
//? if <1.19 {
        /^RenderSystem.enableDepthTest();
^///? }
        GuiComponent.innerBlit(matrix4f, x, x+w, y, y+h, 0, u1, u2, v1, v2);
    }

    public void drawWholeNativeImage(@Nonnull NativeTextureWrapper allocatedTextureObject, int x, int y, int w, int h) {
        var matrix4f = delegate.last().pose();
        RenderSystem.setShaderTexture(0, ((NativeTextureWrapperImpl) allocatedTextureObject).delegate);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
//? if <1.19 {
        /^RenderSystem.enableDepthTest();
^///? }
        RenderSystem.enableDepthTest();
        GuiComponent.innerBlit(matrix4f, x, x+w, y, y+h, 0, 0, 1, 0, 1);
    }
*///? }

    public void drawHoverEvent(StyleWrapper styleWrapper, int x, int y) {
//? if >=1.20 {
        Font textRenderer = Minecraft.getInstance().font;
        Style style = ((StyleWrapperImpl) styleWrapper).delegate();
//? if >=26.1 {
        delegate.componentHoverEffect(textRenderer, style, x, y);
//? } else {
        /*delegate.renderComponentHoverEffect(textRenderer, style, x, y);
*///? }
//? } else {
        /*Screen currentScreen = Minecraft.getInstance().screen;
        if (!(currentScreen instanceof AbstractGuiScreenImpl guiScreen)) return;

        Style style = ((StyleWrapperImpl) styleWrapper).delegate();
        guiScreen.renderComponentHoverEffect(delegate, style, x, y);
*///? }
    }

    public void drawTextWithShadow(FontWrapper fontWrapper, String string, float x, float y, int color) {
        Font textRenderer = ((FontWrapperImpl) fontWrapper).delegate;
//? if >=26.1 {
        delegate.text(textRenderer, string, (int) x, (int) y, color);
//? } else if >=1.20 {
        /*delegate.drawString(textRenderer, string, (int) x, (int) y, color);
*///? } else {
        /*textRenderer.drawShadow(delegate, string, x, y, color);
*///? }
    }

//? if >=1.21.6 {
    private org.joml.Matrix3x2fStack getPose() { return delegate.pose(); }
//? } else if >=1.20 {
    /*private PoseStack getPose() { return delegate.pose(); }
*///? } else {
    /*private PoseStack getPose() { return delegate; }
*///? }
}
