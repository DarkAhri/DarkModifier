package com.DarkAhri.DarkModifier.config;

import java.io.File;
import java.util.concurrent.TimeUnit;

import net.minecraftforge.common.config.Configuration;

import com.DarkAhri.DarkModifier.DarkModifier;

public class DarkModifierConfig {

    public static final String CATEGORY_GENERAL = "general";

    private static final String LANG_PREFIX = DarkModifier.MODID + ".config.";

    private static final int DEFAULT_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT = 55;
    private static final int MIN_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT = 1;
    private static final int MAX_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT = 550;
    private static final String COMMENT_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT = "每个蜂后工作周期累加到 queenWorkCycleThrottle 的数值（直接替换 Forestry 原版每 tick 自增 1），"
        + "同时也是 GT 工业蜂房周期长度常量的除数。数值越大产出越快：原版需 550 tick 产出一次，设为 55 则约 10 tick 一次。允许范围 1 到 550。";

    private static final int DEFAULT_CROP_MIXIN_TICK_RATE = 16;
    private static final int MIN_CROP_MIXIN_TICK_RATE = 1;
    private static final int MAX_CROP_MIXIN_TICK_RATE = 256;
    private static final String COMMENT_CROP_MIXIN_TICK_RATE = "作物基准周期（tick）。替换 CropsNH 作物架的 256 tick 周期，"
        + "工业农场按其等比换算。数值越小作物越快：默认 16 约为原版 16 倍速，设为 256 等同原版。允许范围 1 到 256。";

    private static final long REFRESH_INTERVAL_NANOS = TimeUnit.SECONDS.toNanos(5);

    private static volatile DarkModifierConfig instance;
    private static volatile int queenWorkCycleThrottleIncrement = DEFAULT_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT;
    private static volatile int cropMixinTickRate = DEFAULT_CROP_MIXIN_TICK_RATE;
    private static volatile long lastCheckNanos = System.nanoTime();

    // Runtime speedup switch flipped by the client keybinding. Deliberately not persisted: the mod always starts
    // enabled, so an in-game toggle never surprises the player on the next launch.
    private static volatile boolean enabled = true;

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
            instance.applyValues();
        }
    }

    public static synchronized void reloadIfChanged() {
        DarkModifierConfig current = instance;
        if (current == null || current.configFile == null || !current.configFile.exists()) {
            return;
        }

        long currentTimestamp = current.configFile.lastModified();
        long currentSize = current.configFile.length();
        if (currentTimestamp != current.lastLoadedTimestamp || currentSize != current.lastLoadedSize) {
            current.loadConfig();
        }
    }

    public static synchronized void onConfigChanged(String modId) {
        if (DarkModifier.MODID.equals(modId)) {
            reload();
        }
    }

    private void loadConfig() {
        config.load();
        applyValues();
    }

    /**
     * Applies the current in-memory {@code Property} values to the cached static fields and saves when anything
     * changed. Unlike {@link #loadConfig()}, this deliberately does not read the file back from disk: it is invoked
     * right after the config GUI committed edits, and re-reading would discard them.
     */
    private void applyValues() {
        readValues();

        updateLoadedState();
        if (config.hasChanged()) {
            config.save();
            updateLoadedState();
        }
    }

    private void readValues() {
        config.getCategory(CATEGORY_GENERAL)
            .setLanguageKey(LANG_PREFIX + "category.general");

        queenWorkCycleThrottleIncrement = readConfigInt(
            "queenWorkCycleThrottleIncrement",
            DEFAULT_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT,
            MIN_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT,
            MAX_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT,
            COMMENT_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT);

        cropMixinTickRate = readConfigInt(
            "cropMixinTickRate",
            DEFAULT_CROP_MIXIN_TICK_RATE,
            MIN_CROP_MIXIN_TICK_RATE,
            MAX_CROP_MIXIN_TICK_RATE,
            COMMENT_CROP_MIXIN_TICK_RATE);
    }

    private int readConfigInt(String key, int defaultValue, int minValue, int maxValue, String comment) {
        net.minecraftforge.common.config.Property property = config
            .get(CATEGORY_GENERAL, key, defaultValue, comment, minValue, maxValue);
        property.setLanguageKey(LANG_PREFIX + key);
        // Forge does not enforce the bounds on load, so clamp them here as well.
        return Math.max(minValue, Math.min(maxValue, property.getInt(defaultValue)));
    }

    private void updateLoadedState() {
        lastLoadedTimestamp = configFile.lastModified();
        lastLoadedSize = configFile.length();
    }

    /**
     * Rate-limits the file based change detection. The getters are called from mixins on the tile entity tick, so doing
     * any file I/O there would be too costly; instead the check runs at most once per refresh interval, leaving the
     * hot path with just a couple of volatile reads.
     * <p>
     * {@link System#nanoTime()} is used deliberately: it is monotonic and therefore immune to clock adjustments that
     * would either disable the throttle entirely or make it stop firing.
     */
    private static void maybeRefresh() {
        long now = System.nanoTime();
        if (now - lastCheckNanos < REFRESH_INTERVAL_NANOS) {
            return;
        }
        lastCheckNanos = now;
        reloadIfChanged();
    }

    /**
     * Returns the effective throttle increment, or the vanilla value ({@code 1}) while speedups are switched off via
     * the client keybinding.
     */
    public static int getQueenWorkCycleThrottleIncrement() {
        maybeRefresh();
        return enabled ? queenWorkCycleThrottleIncrement : MIN_QUEEN_WORK_CYCLE_THROTTLE_INCREMENT;
    }

    /**
     * Returns the effective crop tick rate, or the vanilla value ({@code 256}) while speedups are switched off via
     * the client keybinding.
     */
    public static int getCropMixinTickRate() {
        maybeRefresh();
        return enabled ? cropMixinTickRate : MAX_CROP_MIXIN_TICK_RATE;
    }

    /**
     * Whether the speedups are currently active. Flipped by {@link #toggleEnabled()} from the client keybinding;
     * runtime-only state that defaults to enabled on startup.
     */
    public static boolean isEnabled() {
        return enabled;
    }

    public static void toggleEnabled() {
        enabled = !enabled;
    }

    public Configuration getConfig() {
        return config;
    }
}
