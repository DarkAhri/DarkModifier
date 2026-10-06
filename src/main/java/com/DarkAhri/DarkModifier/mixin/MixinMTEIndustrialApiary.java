package com.DarkAhri.DarkModifier.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.DarkAhri.DarkModifier.config.DarkModifierConfig;

import gregtech.common.tileentities.machines.basic.MTEIndustrialApiary;

/**
 * Speeds up the GregTech industrial apiary's production cycle.
 * <p>
 * The vanilla code computes its cycle like this (offsets from a 5.09.54.75 disassembly):
 *
 * <pre>
 * this.usedBeeLife = beelifespan * 550.0f; // 1355: the cycle base constant
 * this.mMaxProgresstime = (int) usedBeeLife; // 1368
 * int minTime = mMaxProgresstime / 100; // 1375: integer division, 0 for short cycles
 * int speed = 1 &lt;&lt; mSpeed;
 * mMaxProgresstime = mMaxProgresstime / Math.min(speed, minTime); // 1404: divides by 0 when minTime is 0
 * </pre>
 * <p>
 * Two things happen here. First the base constant is divided by the configured throttle, shortening the cycle. Second
 * the {@code / 100} is turned into {@code / 1}: that division is what feeds {@code Math.min}, and once the cycle drops
 * below 100 ticks it evaluates to 0 and the machine throws {@code ArithmeticException} every time it starts a recipe.
 * Replacing it with {@code / 1} keeps the divisor at least 1, removing both the crash and GregTech's built-in 100 tick
 * floor so the machine can actually run faster than that.
 * <p>
 * When the feature is switched off via the client keybinding, this mixin restores vanilla behavior: the cycle base
 * goes back to {@code 550.0f} (the config getter then yields the vanilla increment {@code 1}) and the {@code / 100}
 * floor is re-applied.
 */
@Mixin(value = MTEIndustrialApiary.class, remap = false)
public class MixinMTEIndustrialApiary {

    @ModifyConstant(method = "checkRecipe", constant = @Constant(floatValue = 550.0f), remap = false)
    private float getCustomBeeCycleLength(float constant) {
        return 550.0f / DarkModifierConfig.getQueenWorkCycleThrottleIncrement();
    }

    @ModifyConstant(method = "checkRecipe", constant = @Constant(intValue = 100, ordinal = 0), remap = false)
    private int removeMinimumCycleFloor(int original) {
        // 1 while speedups are active (removes the 100 tick floor); 100 when switched off to keep vanilla behavior.
        return DarkModifierConfig.isEnabled() ? 1 : 100;
    }
}
