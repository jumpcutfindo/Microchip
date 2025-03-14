package com.jumpcutfindo.microchip.server.network;

import com.jumpcutfindo.microchip.packets.EntityDataPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerNetworkSender implements ModInitializer {
    @Override
    public void onInitialize() {
        initializeSenders();
    }

    public static void initializeSenders() {
        PayloadTypeRegistry.playS2C().register(EntityDataPacket.PACKET_ID, EntityDataPacket.PACKET_CODEC);
    }

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
