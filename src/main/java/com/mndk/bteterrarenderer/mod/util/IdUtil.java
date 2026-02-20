package com.mndk.bteterrarenderer.mod.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class IdUtil {
//? if >=1.21.11 {
	public static net.minecraft.resources.Identifier fromNamespaceAndPath(String namespace, String path) {
		return net.minecraft.resources.Identifier.fromNamespaceAndPath(namespace, path);
	}

	public static net.minecraft.resources.Identifier parse(String path) {
		return net.minecraft.resources.Identifier.parse(path);
	}

    public static net.minecraft.resources.Identifier withDefaultNamespace(String path) {
        return net.minecraft.resources.Identifier.withDefaultNamespace(path);
    }
//? } else if >=1.21 {
    /*public static net.minecraft.resources.ResourceLocation fromNamespaceAndPath(String namespace, String path) {
		return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(namespace, path);
	}

	public static net.minecraft.resources.ResourceLocation parse(String path) {
        return net.minecraft.resources.ResourceLocation.parse(path);
    }

    public static net.minecraft.resources.ResourceLocation withDefaultNamespace(String path) {
        return net.minecraft.resources.ResourceLocation.withDefaultNamespace(path);
    }
*///? } else {
    /*public static net.minecraft.resources.ResourceLocation fromNamespaceAndPath(String namespace, String path) {
		return new net.minecraft.resources.ResourceLocation(namespace, path);
	}

	public static net.minecraft.resources.ResourceLocation parse(String path) {
        return new net.minecraft.resources.ResourceLocation(path);
    }

    public static net.minecraft.resources.ResourceLocation withDefaultNamespace(String path) {
        return new net.minecraft.resources.ResourceLocation(path);
    }
*///? }
}
