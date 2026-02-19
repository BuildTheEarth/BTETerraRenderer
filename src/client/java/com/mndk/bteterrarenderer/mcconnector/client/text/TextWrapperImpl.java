package com.mndk.bteterrarenderer.mcconnector.client.text;

import com.mndk.bteterrarenderer.mcconnector.client.gui.GuiDrawContextWrapper;
import com.mndk.bteterrarenderer.mcconnector.client.gui.GuiDrawContextWrapperImpl;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@RequiredArgsConstructor
public class TextWrapperImpl extends AbstractTextWrapper {

    @Nonnull public final Text delegate;

    @Override
    protected List<? extends TextWrapper> splitByWidthUnsafe(FontWrapper fontWrapper, int wrapWidth) {
        TextRenderer textRenderer = ((FontWrapperImpl) fontWrapper).delegate;
        return textRenderer.wrapLines(delegate, wrapWidth).stream().map(OrderedTextWrapperImpl::new).toList();
    }

    @Override
    public int getWidth(FontWrapper fontWrapper) {
        TextRenderer textRenderer = ((FontWrapperImpl) fontWrapper).delegate;
        return textRenderer.getWidth(delegate);
    }

    @Override
    @Nullable
    public StyleWrapper getStyleComponentFromLine(FontWrapper fontWrapper, int mouseXFromLeft) {
        TextRenderer textRenderer = ((FontWrapperImpl) fontWrapper).delegate;
        Style style = findStyleAtX(textRenderer, delegate.asOrderedText(), mouseXFromLeft);
        return style != null ? new StyleWrapperImpl(style) : null;
    }

    @Override
    public void drawWithShadow(FontWrapper fontWrapper, GuiDrawContextWrapper context, float x, float y, int color) {
        TextRenderer textRenderer = ((FontWrapperImpl) fontWrapper).delegate;
//? if >=1.20 {
        DrawContext drawContext = ((GuiDrawContextWrapperImpl) context).delegate;
        drawContext.drawTextWithShadow(textRenderer, delegate, (int) x, (int) y, color);
//? } else {
        /*MatrixStack matrixStack = ((GuiDrawContextWrapperImpl) context).delegate;
        textRenderer.drawWithShadow(matrixStack, delegate, (int) x, (int) y, color);
*///? }
    }

    @Nullable
    private static Style findStyleAtX(TextRenderer textRenderer, OrderedText text, int mouseXFromLeft) {
        int[] x = { 0 };
        Style[] result = { null };
        text.accept((index, style, codePoint) -> {
            int width = textRenderer.getWidth(OrderedText.styled(codePoint, style));
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

