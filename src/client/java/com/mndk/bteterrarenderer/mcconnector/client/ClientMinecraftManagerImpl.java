package com.mndk.bteterrarenderer.mcconnector.client;

import com.mndk.bteterrarenderer.mcconnector.client.graphics.BufferBuildersManagerImpl;
import com.mndk.bteterrarenderer.mcconnector.client.graphics.TextureManagerImpl;
import com.mndk.bteterrarenderer.mcconnector.client.gui.screen.AbstractGuiScreenCopy;
import com.mndk.bteterrarenderer.mcconnector.client.gui.screen.AbstractGuiScreenImpl;
import com.mndk.bteterrarenderer.mcconnector.client.gui.screen.NativeGuiScreenWrapper;
import com.mndk.bteterrarenderer.mcconnector.client.gui.screen.NativeGuiScreenWrapperImpl;
import com.mndk.bteterrarenderer.mcconnector.client.i18n.ClientI18nManagerImpl;
import com.mndk.bteterrarenderer.mcconnector.client.input.GameInputManagerImpl;
import com.mndk.bteterrarenderer.mcconnector.client.text.FontWrapper;
import com.mndk.bteterrarenderer.mcconnector.client.text.FontWrapperImpl;
import com.mndk.bteterrarenderer.mcconnector.client.text.TextWrapper;
import com.mndk.bteterrarenderer.mcconnector.client.text.TextWrapperImpl; // <-- if this line errors, see note below
import com.mndk.bteterrarenderer.mcconnector.util.ResourceLocationWrapper;
import com.mndk.bteterrarenderer.mcconnector.util.ResourceLocationWrapperImpl;
import com.mndk.bteterrarenderer.mod.util.IdUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

import javax.annotation.Nullable;
import java.io.File;

public class ClientMinecraftManagerImpl extends ClientMinecraftManager {

    public ClientMinecraftManagerImpl() {
        super(
                new GameInputManagerImpl(),
                new TextureManagerImpl(),
                new ClientI18nManagerImpl(),
                new com.mndk.bteterrarenderer.mcconnector.client.text.TextManagerImpl(),
                new BufferBuildersManagerImpl()
	);
    }

    @Override
    public WindowDimension getWindowSize() {
        return new WindowDimensionImpl(Minecraft.getInstance().getWindow());
    }

    @Override
    public FontWrapper getDefaultFont() {
        return new FontWrapperImpl(Minecraft.getInstance().font);
    }

    @Override
    public ResourceLocationWrapper newResourceLocation(String modId, String location) {
        return new ResourceLocationWrapperImpl(IdUtil.fromNamespaceAndPath(modId, location));
    }

    @Override
    public File getGameDirectory() {
        return FabricLoader.getInstance().getGameDir().toFile();
    }

    @Override
    public File getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir().toFile();
    }

    @Override
    public void displayGuiScreen(@Nullable AbstractGuiScreenCopy screen) {
//? if >=26.2 {
        /*Minecraft.getInstance().gui.setScreen(screen == null ? null : new AbstractGuiScreenImpl(screen));
*///? } else {
        Minecraft.getInstance().setScreen(screen == null ? null : new AbstractGuiScreenImpl(screen));
//? }
    }

    @Override
    public NativeGuiScreenWrapper newChatScreen(String initialText) {
        // 1.21.9+ use (String, boolean)
        return new NativeGuiScreenWrapperImpl(new ChatScreen(initialText/*? if >=1.21.9 {*/, false/*? }*/));
    }

    @Override
    public boolean isOnMac() {
        String os = System.getProperty("os.name", "").toLowerCase();
        return os.contains("mac");
    }

    @Override
    public double getFovDegrees() {
//? if >=1.19 {
        return Minecraft.getInstance().options.fov().get();
//? } else {
        /*return Minecraft.getInstance().options.fov;
*///? }
    }

    @Override
    public double getPlayerRotationYaw() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null ? player.getYRot() : 0;
    }

    @Override
    public double getPlayerRotationPitch() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null ? player.getXRot() : 0;
    }

    @Override
    public void sendTextComponentToChat(TextWrapper textComponent) {
//? if >=26.2 {
        /*Minecraft.getInstance().gui.chatListener().handleSystemMessage(((TextWrapperImpl) textComponent).delegate, false);
*///? } else if >=26.1 {
        Minecraft.getInstance().getChatListener().handleSystemMessage(((TextWrapperImpl) textComponent).delegate, false); // Present since 1.20
//? } else {
        /*LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        player.displayClientMessage(((TextWrapperImpl) textComponent).delegate, false);
*///? }
    }

    @Override
    public void playClickSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }
}
