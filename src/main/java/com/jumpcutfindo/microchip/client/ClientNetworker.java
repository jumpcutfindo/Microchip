package com.jumpcutfindo.microchip.client;

import java.util.UUID;

import com.jumpcutfindo.microchip.constants.NetworkConstants;
import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;

public class ClientNetworker implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        onScreenRefresh();
    }

    private static void onScreenRefresh() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkConstants.PACKET_REFRESH_SCREEN, (client, handler, buf, responseSender) -> {
            if (client.currentScreen instanceof MicrochipsMenuScreen screen) {
                screen.refreshScreen();
            }
        });
    }

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

    public static void sendCreateGroupPacket(String groupName, GroupColor color) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeString(groupName);
        buffer.writeInt(color.ordinal());

        ClientPlayNetworking.send(NetworkConstants.PACKET_CREATE_GROUP_ID, buffer);
    }

    public static void sendDeleteGroupPacket(UUID groupId) {

    }
}
