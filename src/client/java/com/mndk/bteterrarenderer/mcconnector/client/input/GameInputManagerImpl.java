package com.mndk.bteterrarenderer.mcconnector.client.input;

import com.mndk.bteterrarenderer.mcconnector.util.ResourceLocationWrapper;
import com.mndk.bteterrarenderer.mcconnector.util.ResourceLocationWrapperImpl;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class GameInputManagerImpl implements GameInputManager {

    @Override
    public boolean isKeyDown(InputKey key) {
//? if >=26.3-alpha.4 {
        // 26.3-snapshot-4 switched from GLFW to SDL
        return InputConstants.isKeyDown(key.sdlKeyCode);
//? } else if >=1.21.9 {
        /*return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), key.glfwKeyCode);
*///? } else {
        /*return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key.glfwKeyCode);
*///? }
    }

    @Override
    public IKeyBinding registerInternal(String locKey, InputKey key, IKeyBindingCategory category) {
        KeyMapping keyBinding = new KeyMapping(locKey, key.glfwKeyCode, ((KeyBindingCategoryImpl) category).delegate());
//? if >=26.1 {
        net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper.registerKeyMapping(keyBinding);
//? } else {
        /*net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(keyBinding);
*///? }
        return keyBinding::consumeClick;
    }

    @Override
    public IKeyBindingCategory registerCategory(ResourceLocationWrapper identifier) {
        var identifierImpl = ((ResourceLocationWrapperImpl) identifier).delegate();
//? if >=1.21.9 {
        return new KeyBindingCategoryImpl(KeyMapping.Category.register(identifierImpl));
//? } else {
        /*return new KeyBindingCategoryImpl("key.category." + identifierImpl.getNamespace() + "." + identifierImpl.getPath());
*///? }
    }

    @Override
    public String getClipboardContent() {
        return Minecraft.getInstance().keyboardHandler.getClipboard();
    }

    @Override
    public void setClipboardContent(String content) {
        Minecraft.getInstance().keyboardHandler.setClipboard(content);
    }
}
