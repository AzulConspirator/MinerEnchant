package com.azulc.minerenchant.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import java.util.concurrent.CompletableFuture;
import com.azulc.minerenchant.minerenchant;

@EventBusSubscriber(modid = minerenchant.MODID)
public class ModDataGenerator 
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) 
    {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        
        generator.addProvider(event.includeServer() ,new ModDatapackBuiltinEntriesProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new EnchantmentTag(packOutput, lookupProvider, existingFileHelper));
    }
}