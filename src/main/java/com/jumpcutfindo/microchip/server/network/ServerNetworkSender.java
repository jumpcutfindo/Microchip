package com.jumpcutfindo.microchip.server.network;

import com.jumpcutfindo.microchip.constants.NetworkConstants;
import com.jumpcutfindo.microchip.packets.EntityDataPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerNetworkSender {
    public static void sendEntityDataResponse(ServerPlayerEntity player, LivingEntity entity) {
        PacketByteBuf buf = PacketByteBufs.create();

        EntityDataPacketBuilder packetBuilder = new EntityDataPacketBuilder(buf, entity);
        PacketByteBuf buffer = packetBuilder
                .withStatusEffects()
                .withBreedingCooldown()
                .withInventory()
                .withInventorySize()
                .build();

        ServerPlayNetworking.send(player, new EntityDataPacket(buffer));
    }
}
