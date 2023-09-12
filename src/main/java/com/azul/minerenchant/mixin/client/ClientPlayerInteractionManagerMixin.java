package com.azul.minerenchant.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.azul.minerenchant.enchantment.diggerEnchantmentHelper;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Shadow @Final private MinecraftClient client;

    // Suppressing this.client.player possibly being null. In this use, it never will.
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "breakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V"))
    private void digger_enchantment$injectOnBlockBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        diggerEnchantmentHelper.onBlockBroken(this.client.player, pos);
    }
}
