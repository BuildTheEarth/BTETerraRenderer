package com.mndk.bteterrarenderer.mcconnector.util;

import net.minecraft.resources./*? if >=1.21.11 {*/Identifier/*? } else {*//*ResourceLocation*//*? }*/;

import javax.annotation.Nonnull;

public record ResourceLocationWrapperImpl(
        @Nonnull /*? if >=1.21.11 {*/Identifier/*? } else {*//*ResourceLocation*//*? }*/ delegate
) implements ResourceLocationWrapper {}
