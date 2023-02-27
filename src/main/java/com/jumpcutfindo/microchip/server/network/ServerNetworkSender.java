package com.jumpcutfindo.microchip.server.network;

import com.jumpcutfindo.microchip.constants.NetworkConstants;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerNetworkSender {
    public static void sendEntityDataResponse(ServerPlayerEntity player, LivingEntity entity) {
        EntityDataPacketBuilder packetBuilder = new EntityDataPacketBuilder(player, entity);
        PacketByteBuf buffer = packetBuilder
                .withStatusEffects()
                .withBreedingCooldown()
                .withInventory()
                .withInventorySize()
                .build();

        ServerPlayNetworking.send(player, NetworkConstants.PACKET_REQUEST_ENTITY_DATA_ID, buffer);
    }
}
