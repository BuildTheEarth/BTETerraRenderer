package com.mndk.bteterrarenderer.mixin;

import com.mndk.bteterrarenderer.core.gui.MapRenderingOptionsSidebar;
import com.mndk.bteterrarenderer.core.gui.sidebar.SidebarSide;
import com.mndk.bteterrarenderer.mcconnector.client.gui.screen.AbstractGuiScreenImpl;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {

    @Shadow @Final private Minecraft minecraft;

//? if >=26.1 {
    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V", at = @At(value = "HEAD"))
    public void preRender(
            final GuiGraphicsExtractor graphics, final Font font, final int ticks, final int mouseX, final int mouseY,
            final ChatComponent.DisplayMode displayMode, final boolean changeCursorOnInsertions, CallbackInfo ci) {
        var pose = graphics.pose();
//? } else if >=1.21.11 {
    /*@Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;IIIZZ)V", at = @At(value = "HEAD"))
    public void preRender(
            GuiGraphics context, Font textRenderer, int currentTick, int mouseX, int mouseY, boolean focused,
            boolean overlay, CallbackInfo ci) {
        var pose = context.pose();
*///? } else if >=1.20.5 {
    /*@Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIIZ)V", at = @At(value = "HEAD"))
    public void preRender(GuiGraphics context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        var pose = context.pose();
*///? } else if >=1.20 {
    /*@Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;III)V", at = @At(value = "HEAD"))
    public void preRender(GuiGraphics context, int currentTick, int mouseX, int mouseY, CallbackInfo ci) {
        var pose = context.pose();
*///? } else if >=1.19.3 {
    /*@Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;III)V", at = @At(value = "HEAD"))
    public void preRender(PoseStack pose, int currentTick, int mouseX, int mouseY, CallbackInfo ci) {
*///? } else {
    /*@Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;I)V", at = @At(value = "HEAD"))
    public void preRender(PoseStack pose, int currentTick, CallbackInfo ci) {
*///? }
        pose./*? if >=1.21.6 {*/pushMatrix()/*? } else {*//*pushPose()*//*? }*/;

        Screen currentScreen = Minecraft.getInstance().screen;
        if (!(currentScreen instanceof AbstractGuiScreenImpl screenImpl)) return;
        if (!(screenImpl.delegate instanceof MapRenderingOptionsSidebar sidebar)) return;
        if (sidebar.side.get() != SidebarSide.LEFT) return;
        if (((ChatComponent)(Object)this).isChatFocused()) return;

        int translateX = sidebar.sidebarWidth.get().intValue() + 1;
        pose.translate(translateX, 0/*? if <1.21.6 {*//*, 0*//*? }*/);
    }

//? if >=26.1 {
    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V", at = @At(value = "RETURN"))
    public void postRender(
            final GuiGraphicsExtractor graphics, final Font font, final int ticks, final int mouseX, final int mouseY,
            final ChatComponent.DisplayMode displayMode, final boolean changeCursorOnInsertions, CallbackInfo ci) {
        var pose = graphics.pose();
//? } else if >=1.21.11 {
    /*@Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;IIIZZ)V", at = @At(value = "RETURN"))
    public void postRender(
            GuiGraphics context, Font textRenderer, int currentTick, int mouseX, int mouseY, boolean focused,
            boolean overlay, CallbackInfo ci) {
        var pose = context.pose();
*///? } else if >=1.20.5 {
    /*@Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIIZ)V", at = @At(value = "RETURN"))
    public void postRender(GuiGraphics context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        var pose = context.pose();
*///? } else if >=1.20 {
    /*@Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;III)V", at = @At(value = "RETURN"))
    public void postRender(GuiGraphics context, int currentTick, int mouseX, int mouseY, CallbackInfo ci) {
        var pose = context.pose();
*///? } else if >=1.19.3 {
    /*@Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;III)V", at = @At(value = "RETURN"))
    public void postRender(PoseStack pose, int currentTick, int mouseX, int mouseY, CallbackInfo ci) {
*///? } else {
    /*@Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;I)V", at = @At(value = "RETURN"))
    public void postRender(PoseStack pose, int currentTick, CallbackInfo ci) {
*///? }
        pose./*? if >=1.21.6 {*/popMatrix()/*? } else {*//*popPose()*//*? }*/;
    }

    @Inject(method = "isChatFocused", at = @At(value = "RETURN"), cancellable = true)
    public void isChatFocused(CallbackInfoReturnable<Boolean> cir) {
        if (minecraft.screen instanceof AbstractGuiScreenImpl screenImpl) {
            cir.setReturnValue(screenImpl.delegate.isChatFocused());
        }
    }

}
