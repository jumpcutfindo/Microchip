package com.jumpcutfindo.microchip.client;

import java.util.UUID;

import com.jumpcutfindo.microchip.constants.NetworkConstants;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;

public class ClientNetworker {
    public static void sendGlowPacket(LivingEntity entity) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeUuid(entity.getUuid());
        ClientPlayNetworking.send(NetworkConstants.PACKET_GLOW_ENTITY_ID, buffer);
    }

    public static void sendAddEntityToGroupPacket(UUID groupId, UUID entityId) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeUuid(groupId);
        buffer.writeUuid(entityId);

        ClientPlayNetworking.send(NetworkConstants.PACKET_ADD_ENTITY_TO_GROUP_ID, buffer);
    }


    public static void sendRemoveEntityFromGroupPacket(UUID groupId, UUID entityId) {

    }

    public static void sendCreateGroupPacket() {

    }

    public static void sendDeleteGroupPacket(UUID groupId) {

    }
}
