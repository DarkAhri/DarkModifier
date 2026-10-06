package com.DarkAhri.DarkModifier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;

import com.DarkAhri.DarkModifier.config.DarkModifierConfig;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Client-side proxy. Registers the keybinding that toggles all speedups at runtime.
 *
 * The toggle is intentionally not persisted: every fresh start of the game runs with the mod enabled.
 */
public class ClientProxy extends CommonProxy {

    public static final KeyBinding KEY_TOGGLE = new KeyBinding(
        "key.darkmodifier.toggle",
        Keyboard.KEY_APOSTROPHE,
        "key.categories.darkmodifier");

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ClientRegistry.registerKeyBinding(KEY_TOGGLE);
    }

    /**
     * Fires every client tick. {@link KeyBinding#isPressed()} is edge-triggered in 1.7.10: it returns {@code true}
     * exactly once per physical key press, so the toggle never repeats while the key is held. It works even while a
     * GUI is open.
     */
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !KEY_TOGGLE.isPressed()) {
            return;
        }
        DarkModifierConfig.toggleEnabled();
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            String langKey = DarkModifierConfig.isEnabled() ? "darkmodifier.message.toggled_on"
                : "darkmodifier.message.toggled_off";
            mc.thePlayer.addChatMessage(new ChatComponentText(StatCollector.translateToLocal(langKey)));
        }
    }
}
