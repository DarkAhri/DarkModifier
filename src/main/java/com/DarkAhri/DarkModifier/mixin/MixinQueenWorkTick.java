package com.DarkAhri.DarkModifier.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.DarkAhri.DarkModifier.config.DarkModifierConfig;

import forestry.apiculture.BeekeepingLogic;

/**
 * Replaces Forestry's hardcoded "one unit of throttle per work tick" with the configured value, so that the throttle
 * counter reaches its limit after ceil(limit / value) ticks instead of accumulating one unit per tick.
 * <p>
 * The vanilla counter tops out at 550, which is also the cycle length hardcoded into the GregTech industrial apiary -
 * see {@link MixinMTEIndustrialApiary}.
 */
@Mixin(value = BeekeepingLogic.class, remap = false)
public class MixinQueenWorkTick {

    @ModifyConstant(method = "queenWorkTick", constant = @Constant(intValue = 1), remap = false)
    private int modifyQueenWorkCycleThrottleIncrement(int original) {
        return DarkModifierConfig.getQueenWorkCycleThrottleIncrement();
    }
}
