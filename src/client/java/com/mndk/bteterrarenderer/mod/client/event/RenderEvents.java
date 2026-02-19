package com.mndk.bteterrarenderer.mod.client.event;

import com.mndk.bteterrarenderer.core.tile.RenderManager;
import com.mndk.bteterrarenderer.mcconnector.client.gui.GuiDrawContextWrapper;
import com.mndk.bteterrarenderer.mcconnector.client.gui.GuiDrawContextWrapperImpl;
import com.mndk.bteterrarenderer.mcconnector.client.graphics.WorldDrawContextWrapper;
import com.mndk.bteterrarenderer.mcconnector.client.graphics.WorldDrawContextWrapperImpl;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.rendering.v1.*;
//? if >=1.21.10 {
import net.fabricmc.fabric.api.client.rendering.v1.world.*;
//? }
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
//? if >=1.21.2 {
import net.minecraft.util.profiler.Profilers;
//? }

@UtilityClass
public class RenderEvents {

    public static void registerEvents() {
//? if !=1.21.9 {
        WorldRenderEvents.AFTER_ENTITIES.register(RenderEvents::onWorldRender);
//? }
//? if >=1.21.6 {
        HudRenderCallback.EVENT.register(RenderEvents::onHudRender);
//? } else if >= 1.21.4 {
        /*HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerAfter(
                IdentifiedLayer.MISC_OVERLAYS,
                Identifier.of("bteterrarenderer", "tile_renderer_info_layer"),
                RenderEvents::onHudRender
        ));
*///? } else {
        /*HudRenderCallback.EVENT.register(RenderEvents::onHudRender);
*///? }
    }

//? if !=1.21.9 {
    @SuppressWarnings("resource")
    private static void onWorldRender(WorldRenderContext renderContext) {
//? if >=1.21.10 {
        MinecraftClient client = renderContext.gameRenderer().getClient();
        if (client.world == null || client.player == null) return;

        MatrixStack stack = renderContext.matrices();
        VertexConsumerProvider provider = renderContext.consumers();
        if (stack == null || provider == null) return;

        WorldDrawContextWrapper context = new WorldDrawContextWrapperImpl(stack, provider);
        Camera camera = renderContext.gameRenderer().getCamera();
        Vec3d cameraPos = camera.getCameraPos();
//? } else {
        /*var world = renderContext.world();
        MinecraftClient client = renderContext.gameRenderer().getClient();
        if (world == null || client.player == null) return;

        MatrixStack stack = renderContext.matrixStack();
        VertexConsumerProvider provider = renderContext.consumers();
        if (stack == null || provider == null) return;
        WorldDrawContextWrapper context = new WorldDrawContextWrapperImpl(stack, provider);

        // While the player is the "rendering center" in 1.12.2,
        // After 1.18.2 it is the camera being that center.
        // So the camera's position should be given instead, unlike in 1.12.2.
        Vec3d cameraPos = renderContext.camera().getPos();
*///? }

//? if >=1.21.2 {
        Profilers.get().swap("bteterrarenderer-hologram");
//? } else {
        /*world.getProfiler().swap("bteterrarenderer-hologram");
*///? }

        RenderManager.renderTiles(context, cameraPos.x, cameraPos.y, cameraPos.z);
    }
//? }

//? if >=1.21 {
    private static void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
//? } else if >=1.20 {
    /*private static void onHudRender(DrawContext drawContext, float tickCounter) {
*///? } else {
    /*private static void onHudRender(MatrixStack drawContext, float tickCounter) {
*///? }
        GuiDrawContextWrapper wrapper = new GuiDrawContextWrapperImpl(drawContext);
        RenderManager.renderHud(wrapper);
    }
}
