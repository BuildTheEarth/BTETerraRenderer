package com.mndk.bteterrarenderer.mcconnector.client.input;

import com.mndk.bteterrarenderer.mod.util.IdUtil;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources./*? if >=1.21.11 {*/Identifier/*? } else {*//*ResourceLocation*//*? }*/;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameInputManagerImpl implements GameInputManager {

//? if >=1.21.9 {
    private static final Map</*? if >=1.21.11 {*/Identifier/*? } else {*//*ResourceLocation*//*? }*/, KeyMapping.Category> CATEGORIES = new HashMap<>();
    private static List<KeyMapping.Category> VANILLA_CATEGORIES;
//? }

    @Override
    public boolean isKeyDown(InputKey key) {
        return InputConstants.isKeyDown(
                Minecraft.getInstance().getWindow()/*? if <1.21.9 {*//*.getWindow()*//*? }*/,
                key.glfwKeyCode);
    }

    @Override
    public IKeyBinding registerInternal(String description, InputKey key, String category) {
        // category must be a KeyMapping.Category in 1.21.11
        var catId = category.contains(":")
                ? IdUtil.parse(category)
                : IdUtil.fromNamespaceAndPath("bteterrarenderer", category);

//? if >=1.21.9 {
        KeyMapping.Category cat = CATEGORIES.computeIfAbsent(catId, GameInputManagerImpl::getOrCreateCategory);
        KeyMapping keyBinding = new KeyMapping(description, key.glfwKeyCode, cat);
//? } else {
        /*KeyMapping keyBinding = new KeyMapping(description, key.glfwKeyCode, category);
*///? }

//? if >=26.1 {
        net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper.registerKeyMapping(keyBinding);
//? } else {
        /*net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(keyBinding);
*///? }
        return keyBinding::consumeClick;
    }

    @Override
    public String getClipboardContent() {
        return Minecraft.getInstance().keyboardHandler.getClipboard();
    }

    @Override
    public void setClipboardContent(String content) {
        Minecraft.getInstance().keyboardHandler.setClipboard(content);
    }

//? if >=1.21.9 {
    private static KeyMapping.Category getOrCreateCategory(/*? if >=1.21.11 {*/Identifier/*? } else {*//*ResourceLocation*//*? }*/ id) {
        KeyMapping.Category existing = findExistingCategory(id);
        return existing != null ? existing : KeyMapping.Category.register(id);
    }

    private static KeyMapping.Category findExistingCategory(/*? if >=1.21.11 {*/Identifier/*? } else {*//*ResourceLocation*//*? }*/ id) {
        List<KeyMapping.Category> categories = VANILLA_CATEGORIES;
        if (categories == null) {
            try {
                Field field = KeyMapping.Category.class.getDeclaredField("CATEGORIES");
                field.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<KeyMapping.Category> value = (List<KeyMapping.Category>) field.get(null);
                VANILLA_CATEGORIES = value;
                categories = value;
            } catch (Throwable ignored) {
                return null;
            }
        }
        for (KeyMapping.Category cat : categories) {
            if (id.equals(cat.id())) return cat;
        }
        return null;
    }
//? }
}
