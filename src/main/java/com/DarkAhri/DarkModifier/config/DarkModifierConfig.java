package com.DarkAhri.DarkModifier.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

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
    private static int queenWorkCycleThrottleIncrement = DEFAULT_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT;

    private final File configFile;
    private final Configuration config;
    private long lastLoadedTimestamp;

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
        if (currentTimestamp != instance.lastLoadedTimestamp) {
            instance.loadConfig();
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
        lastLoadedTimestamp = configFile.lastModified();

        if (config.hasChanged()) {
            config.save();
            lastLoadedTimestamp = configFile.lastModified();
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

    /**
     * Returns the configured increment used in queen work tick throttling.
     *
     * This checks for file-system changes each time so the mod can hot-reload its config without a restart.
     */
    private static synchronized void refreshFromConfigurationIfAvailable() {
        if (instance != null && instance.config != null) {
            int value = instance.config.getInt(
                "queenWorkCycleThrottleIncrement",
                CATEGORY_GENERAL,
                DEFAULT_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT,
                MIN_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT,
                MAX_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT,
                "The value by which queenWorkCycleThrottle will be incremented each time.");
            setQueenWorkCycleThrottleIncrement(value);
        }
    }

    public static synchronized int getQueenWorkCycleThrottleIncrement() {
        refreshFromConfigurationIfAvailable();
        reloadIfChanged();
        return queenWorkCycleThrottleIncrement;
    }

    /**
     * Returns the underlying Forge Configuration instance for advanced use.
     */
    public Configuration getConfig() {
        return config;
    }
}
