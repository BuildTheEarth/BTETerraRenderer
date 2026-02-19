package com.mndk.bteterrarenderer.mcconnector.client.gui.screen;

import com.mndk.bteterrarenderer.mcconnector.client.gui.GuiDrawContextWrapperImpl;
import com.mndk.bteterrarenderer.mcconnector.client.input.InputKey;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;

import javax.annotation.Nonnull;

public class AbstractGuiScreenImpl extends Screen {

    public final AbstractGuiScreenCopy delegate;

    public AbstractGuiScreenImpl(@Nonnull AbstractGuiScreenCopy delegate) {
        super(/*?if >=1.19 {*/Text.empty()/*?} else {*//*LiteralText.EMPTY*//*? }*/);
        this.delegate = delegate;
    }

    @Override
    protected void init() {
        delegate.initGui(this.width, this.height);
    }

//? if >=1.21.11 {
    /**
     * 1.21.11: Screen#resize signature is now resize(int,int)
     */
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        delegate.setScreenSize(width, height);
    }
//? } else {
    /*@Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        delegate.setScreenSize(width, height);
    }
*///? }

    @Override
    public void tick() {
        delegate.tick();
    }

    @Override
    public void render(
            /*? if >=1.20 {*/DrawContext/*? } else {*//*MatrixStack*//*? }*/ context,
            int mouseX, int mouseY, float delta) {
        delegate.drawScreen(new GuiDrawContextWrapperImpl(context), mouseX, mouseY, delta);
    }

//? if >=1.21.6 {
    /**
     * 1.21.6: renderBackground() is no longer in render()
     */
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
    }
//? }

//? if >=1.21.9 {
    /**
     * 1.21.9: ParentElement#mouseClicked now takes (Click, boolean)
     */
    @Override
    public boolean mouseClicked(Click click, boolean doubleClick) {
        // Call vanilla behavior first (keeps button handling consistent)
        boolean superResult = super.mouseClicked(click, doubleClick);

        double x = click.x();
        double y = click.y();
        int button = click.button();

        boolean delegateResult = delegate.mousePressed(x, y, button);
        return superResult || delegateResult;
    }

    /**
     * 1.21.9: ParentElement#mouseReleased now takes (Click)
     */
    @Override
    public boolean mouseReleased(Click click) {
        boolean superResult = super.mouseReleased(click);

        double x = click.x();
        double y = click.y();
        int button = click.button();

        boolean delegateResult = delegate.mouseReleased(x, y, button);
        return superResult || delegateResult;
    }

    /**
     * 1.21.9: ParentElement#mouseDragged now takes (Click, double, double)
     * The extra doubles are typically drag deltas.
     */
    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        boolean superResult = super.mouseDragged(click, deltaX, deltaY);

        double x = click.x();
        double y = click.y();
        int button = click.button();

        // Your delegate expects (mouseX, mouseY, button, startX, startY)
        // We approximate startX/startY from current - delta
        boolean delegateResult = delegate.mouseDragged(x, y, button, x - deltaX, y - deltaY);
        return superResult || delegateResult;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        boolean superResult = super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        boolean delegateResult = delegate.mouseScrolled(mouseX, mouseY, verticalAmount);
        return superResult || delegateResult;
    }

    /**
     * 1.21.9: Screen#keyPressed now takes (KeyInput)
     */
    @Override
    public boolean keyPressed(KeyInput keyInput) {
        boolean superResult = super.keyPressed(keyInput);

        int keyCode = keyInput.key();
        int scanCode = keyInput.scancode();
        int modifiers = keyInput.modifiers();

        boolean delegateResult = delegate.keyPressed(InputKey.fromGlfwKeyCode(keyCode), scanCode, modifiers);
        return superResult || delegateResult;
    }

    /**
     * 1.21.9: ParentElement#charTyped now takes (CharInput)
     */
    @Override
    public boolean charTyped(CharInput charInput) {
        boolean superResult = super.charTyped(charInput);

        int modifiers = charInput.modifiers();
        int codepoint = charInput.codepoint();
        char chr = codepoint > 0 ? Character.toChars(codepoint)[0] : 0;

        boolean delegateResult = delegate.charTyped(chr, modifiers);
        return superResult || delegateResult;
    }
//? } else {
    /*@Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        return delegate.mousePressed(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        return delegate.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return delegate.mouseDragged(mouseX, mouseY, button, mouseX - deltaX, mouseY - deltaY);
    }

//? if >=1.20.2 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        return delegate.mouseScrolled(mouseX, mouseY, verticalAmount);
    }
//? } else {
    /^@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double verticalAmount) {
        super.mouseScrolled(mouseX, mouseY, verticalAmount);
        return delegate.mouseScrolled(mouseX, mouseY, verticalAmount);
    }
^///? }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean superResult = super.keyPressed(keyCode, scanCode, modifiers);
        boolean delegateResult = delegate.keyPressed(InputKey.fromGlfwKeyCode(keyCode), scanCode, modifiers);
        return superResult || delegateResult;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        super.charTyped(chr, modifiers);
        return delegate.charTyped(chr, modifiers);
    }
*///? }

    @Override
    public void removed() {
        delegate.onRemoved();
        super.removed();
    }

    @Override
    public boolean /*? if >=1.18.1 {*/shouldPause/*? } else {*//*isPauseScreen*//*? }*/() {
        return delegate.doesScreenPauseGame();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return delegate.shouldCloseOnEsc();
    }

}
