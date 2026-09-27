package com.DarkAhri.DarkModifier.config;

import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import com.DarkAhri.DarkModifier.DarkModifier;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;

/**
 * Entry point for the in-game config GUI. Registered via {@code guiFactory} on the {@link DarkModifier} mod
 * annotation, this is what enables the "Mod Options" button in the mod list.
 */
public class ModGuiFactory implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraftInstance) {
        // No client-side setup is needed; the GUI reads straight from the live Configuration instance.
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return ModConfigGui.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }

    public boolean hasConfigGui() {
        return true;
    }

    public static class ModConfigGui extends GuiConfig {

        public ModConfigGui(GuiScreen parentScreen) {
            super(
                parentScreen,
                getConfigElements(),
                DarkModifier.MODID,
                false,
                false,
                DarkModifier.MOD_NAME + " Configuration");
        }

        private static List<IConfigElement> getConfigElements() {
            Configuration configuration = DarkModifierConfig.getInstance()
                .getConfig();
            return new ConfigElement<>(configuration.getCategory(DarkModifierConfig.CATEGORY_GENERAL))
                .getChildElements();
        }

        /**
         * Persist any edits made in the GUI back to disk and refresh the cached values. GuiConfig only updates the
         * in-memory {@code Property} objects, so without this the changes would be lost on the next file reload.
         */
        @Override
        public void onGuiClosed() {
            super.onGuiClosed();
            DarkModifierConfig.reload();
        }
    }
}
