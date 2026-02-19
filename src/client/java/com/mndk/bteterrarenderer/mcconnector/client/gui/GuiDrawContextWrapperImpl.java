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
//? if >=1.21.5 {
import com.mojang.blaze3d.pipeline.RenderPipeline;
//? }
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
//? if >=1.21.5 {
import net.minecraft.client.gl.RenderPipelines;
//? }
import net.minecraft.client.gui.*;
//? if >=1.21.6 {
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
//? }
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.render.*;
//? if >=1.21.6 {
import net.minecraft.client.texture.TextureSetup;
//? }
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.Math;

@RequiredArgsConstructor
public class GuiDrawContextWrapperImpl extends AbstractGuiDrawContextWrapper {

//? if >=1.20.2 {
    private static final Identifier CHECKBOX_SELECTED_HIGHLIGHTED = Identifier.of("widget/checkbox_selected_highlighted");
    private static final Identifier CHECKBOX_SELECTED = Identifier.of("widget/checkbox_selected");
    private static final Identifier CHECKBOX_HIGHLIGHTED = Identifier.of("widget/checkbox_highlighted");
    private static final Identifier CHECKBOX = Identifier.of("widget/checkbox");

    private static final ButtonTextures BUTTON_TEXTURES = new ButtonTextures(
            Identifier.of("widget/button"),
            Identifier.of("widget/button_disabled"),
            Identifier.of("widget/button_highlighted")
    );
//? } else {
    /*private static final Identifier CHECKBOX_TEXTURE = Identifier.of("textures/gui/checkbox.png");
*///? }

//? if >=1.20 {
    @Nonnull public final DrawContext delegate;
//? } else {
    /*@Nonnull public final MatrixStack delegate;
*///? }

//? if >=1.21.6 {
    private int scissorDepth;

    public void translate(float x, float y, float z) {
        // GUI matrices are 2D in this version
        delegate.getMatrices().translate(x, y);
    }

    public void pushMatrix() {
        delegate.getMatrices().pushMatrix();
    }

    public void popMatrix() {
        delegate.getMatrices().popMatrix();
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
            @Nullable ScreenRect scissorArea,
            @Nullable ScreenRect bounds
    ) implements SimpleGuiElementRenderState {
        public QuadRenderState(
                RenderPipeline pipeline, TextureSetup textureSetup, /*? if >=1.21.11 {*/Matrix3x2fc/*? } else {*//*Matrix3x2f*//*? }*/ pose, GraphicsQuad<PosXY> quad,
                int color, @Nullable ScreenRect scissorArea
        ) {
            this(pipeline, textureSetup, pose, quad, color, scissorArea, createBounds(quad, pose, scissorArea));
        }

//? if >=1.21.9 {
        // Depth removed in 1.21.9
        public void setupVertices(VertexConsumer vertices) {
            vertices.vertex(pose(), quad.v0.x, quad.v0.y).color(color);
            vertices.vertex(pose(), quad.v1.x, quad.v1.y).color(color);
            vertices.vertex(pose(), quad.v2.x, quad.v2.y).color(color);
            vertices.vertex(pose(), quad.v3.x, quad.v3.y).color(color);
        }
//? } else {
        /*public void setupVertices(VertexConsumer vertices, float depth) {
            vertices.vertex(pose(), quad.v0.x, quad.v0.y, depth).color(color);
            vertices.vertex(pose(), quad.v1.x, quad.v1.y, depth).color(color);
            vertices.vertex(pose(), quad.v2.x, quad.v2.y, depth).color(color);
            vertices.vertex(pose(), quad.v3.x, quad.v3.y, depth).color(color);
        }
*///? }

        private static ScreenRect createBounds(GraphicsQuad<PosXY> quad, /*? if >=1.21.11 {*/Matrix3x2fc/*? } else {*//*Matrix3x2f*//*? }*/ pose, @Nullable ScreenRect scissorArea) {
            int l = MathHelper.floor(Math.min(Math.min(quad.v0.x, quad.v1.x), Math.min(quad.v2.x, quad.v3.x)));
            int t = MathHelper.floor(Math.min(Math.min(quad.v0.y, quad.v1.y), Math.min(quad.v2.y, quad.v3.y)));
            int r = MathHelper.ceil(Math.max(Math.max(quad.v0.x, quad.v1.x), Math.max(quad.v2.x, quad.v3.x)));
            int b = MathHelper.ceil(Math.max(Math.max(quad.v0.y, quad.v1.y), Math.max(quad.v2.y, quad.v3.y)));
            ScreenRect screenRect = new ScreenRect(l, t, r - l, b - t).transformEachVertex(pose);
            return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
        }
    }

