package com.mndk.bteterrarenderer.mcconnector.client.graphics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources./*? if >=1.21.11 {*/Identifier/*? } else {*//*ResourceLocation*//*? }*/;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class NativeTextureWrapperImpl extends AbstractNativeTextureWrapper {
    @Nonnull public final /*? if >=1.21.11 {*/Identifier/*? } else {*//*ResourceLocation*//*? }*/ delegate;
    @Getter public final int width;
    @Getter public final int height;
}
