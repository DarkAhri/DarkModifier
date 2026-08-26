package com.DarkAhri.DarkModifier.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import com.DarkAhri.DarkModifier.DarkModifier;

/**
 * Configuration holder for DarkModifier module.
 *
 * Responsibilities:
 * - Provide defaults
 * - Load persisted config from disk
 * - Validate and expose configuration values through accessors
 */
public class DarkModifierConfig {

    public static final String CATEGORY_GENERAL = "general";

    private static final int DEFAULT_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT = 55;
    private static final int MIN_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT = 1;
    private static final int MAX_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT = 550;

    private static volatile DarkModifierConfig instance;

    // Backing field for the config value. Access via getter to keep encapsulation.
    private static volatile int queenWorkCycleThrottleIncrement = DEFAULT_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT;

    // New crop mixin configuration
    private static volatile int cropMixinTickRate = 16;

    private final File configFile;
    private final Configuration config;
    private long lastLoadedTimestamp;
    private long lastLoadedSize;

    public DarkModifierConfig(File configFile) {
        if (configFile == null) {
            throw new IllegalArgumentException("configFile must not be null");
        }
        this.configFile = configFile;
        this.config = new Configuration(configFile);
        instance = this;
        loadConfig();
    }

    public static DarkModifierConfig getInstance() {
        return instance;
    }

    public static synchronized void reload() {
        if (instance != null) {
            instance.loadConfig();
        }
    }

    public static synchronized void reloadIfChanged() {
        if (instance == null || instance.configFile == null || !instance.configFile.exists()) {
            return;
        }

        long currentTimestamp = instance.configFile.lastModified();
        long currentSize = instance.configFile.length();
        if (currentTimestamp != instance.lastLoadedTimestamp || currentSize != instance.lastLoadedSize) {
            instance.loadConfig();
        }
    }

    public static synchronized void onConfigChanged(String modId) {
        if (DarkModifier.MODID.equals(modId)) {
            reload();
        }
    }

    private void loadConfig() {
        config.load();

        // Set a human-readable comment for the category that will be shown in the GUI as a subtitle
        config.getCategory(CATEGORY_GENERAL)
            .setComment(
                "General settings for DarkModifier. Changes here apply immediately if supported; some options may require a restart.");
        // Set a language key for the category so it can be localized via lang files
        try {
            config.getCategory(CATEGORY_GENERAL)
                .setLanguageKey("darkmodifier.config.category.general");
        } catch (Exception ignored) {}

        // Create or get the property so we can set a language key and a comment (tooltip will be generated from langKey
        // + ".tooltip")
        net.minecraftforge.common.config.Property prop = config.get(
            CATEGORY_GENERAL,
            "queenWorkCycleThrottleIncrement",
            Integer.toString(DEFAULT_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT));
        prop.comment = "Amount added to queenWorkCycleThrottle each tick when the queen works. Increase to speed up bee work cycles; set too high may affect gameplay balance.";
        prop.setLanguageKey("darkmodifier.config.queenWorkCycleThrottleIncrement");
        int value = prop.getInt(DEFAULT_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT);

        setQueenWorkCycleThrottleIncrement(value);

        // Crop mixins: tick rate (mixins are always enabled)
        net.minecraftforge.common.config.Property cropTickProp = config
            .get(CATEGORY_GENERAL, "cropMixinTickRate", Integer.toString(16));
        cropTickProp.comment = "Tick rate used by crop mixins when modifying crop tick constants. Lower value = faster ticks.";
        cropTickProp.setLanguageKey("darkmodifier.config.cropMixinTickRate");
        cropMixinTickRate = cropTickProp.getInt(16);

        lastLoadedTimestamp = configFile.lastModified();
        lastLoadedSize = configFile.length();

        if (config.hasChanged()) {
            config.save();
            lastLoadedTimestamp = configFile.lastModified();
            lastLoadedSize = configFile.length();
        }
    }

    private static synchronized void setQueenWorkCycleThrottleIncrement(int value) {
        if (value < MIN_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT) {
            queenWorkCycleThrottleIncrement = MIN_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT;
        } else if (value > MAX_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT) {
            queenWorkCycleThrottleIncrement = MAX_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT;
        } else {
            queenWorkCycleThrottleIncrement = value;
        }
    }

    // refreshFromConfigurationIfAvailable removed — reloadIfChanged/loadConfig handles live updates.

    public static int getQueenWorkCycleThrottleIncrement() {
        // Check timestamp and reload config if file changed; loadConfig takes care of validation
        reloadIfChanged();
        return queenWorkCycleThrottleIncrement;
    }

    /**
     * Returns the configured tick rate used by crop mixins.
     */
    public static int getCropMixinTickRate() {
        reloadIfChanged();
        return cropMixinTickRate;
    }

    /**
     * Returns the underlying Forge Configuration instance for advanced use.
     */
    public Configuration getConfig() {
        return config;
    }
}
