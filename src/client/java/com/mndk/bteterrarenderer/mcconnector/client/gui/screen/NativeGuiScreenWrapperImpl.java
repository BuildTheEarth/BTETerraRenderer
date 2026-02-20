package com.mndk.bteterrarenderer.mcconnector.client.gui.screen;

import com.mndk.bteterrarenderer.mcconnector.client.gui.GuiDrawContextWrapper;
import com.mndk.bteterrarenderer.mcconnector.client.gui.GuiDrawContextWrapperImpl;
import com.mndk.bteterrarenderer.mcconnector.client.input.InputKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.Screen;
//? if >=1.21.7 {
import net.minecraft.client.input.*;
//? }

import javax.annotation.Nonnull;

public record NativeGuiScreenWrapperImpl(@Nonnull Screen delegate) implements NativeGuiScreenWrapper {

    @Override
    public void onDisplayed() {}

    /**
     * 1.21.11 Screen.init is init(int,int)
     */
    @Override
    public void initGui(int width, int height) {
        delegate.init(/*? if <1.21.11 {*//*Minecraft.getInstance(),*//*? }*/width, height);
    }

    /**
     * 1.21.11: Screen.resize is now resize(int,int)
     */
    @Override
    public void setScreenSize(int width, int height) {
        delegate.resize(/*? if <1.21.11 {*//*Minecraft.getInstance(),*//*? }*/width, height);
    }

    @Override
    public void tick() {
        delegate.tick();
    }

    @Override
    public void drawScreen(@Nonnull GuiDrawContextWrapper context, int mouseX, int mouseY, float partialTicks) {
        delegate.render(((GuiDrawContextWrapperImpl) context).delegate, mouseX, mouseY, partialTicks);
    }

    /**
     * 1.21.9: mouseClicked takes (Click, boolean)
     */
    @Override
    public boolean mousePressed(double mouseX, double mouseY, int mouseButton) {
//? if >=1.21.9 {
        MouseButtonEvent click = makeClick(mouseX, mouseY, mouseButton);
        return delegate.mouseClicked(click, false);
//? } else {
        /*return delegate.mouseClicked(mouseX, mouseY, mouseButton);
*///? }
    }

    /**
     * 1.21.9: mouseReleased takes (Click)
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
//? if >=1.21.9 {
        MouseButtonEvent click = makeClick(mouseX, mouseY, mouseButton);
        return delegate.mouseReleased(click);
//? } else {
        /*return delegate.mouseReleased(mouseX, mouseY, mouseButton);
*///? }
    }

    /**
     * 1.21.9: mouseDragged takes (Click, double, double)
     * where the doubles are deltas
     */
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double pMouseX, double pMouseY) {
//? if >=1.21.9 {
        MouseButtonEvent click = makeClick(mouseX, mouseY, mouseButton);
        double deltaX = mouseX - pMouseX;
        double deltaY = mouseY - pMouseY;

        return delegate.mouseDragged(click, deltaX, deltaY);
//? } else {
        /*return delegate.mouseDragged(mouseX, mouseY, mouseButton, mouseX - pMouseX, mouseY - pMouseY);
*///? }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
//? if >=1.20.2 {
        // Screen still has mouseScrolled(double,double,double,double) in 1.21.9
        return delegate.mouseScrolled(mouseX, mouseY, 0, scrollAmount);
//? } else {
        /*return delegate.mouseScrolled(mouseX, mouseY, scrollAmount);
*///? }
    }

    /**
     * 1.21.9: charTyped takes (CharInput)
     */
    @Override
    public boolean charTyped(char typedChar, int keyCode) {
//? if >=1.21.9 {
        CharacterEvent charInput = new CharacterEvent(typedChar, keyCode);
        return delegate.charTyped(charInput);
//? } else {
        /*return delegate.charTyped(typedChar, keyCode);
*///? }
    }

    /**
     * 1.21.9: keyPressed takes (KeyInput)
     */
    @Override
    public boolean keyPressed(InputKey key, int scanCode, int modifiers) {
//? if >=1.21.9 {
        KeyEvent keyInput = new KeyEvent(key.glfwKeyCode, scanCode, modifiers);
        return delegate.keyPressed(keyInput);
//? } else {
        /*return delegate.keyPressed(key.glfwKeyCode, scanCode, modifiers);
*///? }
    }

    @Override
    public void onRemoved() {
        delegate.removed();
    }

    @Override
    public boolean doesScreenPauseGame() {
        return delegate./*? if >=1.18.1 {*/isPauseScreen/*? } else {*//*isPauseScreen*//*? }*/();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return delegate.shouldCloseOnEsc();
    }

    @Override
    public boolean alsoListensForKeyPress() {
        return true;
    }

//? if >=1.21.9 {
    // ---------------- input factories ----------------

    private static MouseButtonEvent makeClick(double x, double y, int button) {
        return new MouseButtonEvent(x, y, new MouseButtonInfo(button, 0));
    }
//? }
}
