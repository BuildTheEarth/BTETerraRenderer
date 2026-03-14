package com.mndk.bteterrarenderer.mcconnector.client.text;

import com.mndk.bteterrarenderer.mcconnector.client.gui.GuiDrawContextWrapper;
import com.mndk.bteterrarenderer.mcconnector.client.gui.GuiDrawContextWrapperImpl;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@RequiredArgsConstructor
public class TextWrapperImpl extends AbstractTextWrapper {

    @Nonnull public final Component delegate;

    @Override
    protected List<? extends TextWrapper> splitByWidthUnsafe(FontWrapper fontWrapper, int wrapWidth) {
        Font textRenderer = ((FontWrapperImpl) fontWrapper).delegate;
        return textRenderer.split(delegate, wrapWidth).stream().map(OrderedTextWrapperImpl::new).toList();
    }

    @Override
    public int getWidth(FontWrapper fontWrapper) {
        Font textRenderer = ((FontWrapperImpl) fontWrapper).delegate;
        return textRenderer.width(delegate);
    }

    @Override
    @Nullable
    public StyleWrapper getStyleComponentFromLine(FontWrapper fontWrapper, int mouseXFromLeft) {
        Font textRenderer = ((FontWrapperImpl) fontWrapper).delegate;
        Style style = findStyleAtX(textRenderer, delegate.getVisualOrderText(), mouseXFromLeft);
        return style != null ? new StyleWrapperImpl(style) : null;
    }

    @Override
    public void drawWithShadow(FontWrapper fontWrapper, GuiDrawContextWrapper context, float x, float y, int color) {
        Font textRenderer = ((FontWrapperImpl) fontWrapper).delegate;
        var drawContext = ((GuiDrawContextWrapperImpl) context).delegate;
//? if >=26.1 {
        drawContext.text(textRenderer, delegate, (int) x, (int) y, color);
//? } else if >=1.20 {
        /*drawContext.drawString(textRenderer, delegate, (int) x, (int) y, color);
*///? } else {
        /*textRenderer.drawShadow(drawContext, delegate, (int) x, (int) y, color);
*///? }
    }

    @Nullable
    private static Style findStyleAtX(Font textRenderer, FormattedCharSequence text, int mouseXFromLeft) {
        int[] x = { 0 };
        Style[] result = { null };
        text.accept((index, style, codePoint) -> {
            int width = textRenderer.width(FormattedCharSequence.codepoint(codePoint, style));
            if (mouseXFromLeft < x[0] + width) {
                result[0] = style;
                return false;
            }
            x[0] += width;
            return true;
        });
        return result[0];
    }
}

