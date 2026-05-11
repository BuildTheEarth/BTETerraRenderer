package com.mndk.bteterrarenderer.mcconnector.client;

import com.mndk.bteterrarenderer.mcconnector.CommonMinecraftManager;
import com.mndk.bteterrarenderer.mcconnector.client.graphics.BufferBuildersManager;
import com.mndk.bteterrarenderer.mcconnector.client.graphics.TextureManager;
import com.mndk.bteterrarenderer.mcconnector.client.gui.screen.AbstractGuiScreenCopy;
import com.mndk.bteterrarenderer.mcconnector.client.gui.screen.NativeGuiScreenWrapper;
import com.mndk.bteterrarenderer.mcconnector.client.gui.text.TextManager;
import com.mndk.bteterrarenderer.mcconnector.client.input.GameInputManager;
import com.mndk.bteterrarenderer.mcconnector.client.text.FontWrapper;
import com.mndk.bteterrarenderer.mcconnector.client.text.TextWrapper;
import com.mndk.bteterrarenderer.mcconnector.i18n.I18nManager;

import javax.annotation.Nullable;
import java.io.File;
import java.net.URI;
import java.nio.file.Path;

public abstract class ClientMinecraftManager extends CommonMinecraftManager {

    public final TextureManager textureManager;
    public final GameInputManager inputManager;
    public final TextManager textManager;
    public final BufferBuildersManager bufferBuildersManager;

    public ClientMinecraftManager(GameInputManager inputManager, TextureManager textureManager,
                                  I18nManager i18nManager, TextManager textManager,
                                  BufferBuildersManager bufferBuildersManager) {
        super(i18nManager);
        this.inputManager = inputManager;
        this.textureManager = textureManager;
        this.textManager = textManager;
        this.bufferBuildersManager = bufferBuildersManager;
    }

    public abstract WindowDimension getWindowSize();
    public abstract FontWrapper getDefaultFont();

    public abstract void displayGuiScreen(@Nullable AbstractGuiScreenCopy screen);
    public abstract NativeGuiScreenWrapper newChatScreen(String initialText);

    public abstract boolean isOnMac();
    public abstract double getFovDegrees();
    public abstract double getPlayerRotationYaw();
    public abstract double getPlayerRotationPitch();
    public abstract void playClickSound();

    public abstract void sendTextComponentToChat(TextWrapper textComponent);
    public void sendFormattedStringToChat(String formattedString) {
        this.sendTextComponentToChat(this.textManager.fromString(formattedString));
    }

    public abstract void openUri(URI uri);

    public final void openFile(File file) {
        this.openUri(file.toURI());
    }

    public final void openPath(Path path) {
        this.openUri(path.toUri());
    }
}
