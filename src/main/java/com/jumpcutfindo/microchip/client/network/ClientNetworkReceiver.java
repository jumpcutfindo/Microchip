package com.jumpcutfindo.microchip.client.network;

import com.jumpcutfindo.microchip.constants.NetworkConstants;
import com.jumpcutfindo.microchip.packets.EntityDataPacket;
import com.jumpcutfindo.microchip.screen.MicrochipScreen;
import com.jumpcutfindo.microchip.screen.window.MicrochipInfoWindow;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
