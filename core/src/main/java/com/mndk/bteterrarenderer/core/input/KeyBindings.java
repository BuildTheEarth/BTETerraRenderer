package com.mndk.bteterrarenderer.core.input;

import com.mndk.bteterrarenderer.BTETerraRenderer;
import com.mndk.bteterrarenderer.core.config.BTETerraRendererConfig;
import com.mndk.bteterrarenderer.core.gui.MapRenderingOptionsSidebar;
import com.mndk.bteterrarenderer.core.loader.LoaderRegistry;
import com.mndk.bteterrarenderer.mcconnector.McConnector;
import com.mndk.bteterrarenderer.mcconnector.client.ClientMinecraftManager;
import com.mndk.bteterrarenderer.mcconnector.client.input.GameInputManager;
import com.mndk.bteterrarenderer.mcconnector.client.input.IKeyBinding;
import com.mndk.bteterrarenderer.mcconnector.client.input.IKeyBindingCategory;
import com.mndk.bteterrarenderer.mcconnector.client.input.InputKey;

public class KeyBindings {

    public static IKeyBindingCategory CATEGORY;
    public static IKeyBinding MAP_TOGGLE_KEY;
    public static IKeyBinding MAP_OPTIONS_KEY;
    public static IKeyBinding MOVE_UP_KEY;
    public static IKeyBinding MOVE_DOWN_KEY;
    private static boolean registered;

    public static void registerAll() {
        if (registered) return;
        ClientMinecraftManager client = McConnector.client();
        GameInputManager inputManager = client.inputManager;
        CATEGORY = inputManager.registerCategory(client.newResourceLocation(BTETerraRenderer.MODID, BTETerraRenderer.MODID));
        MAP_TOGGLE_KEY = inputManager.register(BTETerraRenderer.MODID, "toggle", InputKey.KEY_R, CATEGORY);
        MAP_OPTIONS_KEY = inputManager.register(BTETerraRenderer.MODID, "options_ui", InputKey.KEY_GRAVE_ACCENT, CATEGORY);
        MOVE_UP_KEY = inputManager.register(BTETerraRenderer.MODID, "move_up", InputKey.KEY_Y, CATEGORY);
        MOVE_DOWN_KEY = inputManager.register(BTETerraRenderer.MODID, "move_down", InputKey.KEY_I, CATEGORY);
        registered = true;
    }

    public static void checkInputs() {
        if (KeyBindings.MAP_TOGGLE_KEY.wasPressed()) {
            BTETerraRendererConfig.toggleRender();
        }
        if (KeyBindings.MAP_OPTIONS_KEY.wasPressed()) {
            MapRenderingOptionsSidebar.open();
        }
        while (KeyBindings.MOVE_UP_KEY.wasPressed()) {
            LoaderRegistry.getCurrentTMS().moveAlongYAxis(0.5);
        }
        while (KeyBindings.MOVE_DOWN_KEY.wasPressed()) {
            LoaderRegistry.getCurrentTMS().moveAlongYAxis(-0.5);
        }
    }
}
