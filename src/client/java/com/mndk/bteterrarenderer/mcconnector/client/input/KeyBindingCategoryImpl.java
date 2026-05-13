package com.mndk.bteterrarenderer.mcconnector.client.input;

import javax.annotation.Nonnull;

public record KeyBindingCategoryImpl(
//? if >=1.21.9 {
        @Nonnull net.minecraft.client.KeyMapping.Category delegate
//? } else {
        /*@Nonnull String delegate
*///? }
) implements IKeyBindingCategory {
}
