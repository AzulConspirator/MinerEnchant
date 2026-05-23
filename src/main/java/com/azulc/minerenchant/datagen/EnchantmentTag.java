package com.azulc.minerenchant.datagen;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import com.azulc.minerenchant.ModEnchantments;
import com.azulc.minerenchant.minerenchant;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class EnchantmentTag extends EnchantmentTagsProvider 
{
    public EnchantmentTag(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) 
    {
        super(output, lookupProvider, minerenchant.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) 
    {
        this.tag(EnchantmentTags.IN_ENCHANTING_TABLE).addOptional(ModEnchantments.DIGGER.location());
        this.tag(EnchantmentTags.TRADEABLE).addOptional(ModEnchantments.DIGGER.location());
        this.tag(EnchantmentTags.ON_RANDOM_LOOT).addOptional(ModEnchantments.DIGGER.location());
        this.tag(EnchantmentTags.DOUBLE_TRADE_PRICE).addOptional(ModEnchantments.DIGGER.location());
    }
}