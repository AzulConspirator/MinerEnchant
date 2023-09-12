package com.azul.minerenchant.enchantment;

import com.azul.minerenchant.minerEnchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class modEchantments 
{
    public static Enchantment DIGGER = register("digger",new digger_enchantment(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.DIGGER, EquipmentSlot.MAINHAND));
    
    private static Enchantment register(String name,Enchantment enchantment)
    {
         return Registry.register(Registries.ENCHANTMENT,new Identifier(minerEnchant.MOD_ID,name),enchantment);
    }

    public static void registerModEnchantments() {
        minerEnchant.LOGGER.info("Registering ModEnchantments for " + minerEnchant.MOD_ID);
    }
}
