package com.mndk.bteterrarenderer.mcconnector.client.text;

import com.mndk.bteterrarenderer.mcconnector.client.gui.text.TextFormatCopy;
import com.mndk.bteterrarenderer.mcconnector.client.gui.text.TextManager;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;

import javax.annotation.Nonnull;

public class TextManagerImpl implements TextManager {

    @Override
    public TextWrapper fromJson(@Nonnull String json) {
//? if >=1.20.5 {
        try {
            // 1.21+: decode Text via codecs instead of Text.Serialization
            var element = com.google.gson.JsonParser.parseString(json);

            var result = ComponentSerialization.CODEC
                    .parse(net.minecraft.core.RegistryAccess.EMPTY.createSerializationContext(JsonOps.INSTANCE), element)
                    .result()
                    .orElse(null);

            return result != null ? new TextWrapperImpl(result) : null;
        } catch (Throwable t) {
            return null;
        }
//? } else {
        /*Component text = Component.Serializer.fromJson(json);
        return text != null ? new TextWrapperImpl(text) : null;
*///? }
    }

    @Override
    public TextWrapper fromString(@Nonnull String text) {
        return new TextWrapperImpl(/*? if >=1.19 {*/Component.literal(text)/*?} else {*//*new TextComponent(text)*//*? }*/);
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
        Minecraft client = Minecraft.getInstance();
        Screen currentScreen = client.screen;
        if (currentScreen == null) return false;

        Style style = ((StyleWrapperImpl) styleWrapper).delegate();

        ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent == null) return false;

//? if >=1.21.6 {
        Screen.defaultHandleGameClickEvent(clickEvent, client, currentScreen);
        return true;
//? } else {
        /*return currentScreen.handleComponentClicked(style);
*///? }
    }
}
