package com.azul.minerenchant;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azul.minerenchant.enchantment.modEchantments;

public class minerEnchant implements ModInitializer {
	
	public static final String MOD_ID = "minerenchant";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		modEchantments.registerModEnchantments();
	}
}