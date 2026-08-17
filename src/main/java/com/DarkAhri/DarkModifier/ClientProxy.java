package com.DarkAhri.DarkModifier;

public class ClientProxy extends CommonProxy {

    /**
     * Client-side initialization. Keep client-specific registrations (renderers, key bindings) here.
     * Calls through to super to ensure shared initialization happens.
     */
    @Override
    public void preInit(cpw.mods.fml.common.event.FMLPreInitializationEvent event) {
        super.preInit(event);
        // TODO: register client-side renderers and handlers
    }

}