    public void fillQuad(GraphicsQuad<PosXY> quad, int color, float z) {
        delegate.state.addSimpleElement(new QuadRenderState(
                RenderPipelines.GUI, TextureSetup.empty(), new Matrix3x2f(delegate.getMatrices()), quad, color,
                delegate.scissorStack.peekLast()));
    }
//? } else {
    /*public void translate(float x, float y, float z) {
        getPose().translate(x, y, z);
    }
    public void pushMatrix() {
        getPose().push();
    }
    public void popMatrix() {
        getPose().pop();
    }

    protected int[] getAbsoluteScissorDimension(int relX, int relY, int relWidth, int relHeight) {
        WindowDimension window = McConnector.client().getWindowSize();
        if (window.getScaledWidth() == 0 || window.getScaledHeight() == 0) { // Division by zero handling
            return new int[] { 0, 0, 0, 0 };
        }
        float scaleFactorX = window.getScaleFactorX();
        float scaleFactorY = window.getScaleFactorY();

        var matrix = getPose().peek().getPositionMatrix();
//? if >=1.19.3 {
        var start = new org.joml.Vector4f(relX, relY, 0, 1);
        var end = new org.joml.Vector4f(relX + relWidth, relY + relHeight, 0, 1);
        start = matrix.transform(start);
        end = matrix.transform(end);

        int scissorX = (int) (scaleFactorX * Math.min(start.x(), end.x()));
        int scissorY = (int) (window.getPixelHeight() - scaleFactorY * Math.max(start.y(), end.y()));
        int scissorWidth = (int) (scaleFactorX * Math.abs(start.x() - end.x()));
        int scissorHeight = (int) (scaleFactorY * Math.abs(start.y() - end.y()));
//? } else {
        /^var start = new net.minecraft.util.math.Vector4f(relX, relY, 0, 1);
        var end = new net.minecraft.util.math.Vector4f(relX + relWidth, relY + relHeight, 0, 1);
        start.transform(matrix);
        end.transform(matrix);

        int scissorX = (int) (scaleFactorX * Math.min(start.getX(), end.getX()));
        int scissorY = (int) (window.getPixelHeight() - scaleFactorY * Math.max(start.getY(), end.getY()));
        int scissorWidth = (int) (scaleFactorX * Math.abs(start.getX() - end.getX()));
        int scissorHeight = (int) (scaleFactorY * Math.abs(start.getY() - end.getY()));
^///? }
        return new int[] { scissorX, scissorY, scissorWidth, scissorHeight };
    }
    protected void glEnableScissor(int x, int y, int width, int height) {
//? if >=1.20 {
        delegate.draw();
//? }
        RenderSystem.enableScissor(x, y, width, height);
    }
    protected void glDisableScissor() {
//? if >=1.20 {
        delegate.draw();
//? }
        RenderSystem.disableScissor();
    }

    public void fillQuad(GraphicsQuad<PosXY> quad, int color, float z) {
        var matrix4f = getPose().peek().getPositionMatrix();
//? if >=1.20 {
        VertexConsumer vertexConsumer = delegate
//? if >=1.21.2 {
                .vertexConsumers // Must use AccessWidener
//? } else {
                /^.getVertexConsumers()
^///? }
                .getBuffer(RenderLayer.getGui());
        quad.forEach(v -> vertexConsumer.vertex(matrix4f, v.x, v.y, z).color(color));
//? } else if >=1.19.3 {
        /^BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.setShader(/^¹? if >=1.19.3 {¹^/GameRenderer::getPositionColorProgram/^¹? } else {¹^//^¹GameRenderer::getPositionColorShader¹^//^¹? }¹^/);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        quad.forEach(v -> bufferBuilder.vertex(matrix4f, v.x, v.y, z).color(color).next());
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
^///? } else {
        /^Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        quad.forEach(v -> bufferBuilder.vertex(matrix4f, v.x, v.y, z).color(color).next());
        tessellator.draw();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
^///? }
    }
*///? }

