package com.DarkAhri.DarkModifier.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.gtnewhorizon.cropsnh.tileentity.TileEntityCropSticks;

@Mixin(TileEntityCropSticks.class)
public class MixinTileEntityCropSticks {

    @ModifyConstant(
        method = "updateEntity", // 修改 updateEntity 方法中的常量
        constant = @Constant(intValue = 256),
        remap = true)
    private int modifyTickRateInUpdateEntity(int original) {
        return com.DarkAhri.DarkModifier.config.DarkModifierConfig.getCropMixinTickRate();
    }
}
