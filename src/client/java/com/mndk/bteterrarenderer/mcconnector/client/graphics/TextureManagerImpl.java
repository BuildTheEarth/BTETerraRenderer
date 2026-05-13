package com.mndk.bteterrarenderer.mcconnector.client.graphics;

import com.mndk.bteterrarenderer.mod.util.IdUtil;
import com.mndk.bteterrarenderer.util.IOUtil;
import com.mojang.blaze3d.platform.NativeImage;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;

public class TextureManagerImpl extends TextureManager {

    protected NativeTextureWrapper getMissingTextureObject() {
        return new NativeTextureWrapperImpl(MissingTextureAtlasSprite.getLocation(), 16, 16);
    }
    @SneakyThrows
    protected NativeTextureWrapper allocateAndGetTextureObject(String modId, int count, @Nonnull BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        NativeImage nativeImage = NativeImage.read(IOUtil.imageToInputStream(image));
        DynamicTexture texture = new DynamicTexture(
                /*? if >=1.21.5 {*/() -> modId + "-dynamic-" + count,/*? }*/
                nativeImage
        );
        var id = IdUtil.fromNamespaceAndPath(modId, "dynamic-" + count);
        Minecraft.getInstance().getTextureManager().register(id, texture);
        return new NativeTextureWrapperImpl(id, width, height);
    }
    protected void deleteTextureObjectInternal(NativeTextureWrapper textureObject) {
        Minecraft.getInstance().getTextureManager().release(((NativeTextureWrapperImpl) textureObject).delegate);
    }

}
