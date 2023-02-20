package com.jumpcutfindo.microchip.client.network;

import com.jumpcutfindo.microchip.constants.NetworkConstants;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.window.MicrochipInfoWindow;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.ArrayList;
import java.util.Collection;

public class ClientNetworkReceiver implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        onEntityDataResponse();
    }

    public static void onEntityDataResponse() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkConstants.PACKET_REQUEST_ENTITY_DATA_ID, (client, handler, buf, responseSender) -> {

            Collection<StatusEffectInstance> entityStatuses = buf.readCollection((size) -> new ArrayList<>(), (packetByteBuf -> StatusEffectInstance.fromNbt(packetByteBuf.readNbt())));
            int breedingAge = buf.readInt();

            if (client.currentScreen instanceof MicrochipsMenuScreen screen && screen.getActiveWindow() instanceof MicrochipInfoWindow infoWindow) {
                infoWindow.setEntityStatuses(entityStatuses);
                infoWindow.setBreedingAge(breedingAge);
            }
        });
    }
}
