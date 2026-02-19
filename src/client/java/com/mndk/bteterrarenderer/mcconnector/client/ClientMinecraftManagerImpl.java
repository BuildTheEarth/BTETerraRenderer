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
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

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
        return new WindowDimensionImpl(MinecraftClient.getInstance().getWindow());
    }

    @Override
    public FontWrapper getDefaultFont() {
        return new FontWrapperImpl(MinecraftClient.getInstance().textRenderer);
    }

    @Override
    public ResourceLocationWrapper newResourceLocation(String modId, String location) {
        return new ResourceLocationWrapperImpl(Identifier.of(modId, location));
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
        MinecraftClient.getInstance().setScreen(screen == null ? null : new AbstractGuiScreenImpl(screen));
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
        return MinecraftClient.getInstance().options.getFov().getValue();
//? } else {
        /*return MinecraftClient.getInstance().options.fov;
*///? }
    }

    @Override
    public double getPlayerRotationYaw() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        return player != null ? player.getYaw() : 0;
    }

    @Override
    public double getPlayerRotationPitch() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        return player != null ? player.getPitch() : 0;
    }

    @Override
    public void sendTextComponentToChat(TextWrapper textComponent) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        player.sendMessage(((TextWrapperImpl) textComponent).delegate, false);
    }

    @Override
    public void playClickSound() {
//? if >=1.21.11 {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK, 1.0F));
//? } else {
        /*MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
*///? }
    }
}