    public void drawButton(int x, int y, int width, int height, AbstractWidgetCopy.HoverState hoverState) {
//? if >=1.20.2 {
        boolean enabled = hoverState != AbstractWidgetCopy.HoverState.DISABLED;
        boolean focused = hoverState == AbstractWidgetCopy.HoverState.MOUSE_OVER;
        Identifier buttonTexture = BUTTON_TEXTURES.get(enabled, focused);

//? if >=1.21.2 {
        delegate.drawGuiTexture(
                /*? if >=1.21.6 {*/RenderPipelines.GUI_TEXTURED/*? } else {*//*RenderLayer::getGuiTextured*//*? }*/,
                buttonTexture, x, y, width, height);
//? } else {
        /*delegate.drawGuiTexture(buttonTexture, x, y, width, height);
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
        delegate.setShaderColor(1, 1, 1, 1);
        delegate.drawNineSlicedTexture(
                net.minecraft.client.gui.widget.ClickableWidget.WIDGETS_TEXTURE, x, y, width, height, 20, 4, 200, 20,
                0, 46 + i * 20);
//? } else if >=1.19.4 {
        /^RenderSystem.setShaderTexture(0, net.minecraft.client.gui.widget.ClickableWidget.WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        DrawableHelper.drawNineSlicedTexture(delegate, x, y, width, height, 20, 4, 200, 20, 0, 46 + i * 20);
^///? } else {
        /^RenderSystem.setShader(/^¹? if >=1.19.3 {¹^/GameRenderer::getPositionTexProgram/^¹? } else {¹^//^¹GameRenderer::getPositionTexShader¹^//^¹? }¹^/);
        RenderSystem.setShaderTexture(0, net.minecraft.client.gui.widget.ClickableWidget.WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        DrawableHelper.drawTexture(delegate, x, y, 0, 0, 46 + i * 20, width / 2, height, 256, 256);
        DrawableHelper.drawTexture(delegate, x + width / 2, y, 0, 200 - (float) width / 2, 46 + i * 20, width / 2, height, 256, 256);
^///? }
*///? }
    }

    public void drawCheckBox(int x, int y, int width, int height, boolean focused, boolean checked) {
//? if >=1.20.2 {
        Identifier identifier = checked
                ? (focused ? CHECKBOX_SELECTED_HIGHLIGHTED : CHECKBOX_SELECTED)
                : (focused ? CHECKBOX_HIGHLIGHTED : CHECKBOX);

//? if >=1.21.2 {
        delegate.drawGuiTexture(
                /*? if >=1.21.6 {*/RenderPipelines.GUI_TEXTURED/*? } else {*//*RenderLayer::getGuiTextured*//*? }*/,
                identifier, x, y, width, height, ColorHelper.getWhite(1));
//? } else {
        /*RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        delegate.setShaderColor(1, 1, 1, 1);
        delegate.drawGuiTexture(identifier, x, y, width, height);
*///? }
//? } else {
        /*float size = 20 / 64f;
        float u0 = focused ? size : 0, v0 = checked ? size : 0;
        float u1 = u0 + size, v1 = v0 + size;
//? if >=1.20 {
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        delegate.setShaderColor(1, 1, 1, 1);
        delegate.drawTexturedQuad(CHECKBOX_TEXTURE, x, x+width, y, y+height, 0, u0, u1, v0, v1);
//? } else {
        /^var matrix4f = delegate.peek().getPositionMatrix();
        RenderSystem.setShaderTexture(0, CHECKBOX_TEXTURE);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        DrawableHelper.drawTexturedQuad(matrix4f, x, x+width, y, y+height, 0, u0, u1, v0, v1);
^///? }
*///? }
    }

    public void drawTextHighlight(int startX, int startY, int endX, int endY) {
//? if >=1.20 {
        delegate.fill(startX, startY, endX, endY, 0xff0000ff);
//? } else if >=1.19 {
        /*RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(com.mojang.blaze3d.platform.GlStateManager.LogicOp.OR_REVERSE);
        DrawableHelper.fill(delegate, startX, startY, endX, endY, 0xff0000ff);
        RenderSystem.disableColorLogicOp();
*///? } else {
        /*Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(0.0F, 0.0F, 1.0F, 1.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(com.mojang.blaze3d.platform.GlStateManager.LogicOp.OR_REVERSE);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        bufferBuilder.vertex(startX, endY, 0).next();
        bufferBuilder.vertex(endX, endY, 0).next();
        bufferBuilder.vertex(endX, startY, 0).next();
        bufferBuilder.vertex(startX, startY, 0).next();
        tessellator.draw();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
*///? }
    }

//? if >=1.21.6 {
    public void drawImage(ResourceLocationWrapper res, int x, int y, int w, int h,
                          float u1, float u2, float v1, float v2) {
        Identifier texture = ((ResourceLocationWrapperImpl) res).delegate();
        delegate.drawTexturedQuad(texture, x, y, x+w, y+h, u1, u2, v1, v2);
    }

