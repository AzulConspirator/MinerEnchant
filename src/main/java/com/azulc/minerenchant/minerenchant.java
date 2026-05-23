package com.azulc.minerenchant;

import org.slf4j.Logger;

import com.azulc.minerenchant.event.EnchantmentEventHandler;
import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(minerenchant.MODID)
public class minerenchant {
    public static final String MODID = "minerenchant";
    public static final Logger LOGGER = LogUtils.getLogger();
    //
    public minerenchant(IEventBus modEventBus, ModContainer modContainer) 
    {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
    }
    
    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}
