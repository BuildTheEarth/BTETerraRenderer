package com.mndk.bteterrarenderer.mcconnector.client;

import com.mojang.blaze3d.platform.Window;

public record WindowDimensionImpl(Window window) implements WindowDimension {

    @Override
    public int getPixelWidth() { return this.window.getWidth(); }

    @Override
    public int getPixelHeight() { return this.window.getHeight(); }

    @Override
    public int getScaledWidth() { return this.window.getGuiScaledWidth(); }

    @Override
    public int getScaledHeight() { return this.window.getGuiScaledHeight(); }

    @Override
    public float getScaleFactorX() {
        int scaled = getScaledWidth();
        return scaled == 0 ? 0f : (float) getPixelWidth() / (float) scaled;
    }

    @Override
    public float getScaleFactorY() {
        int scaled = getScaledHeight();
        return scaled == 0 ? 0f : (float) getPixelHeight() / (float) scaled;
    }
}
