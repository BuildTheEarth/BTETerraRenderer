package com.mndk.bteterrarenderer.mixin;

import com.mndk.bteterrarenderer.mcconnector.client.gui.screen.AbstractGuiScreenImpl;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Gui.class)
public class GuiMixin {

//? if >=26.2 {
    /*@Shadow @Nullable private Screen screen;

    @Inject(method = "setScreen", at = @At(value = "HEAD"), cancellable = true)
    public void preSetScreen(Screen screen, CallbackInfo ci) {
        if (screen == null && this.screen instanceof AbstractGuiScreenImpl screenImpl) {
            boolean escapable = screenImpl.delegate.handleScreenEscape();
            if (!escapable) ci.cancel();
        }
    }
*///? }

}
