package com.mndk.bteterrarenderer.mod.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
//? if >=1.19 {
import net.fabricmc.fabric.api.client.command.v2.*;
import net.minecraft.commands.CommandBuildContext;
//? } else {
/*import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
*///? }

//? if >=1.19 {
public class CommandsRegisterer implements ClientCommandRegistrationCallback {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(new CommandsRegisterer());
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        var toggleSubcommand = LiteralArgumentBuilder.<FabricClientCommandSource>literal("toggle")
                .executes(new ToggleRenderingCommand())
                .build();
//? if >=26.1 {
        var btrCommand = ClientCommands.literal("btr").then(toggleSubcommand);
//? } else {
        /*var btrCommand = ClientCommandManager.literal("btr").then(toggleSubcommand);
*///? }
        dispatcher.register(btrCommand);
    }
}

//? } else {
/*@UtilityClass
public class CommandsRegisterer {

    public void register() {
        var toggleSubcommand = ClientCommandManager.literal("toggle")
                .executes(new ToggleRenderingCommand())
                .build();
        var btrCommand = ClientCommandManager.literal("btr").then(toggleSubcommand);
        ClientCommandManager.DISPATCHER.register(btrCommand);
    }
}
*///? }
