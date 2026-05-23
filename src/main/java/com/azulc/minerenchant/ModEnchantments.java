package com.azulc.minerenchant;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantment.Cost;

public class ModEnchantments 
{
    public static final ResourceKey<Enchantment> DIGGER = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(minerenchant.MODID, "digger"));

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> itemLookup = context.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantmentLookup = context.lookup(Registries.ENCHANTMENT);

        register(context, DIGGER, Enchantment.enchantment(
                Enchantment.definition(
                        itemLookup.getOrThrow(ItemTags.MINING_LOOT_ENCHANTABLE),
                        5,      // Weight
                        2,      // level
                        new Cost(15, 9), // Min cost
                        new Cost(65, 9), // Max cost
                        4, // Anvil cost
                        EquipmentSlotGroup.ANY
                )
        ).exclusiveWith(enchantmentLookup.getOrThrow(EnchantmentTags.MINING_EXCLUSIVE))); 
    }
    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }
}