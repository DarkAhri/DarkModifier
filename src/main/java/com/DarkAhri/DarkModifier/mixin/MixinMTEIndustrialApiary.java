package com.DarkAhri.DarkModifier.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.DarkAhri.DarkModifier.config.DarkModifierConfig;

import gregtech.common.tileentities.machines.basic.MTEIndustrialApiary;

@Mixin(value = MTEIndustrialApiary.class, remap = false)
public class MixinMTEIndustrialApiary {

    // 方案 A：拦截常量读取（推荐）
    @ModifyConstant(method = "checkRecipe", constant = @Constant(floatValue = 550.0f), remap = false)
    private float getCustomBeeCycleLength(float constant) {
        // 可以在这里添加更复杂的逻辑
        // 例如：根据机器等级、世界时间等动态返回值
        return 550.0f / DarkModifierConfig.getQueenWorkCycleThrottleIncrement();// 自定义系数
    }
}
