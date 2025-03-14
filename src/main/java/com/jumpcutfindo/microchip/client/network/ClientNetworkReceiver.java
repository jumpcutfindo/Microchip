package com.jumpcutfindo.microchip.client.network;

import com.jumpcutfindo.microchip.packets.EntityDataPacket;
import com.jumpcutfindo.microchip.screen.MicrochipScreen;
import com.jumpcutfindo.microchip.screen.window.MicrochipInfoWindow;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientNetworkReceiver implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        onEntityDataResponse();
    }

    public static void onEntityDataResponse() {
        ClientPlayNetworking.registerGlobalReceiver(EntityDataPacket.PACKET_ID, (entityDataPacket, context) -> {
            if (context.client().currentScreen instanceof MicrochipScreen screen && screen.getActiveWindow() instanceof MicrochipInfoWindow infoWindow) {
                infoWindow.setEntityStatuses(entityDataPacket.entityStatuses());
                infoWindow.setBreedingAge(entityDataPacket.breedingAge());
                infoWindow.setInventoryList(entityDataPacket.inventoryList(), entityDataPacket.inventorySize());
            }
        });
    }
}
