package com.azul.minerenchant.enchantment;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;

public class diggerEnchantmentHelper {

    // This is called regardless of if the entity is a player in creative mode or not.
    public static void onBlockBroken(PlayerEntity player, BlockPos pos) {
        forEachEnchantment((enchantment, stack, level) -> enchantment.onBlockBreak(player, pos, stack, level), player.getMainHandStack());
    }

    private static void forEachEnchantment(Consumer consumer, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        NbtList nbtList = stack.getEnchantments();
        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound compound = nbtList.getCompound(i);
            Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(compound)).ifPresent(enchantment -> {
                if (enchantment instanceof digger_enchantment) {
                    consumer.accept((digger_enchantment) enchantment, stack, EnchantmentHelper.getLevelFromNbt(compound));
                }
            });
        }
    }

    @FunctionalInterface
    interface Consumer {
        void accept(digger_enchantment enchantment, ItemStack stack, int level);
    }
}
