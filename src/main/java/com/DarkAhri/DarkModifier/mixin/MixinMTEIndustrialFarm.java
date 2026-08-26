package com.DarkAhri.DarkModifier.mixin;

import static com.DarkAhri.DarkModifier.config.DarkModifierConfig.getCropMixinTickRate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gtnewhorizon.cropsnh.api.ISeedData;
import com.gtnewhorizon.cropsnh.tileentity.multi.MTEIndustrialFarm;

@Mixin(value = MTEIndustrialFarm.class, remap = false)
public class MixinMTEIndustrialFarm {

    @Shadow
    private int getGrowthSpeedUnscaled(ISeedData aCrop) {
        throw new UnsupportedOperationException("Shadow method");
    }

    @Shadow
    private double getGrowthSpeedMultiplier() {
        throw new UnsupportedOperationException("Shadow method");
    }

    @Inject(method = "getGrowthProgressPerCycle", at = @At("HEAD"), cancellable = true)
    public void onGetGrowthProgressPerCycle(ISeedData aCrop, CallbackInfoReturnable<Double> cir) {
        // 1. 获取原始生长速度
        int tUnscaledGrowthSpeed = this.getGrowthSpeedUnscaled(aCrop);
        if (tUnscaledGrowthSpeed <= 0) {
            cir.setReturnValue(-1.0d);
            return;
        }

        // 2. 自定义计算方式 - 例如改为直接使用速度值
        double tGrowthPerCycle = (((double) tUnscaledGrowthSpeed) / getCropMixinTickRate()) * 100; // 您的自定义公式

        // 3. 应用倍率（保留原逻辑）
        tGrowthPerCycle *= this.getGrowthSpeedMultiplier();
        if (tGrowthPerCycle <= 0) {
            cir.setReturnValue(-1.0d);
            return;
        }

        // 4. 计算百分比
        double result = Math.min(
            1.0d,
            1.0d / Math.ceil(
                aCrop.getCrop()
                    .getGrowthDuration() / tGrowthPerCycle));
        cir.setReturnValue(result);
    }
}
