package com.mndk.bteterrarenderer.mcconnector.client.text;

import com.mndk.bteterrarenderer.mcconnector.client.gui.GuiDrawContextWrapper;
import com.mndk.bteterrarenderer.mcconnector.client.gui.GuiDrawContextWrapperImpl;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.ComponentCollector;
import net.minecraft.client.gui.*;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class OrderedTextWrapperImpl extends AbstractTextWrapper {

    @Nonnull public final FormattedCharSequence delegate;

    protected List<? extends TextWrapper> splitByWidthUnsafe(FontWrapper fontWrapper, int wrapWidth) {
        Font textRenderer = ((FontWrapperImpl) fontWrapper).delegate;
        FormattedText text = this.toStringVisitable();
        return textRenderer.split(text, wrapWidth).stream().map(OrderedTextWrapperImpl::new).toList();
    }

    public int getWidth(FontWrapper fontWrapper) {
        Font textRenderer = ((FontWrapperImpl) fontWrapper).delegate;
        return textRenderer.width(delegate);
    }

    @Nullable
    public StyleWrapper getStyleComponentFromLine(FontWrapper fontWrapper, int mouseXFromLeft) {
        Font textRenderer = ((FontWrapperImpl) fontWrapper).delegate;
        Style style = findStyleAtX(textRenderer, delegate, mouseXFromLeft);
        return style != null ? new StyleWrapperImpl(style) : null;
    }

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

    private FormattedText toStringVisitable() {
        ComponentCollector textCollector = new ComponentCollector();
        AtomicReference<Style> lastStyle = new AtomicReference<>();
        AtomicReference<String> lastString = new AtomicReference<>("");
        delegate.accept((index, style, codePoint) -> {
            String append = new String(Character.toChars(codePoint));
            if (Objects.equals(style, lastStyle.get())) {
                lastString.set(lastString.get() + append);
                return true;
            }
            if (!lastString.get().isEmpty()) {
                textCollector.append(FormattedText.of(lastString.get(), lastStyle.get()));
            }
            lastStyle.set(style);
            lastString.set(append);
            return true;
        });
        if (!lastString.get().isEmpty()) {
            textCollector.append(FormattedText.of(lastString.get(), lastStyle.get()));
        }
        return textCollector.getResultOrEmpty();
    }
}
