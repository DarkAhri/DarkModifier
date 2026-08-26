package com.DarkAhri.DarkModifier.config;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;

public class ModGuiFactory implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraftInstance) {
        // No client-side initialization is required for this simple config GUI.
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
                new ConfigElement<>(
                    DarkModifierConfig.getInstance()
                        .getConfig()
                        .getCategory(DarkModifierConfig.CATEGORY_GENERAL)).getChildElements(),
                com.DarkAhri.DarkModifier.DarkModifier.MODID,
                false,
                false,
                "DarkModifier Configuration",
                (DarkModifierConfig.getInstance() != null && DarkModifierConfig.getInstance()
                    .getConfig() != null) ? DarkModifierConfig.getInstance()
                        .getConfig()
                        .getCategory(DarkModifierConfig.CATEGORY_GENERAL)
                        .getComment() : null);

        }
    }
}
