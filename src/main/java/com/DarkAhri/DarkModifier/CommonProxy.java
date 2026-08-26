package com.DarkAhri.DarkModifier;

import static com.DarkAhri.DarkModifier.DarkModifier.MODID;

import java.io.File;

import com.DarkAhri.DarkModifier.config.DarkModifierConfig;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Common (server-side) proxy. Holds shared initialization logic.
 *
 * Improvements made:
 * - Persist the loaded configuration on the proxy so other systems can access it
 * - Add simple null checks and documentation
 */
public class CommonProxy {

    private DarkModifierConfig config;

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("FMLPreInitializationEvent must not be null");
        }
        File configFile = new File(event.getModConfigurationDirectory(), MODID + ".cfg");
        this.config = new DarkModifierConfig(configFile);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {}

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {}

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {}

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        DarkModifierConfig.onConfigChanged(event.modID);
    }

    /**
     * Returns the loaded configuration instance, or null if preInit has not run yet.
     */
    public DarkModifierConfig getConfig() {
        return config;
    }
}
