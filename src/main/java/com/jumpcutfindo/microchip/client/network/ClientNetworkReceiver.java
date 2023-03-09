package com.jumpcutfindo.microchip.client.network;

import com.jumpcutfindo.microchip.constants.NetworkConstants;
import com.jumpcutfindo.microchip.screen.MicrochipScreen;
import com.jumpcutfindo.microchip.screen.window.MicrochipInfoWindow;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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
        ClientPlayNetworking.registerGlobalReceiver(NetworkConstants.PACKET_REQUEST_ENTITY_DATA_ID, (client, handler, buf, responseSender) -> {
            Collection<StatusEffectInstance> entityStatuses = buf.readCollection((size) -> new ArrayList<>(), (packetByteBuf -> StatusEffectInstance.fromNbt(packetByteBuf.readNbt())));
            int breedingAge = buf.readInt();

            NbtCompound inventory = buf.readNbt();
            List<ItemStack> inventoryList = new ArrayList<>();
            if (inventory != null && inventory.contains("Inventory")) {
                NbtList inventoryNbtList = inventory.getList("Inventory", 10);
                inventoryNbtList.forEach(element -> inventoryList.add(ItemStack.fromNbt((NbtCompound) element)));
            }

            int inventorySize = buf.readInt();

            if (client.currentScreen instanceof MicrochipScreen screen && screen.getActiveWindow() instanceof MicrochipInfoWindow infoWindow) {
                infoWindow.setEntityStatuses(entityStatuses);
                infoWindow.setBreedingAge(breedingAge);
                infoWindow.setInventoryList(inventoryList, inventorySize);
            }
        });
    }
}
