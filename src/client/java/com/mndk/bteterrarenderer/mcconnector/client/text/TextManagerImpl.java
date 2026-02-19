package com.mndk.bteterrarenderer.mcconnector.client.text;

import com.mndk.bteterrarenderer.mcconnector.client.gui.text.TextFormatCopy;
import com.mndk.bteterrarenderer.mcconnector.client.gui.text.TextManager;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.*;

import javax.annotation.Nonnull;

public class TextManagerImpl implements TextManager {

    @Override
    public TextWrapper fromJson(@Nonnull String json) {
//? if >=1.20.5 {
        try {
            // 1.21+: decode Text via codecs instead of Text.Serialization
            var element = com.google.gson.JsonParser.parseString(json);

            var result = TextCodecs.CODEC
                    .parse(net.minecraft.registry.DynamicRegistryManager.EMPTY.getOps(JsonOps.INSTANCE), element)
                    .result()
                    .orElse(null);

            return result != null ? new TextWrapperImpl(result) : null;
        } catch (Throwable t) {
            return null;
        }
//? } else if >=1.20.3 {
        /*Text text = Text.Serialization.fromJson(json);
        return text != null ? new TextWrapperImpl(text) : null;
*///? } else {
        /*Text text = Text.Serializer.fromJson(json);
        return text != null ? new TextWrapperImpl(text) : null;
*///? }
    }

    @Override
    public TextWrapper fromString(@Nonnull String text) {
        return new TextWrapperImpl(/*? if >=1.19 {*/Text.literal(text)/*?} else {*//*new LiteralText(text)*//*? }*/);
    }

    @Override
    public StyleWrapper emptyStyle() {
        return new StyleWrapperImpl(Style.EMPTY);
    }

    @Override
    public StyleWrapper styleWithColor(StyleWrapper styleWrapper, TextFormatCopy textColor) {
        Style style = ((StyleWrapperImpl) styleWrapper).delegate();
        return new StyleWrapperImpl(style.withColor(textColor.getColorIndex()));
    }

    @Override
    public boolean handleClick(@Nonnull StyleWrapper styleWrapper) {
        MinecraftClient client = MinecraftClient.getInstance();
        Screen currentScreen = client.currentScreen;
        if (currentScreen == null) return false;

        Style style = ((StyleWrapperImpl) styleWrapper).delegate();

        ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent == null) return false;

//? if >=1.21.6 {
        Screen.handleClickEvent(clickEvent, client, currentScreen);
        return true;
//? } else {
        /*return currentScreen.handleTextClick(style);
*///? }
    }
}
