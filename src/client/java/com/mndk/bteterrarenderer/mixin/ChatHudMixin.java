package com.mndk.bteterrarenderer.mixin;

import com.mndk.bteterrarenderer.core.gui.MapRenderingOptionsSidebar;
import com.mndk.bteterrarenderer.core.gui.sidebar.SidebarSide;
import com.mndk.bteterrarenderer.mcconnector.client.gui.screen.AbstractGuiScreenImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatHud.class)
public class ChatHudMixin {

    @Shadow @Final private MinecraftClient client;

//? if >=1.21.11 {
    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;IIIZZ)V", at = @At(value = "HEAD"))
    public void preRender(DrawContext context, TextRenderer textRenderer, int currentTick, int mouseX, int mouseY,
                          boolean focused, boolean overlay, CallbackInfo ci) {
        var pose = context.getMatrices();
//? } else if >=1.20.5 {
    /*@Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;IIIZ)V", at = @At(value = "HEAD"))
    public void preRender(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        var pose = context.getMatrices();
*///? } else if >=1.20 {
    /*@Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;III)V", at = @At(value = "HEAD"))
    public void preRender(DrawContext context, int currentTick, int mouseX, int mouseY, CallbackInfo ci) {
        var pose = context.getMatrices();
*///? } else if >=1.19.3 {
    /*@Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;III)V", at = @At(value = "HEAD"))
    public void preRender(MatrixStack pose, int currentTick, int mouseX, int mouseY, CallbackInfo ci) {
*///? } else {
    /*@Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;I)V", at = @At(value = "HEAD"))
    public void preRender(MatrixStack pose, int currentTick, CallbackInfo ci) {
*///? }
        pose./*? if >=1.21.6 {*/pushMatrix()/*? } else {*//*push()*//*? }*/;

        Screen currentScreen = MinecraftClient.getInstance().currentScreen;
        if (!(currentScreen instanceof AbstractGuiScreenImpl screenImpl)) return;
        if (!(screenImpl.delegate instanceof MapRenderingOptionsSidebar sidebar)) return;
        if (sidebar.side.get() != SidebarSide.LEFT) return;

        int translateX = sidebar.sidebarWidth.get().intValue();
        pose.translate(translateX, 0/*? if <1.21.6 {*//*, 0*//*? }*/);
    }

//? if >=1.21.11 {
    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;IIIZZ)V", at = @At(value = "RETURN"))
    public void postRender(DrawContext context, TextRenderer textRenderer, int currentTick, int mouseX, int mouseY,
                           boolean focused, boolean overlay, CallbackInfo ci) {
        var pose = context.getMatrices();
//? } else if >=1.20.5 {
    /*@Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;IIIZ)V", at = @At(value = "RETURN"))
    public void postRender(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        var pose = context.getMatrices();
*///? } else if >=1.20 {
    /*@Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;III)V", at = @At(value = "RETURN"))
    public void postRender(DrawContext context, int currentTick, int mouseX, int mouseY, CallbackInfo ci) {
        var pose = context.getMatrices();
*///? } else if >=1.19.3 {
    /*@Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;III)V", at = @At(value = "RETURN"))
    public void postRender(MatrixStack pose, int currentTick, int mouseX, int mouseY, CallbackInfo ci) {
*///? } else {
    /*@Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;I)V", at = @At(value = "RETURN"))
    public void postRender(MatrixStack pose, int currentTick, CallbackInfo ci) {
*///? }
        pose./*? if >=1.21.6 {*/popMatrix()/*? } else {*//*pop()*//*? }*/;
    }

    @Inject(method = "isChatFocused", at = @At(value = "RETURN"), cancellable = true)
    public void isChatFocused(CallbackInfoReturnable<Boolean> cir) {
        if (client.currentScreen instanceof AbstractGuiScreenImpl screenImpl) {
            cir.setReturnValue(screenImpl.delegate.isChatFocused());
        }
    }

}
