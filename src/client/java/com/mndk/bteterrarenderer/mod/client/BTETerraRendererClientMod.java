package com.mndk.bteterrarenderer.mod.client;

import com.mndk.bteterrarenderer.core.BTETerraRendererCore;
import com.mndk.bteterrarenderer.core.input.KeyBindings;
import com.mndk.bteterrarenderer.core.loader.LoaderRegistry;
import com.mndk.bteterrarenderer.core.tile.TileMapService;
import com.mndk.bteterrarenderer.mcconnector.client.ClientMinecraftManagerImpl;
import com.mndk.bteterrarenderer.mod.client.command.CommandsRegisterer;
import com.mndk.bteterrarenderer.mod.client.event.ClientOngoingConnectionEvents;
import com.mndk.bteterrarenderer.mod.client.event.RenderEvents;
import com.mndk.bteterrarenderer.mod.client.event.TickEvents;
import com.mndk.bteterrarenderer.util.Loggers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

@SuppressWarnings("unused")
public class BTETerraRendererClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BTETerraRendererCore.initialize(new ClientMinecraftManagerImpl());
        CommandsRegisterer.register();
        KeyBindings.registerAll();

        // Events
        RenderEvents.registerEvents();
        TickEvents.registerEvents();
        ClientOngoingConnectionEvents.registerEvents();

        Loggers.get(this).info("Client Mod BTETerraRenderer initialized");

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            TileMapService tms = LoaderRegistry.getCurrentTMS();
            if (tms != null) {
                try {
                    tms.close();
                } catch (Exception e) {
                    Loggers.get(this).error("Failed to close tile map service", e);
                }
            }
        });
    }
}
