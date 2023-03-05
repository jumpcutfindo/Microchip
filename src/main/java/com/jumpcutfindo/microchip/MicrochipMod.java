package com.jumpcutfindo.microchip;

import com.jumpcutfindo.microchip.data.MicrochipPlayerState;
import com.jumpcutfindo.microchip.server.MicrochipServerState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MicrochipMod implements ModInitializer {
	public static final String MOD_ID = "microchip";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Microchip is installed and ready to tag everything!");

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			MicrochipServerState serverState = MicrochipServerState.getServerState(handler.getPlayer().getWorld().getServer());
			MicrochipPlayerState playerState = serverState.getPlayerState(handler.getPlayer());

		});
	}
}
