package com.jumpcutfindo.microchip;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MicrochipMod implements ModInitializer {
	public static final String MOD_ID = "microchip";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Microchip is installed and ready to tag everything!");
	}
}
