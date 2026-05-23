package com.azulc.minerenchant.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.azulc.minerenchant.ModEnchantments;
import com.azulc.minerenchant.minerenchant;

public class ModDatapackBuiltinEntriesProvider extends DatapackBuiltinEntriesProvider 
{
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder().add(Registries.ENCHANTMENT, ModEnchantments::bootstrap);

    public ModDatapackBuiltinEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) 
    {
        super(output, registries, BUILDER, Set.of(minerenchant.MODID));
    }
}