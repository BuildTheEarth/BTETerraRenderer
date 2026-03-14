package com.mndk.bteterrarenderer.mod.client.event;

import com.mndk.bteterrarenderer.core.tile.RenderManager;
import com.mndk.bteterrarenderer.mcconnector.client.gui.GuiDrawContextWrapper;
import com.mndk.bteterrarenderer.mcconnector.client.gui.GuiDrawContextWrapperImpl;
import com.mndk.bteterrarenderer.mcconnector.client.graphics.WorldDrawContextWrapper;
import com.mndk.bteterrarenderer.mcconnector.client.graphics.WorldDrawContextWrapperImpl;
import com.mndk.bteterrarenderer.mod.util.IdUtil;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.rendering.v1.*;
//? if >=1.21.6 {
import net.fabricmc.fabric.api.client.rendering.v1.hud.*;
//? }
//? if >=26.1 {
import net.fabricmc.fabric.api.client.rendering.v1.level.*;
//? } else if >=1.21.10 {
/*import net.fabricmc.fabric.api.client.rendering.v1.world.*;
*///? }
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
//? if >=1.21.2 {
import net.minecraft.util.profiling.Profiler;
//? }

@UtilityClass
public class RenderEvents {

    public static void registerEvents() {
//? if >=26.1 {
        LevelRenderEvents.AFTER_TRANSLUCENT_FEATURES.register(RenderEvents::onWorldRender);
//? } else if !=1.21.9 {
        /*WorldRenderEvents.AFTER_ENTITIES.register(RenderEvents::onWorldRender);
*///? }

//? if >=1.21.6 {
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.MISC_OVERLAYS,
                IdUtil.fromNamespaceAndPath("bteterrarenderer", "tile_renderer_info_element"),
                RenderEvents::onHudRender
        );
//? } else if >= 1.21.4 {
        /*HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerAfter(
                IdentifiedLayer.MISC_OVERLAYS,
                IdUtil.fromNamespaceAndPath("bteterrarenderer", "tile_renderer_info_layer"),
                RenderEvents::onHudRender
        ));
*///? } else {
        /*HudRenderCallback.EVENT.register(RenderEvents::onHudRender);
*///? }
    }

//? if !=1.21.9 {
    @SuppressWarnings("resource")
    private static void onWorldRender(/*? if >=26.1 {*/LevelRenderContext/*? } else {*//*WorldRenderContext*//*? }*/ renderContext) {
//? if >=1.21.10 {
        Minecraft client = renderContext.gameRenderer().getMinecraft();
        if (client.level == null || client.player == null) return;

//? if >=26.1 {
        PoseStack stack = renderContext.poseStack();
        MultiBufferSource provider = renderContext.bufferSource();
//? } else {
        /*PoseStack stack = renderContext.matrices();
        MultiBufferSource provider = renderContext.consumers();
*///? }
        if (stack == null || provider == null) return;

        WorldDrawContextWrapper context = new WorldDrawContextWrapperImpl(stack, provider);
        Camera camera = renderContext.gameRenderer().getMainCamera();
        Vec3 cameraPos = camera.position();
//? } else {
        /*var world = renderContext.world();
        Minecraft client = renderContext.gameRenderer().getMinecraft();
        if (world == null || client.player == null) return;

        PoseStack stack = renderContext.matrixStack();
        MultiBufferSource provider = renderContext.consumers();
        if (stack == null || provider == null) return;
        WorldDrawContextWrapper context = new WorldDrawContextWrapperImpl(stack, provider);

        // While the player is the "rendering center" in 1.12.2,
        // After 1.18.2 it is the camera being that center.
        // So the camera's position should be given instead, unlike in 1.12.2.
        Vec3 cameraPos = renderContext.camera().getPosition();
*///? }

//? if >=1.21.2 {
        Profiler.get().popPush("bteterrarenderer-hologram");
//? } else {
        /*world.getProfiler().popPush("bteterrarenderer-hologram");
*///? }

        RenderManager.renderTiles(context, cameraPos.x, cameraPos.y, cameraPos.z);
    }
//? }

//? if >=26.1 {
    private static void onHudRender(GuiGraphicsExtractor drawContext, DeltaTracker tickCounter) {
//? } else if >=1.21 {
    /*private static void onHudRender(GuiGraphics drawContext, DeltaTracker tickCounter) {
*///? } else if >=1.20 {
    /*private static void onHudRender(GuiGraphics drawContext, float tickCounter) {
*///? } else {
    /*private static void onHudRender(PoseStack drawContext, float tickCounter) {
*///? }
        GuiDrawContextWrapper wrapper = new GuiDrawContextWrapperImpl(drawContext);
        RenderManager.renderHud(wrapper);
    }
}