    public void drawWholeNativeImage(@Nonnull NativeTextureWrapper allocatedTextureObject, int x, int y, int w, int h) {
        Identifier texture = ((NativeTextureWrapperImpl) allocatedTextureObject).delegate;
        delegate.drawTexturedQuad(texture, x, y, x+w, y+h, 0, 1, 0, 1);
    }
//? } else if >=1.21.2 {
    /*public void drawImage(ResourceLocationWrapper res, int x, int y, int w, int h, float u1, float u2, float v1, float v2) {
        Identifier texture = ((ResourceLocationWrapperImpl) res).delegate();
        delegate.drawTexturedQuad(RenderLayer::getGuiTextured, texture, x, x+w, y, y+h, u1, u2, v1, v2, ColorHelper.getWhite(1));
    }

    public void drawWholeNativeImage(@Nonnull NativeTextureWrapper allocatedTextureObject, int x, int y, int w, int h) {
        Identifier texture = ((NativeTextureWrapperImpl) allocatedTextureObject).delegate;
        delegate.drawTexturedQuad(RenderLayer::getGuiTextured, texture, x, x+w, y, y+h, 0, 1, 0, 1, ColorHelper.getWhite(1));
    }
*///? } else if >=1.20 {
    /*public void drawImage(ResourceLocationWrapper res, int x, int y, int w, int h, float u1, float u2, float v1, float v2) {
        Identifier texture = ((ResourceLocationWrapperImpl) res).delegate();
        delegate.drawTexturedQuad(texture, x, x+w, y, y+h, 0, u1, u2, v1, v2);
    }

    public void drawWholeNativeImage(@Nonnull NativeTextureWrapper allocatedTextureObject, int x, int y, int w, int h) {
        Identifier texture = ((NativeTextureWrapperImpl) allocatedTextureObject).delegate;
        delegate.drawTexturedQuad(texture, x, x+w, y, y+h, 0, 0, 1, 0, 1);
    }*/
//? } else {
    /*public void drawImage(ResourceLocationWrapper res, int x, int y, int w, int h, float u1, float u2, float v1, float v2) {
        var matrix4f = delegate.peek().getPositionMatrix();
        RenderSystem.setShaderTexture(0, ((ResourceLocationWrapperImpl) res).delegate());
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
//? if <1.19 {
        /^RenderSystem.enableDepthTest();
^///? }
        DrawableHelper.drawTexturedQuad(matrix4f, x, x+w, y, y+h, 0, u1, u2, v1, v2);
    }

    public void drawWholeNativeImage(@Nonnull NativeTextureWrapper allocatedTextureObject, int x, int y, int w, int h) {
        var matrix4f = delegate.peek().getPositionMatrix();
        RenderSystem.setShaderTexture(0, ((NativeTextureWrapperImpl) allocatedTextureObject).delegate);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
//? if <1.19 {
        /^RenderSystem.enableDepthTest();
^///? }
        RenderSystem.enableDepthTest();
        DrawableHelper.drawTexturedQuad(matrix4f, x, x+w, y, y+h, 0, 0, 1, 0, 1);
    }
*///? }

    public void drawHoverEvent(StyleWrapper styleWrapper, int x, int y) {
//? if >=1.20 {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        Style style = ((StyleWrapperImpl) styleWrapper).delegate();
        delegate.drawHoverEvent(textRenderer, style, x, y);
//? } else {
        /*Screen currentScreen = MinecraftClient.getInstance().currentScreen;
        if (!(currentScreen instanceof AbstractGuiScreenImpl guiScreen)) return;

        Style style = ((StyleWrapperImpl) styleWrapper).delegate();
        guiScreen.renderTextHoverEffect(delegate, style, x, y);
*///? }
    }

    public void drawTextWithShadow(FontWrapper fontWrapper, String string, float x, float y, int color) {
        TextRenderer textRenderer = ((FontWrapperImpl) fontWrapper).delegate;
//? if >=1.20 {
        delegate.drawTextWithShadow(textRenderer, string, (int) x, (int) y, color);
//? } else {
        /*textRenderer.drawWithShadow(delegate, string, x, y, color);
*///? }
    }

//? if >=1.21.6 {
    private org.joml.Matrix3x2fStack getPose() { return delegate.getMatrices(); }
//? } else if >=1.20 {
    /*private MatrixStack getPose() { return delegate.getMatrices(); }
*///? } else {
    /*private MatrixStack getPose() { return delegate; }
*///? }
}
