package com.mndk.bteterrarenderer.mcconnector.client.graphics;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;

import javax.annotation.Nonnull;

public record WorldDrawContextWrapperImpl(
        @Nonnull PoseStack poseStack,
//? if >=1.21.10 {
        @Nonnull SubmitNodeCollector submitNodeCollector
//? } else {
        /*@Nonnull MultiBufferSource bufferSource
*///? }
) implements WorldDrawContextWrapper {}
