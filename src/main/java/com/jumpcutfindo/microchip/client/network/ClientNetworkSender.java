package com.jumpcutfindo.microchip.client.network;

import com.google.gson.Gson;
import com.jumpcutfindo.microchip.constants.NetworkConstants;
import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;

import java.util.List;
import java.util.UUID;

public class ClientNetworkSender {
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

    public static void sendMoveEntitiesPacket(UUID fromGroup, UUID toGroup, List<UUID> microchipIds) {
        Gson gson = new Gson();
        String microchipIdsSerialized = gson.toJson(microchipIds);

        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeUuid(fromGroup);
        buffer.writeUuid(toGroup);
        buffer.writeString(microchipIdsSerialized);

        ClientPlayNetworking.send(NetworkConstants.PACKET_MOVE_ENTITIES_ID, buffer);
    }

    public static void sendRemoveEntitiesFromGroupPacket(UUID groupId, List<UUID> microchipIds) {
        Gson gson = new Gson();
        String microchipIdsSerialized = gson.toJson(microchipIds);

        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeUuid(groupId);
        buffer.writeString(microchipIdsSerialized);

        ClientPlayNetworking.send(NetworkConstants.PACKET_REMOVE_ENTITY_FROM_GROUP_ID, buffer);
    }

    public static void sendCreateGroupPacket(String groupName, GroupColor color) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeString(groupName);
        buffer.writeInt(color.ordinal());

        ClientPlayNetworking.send(NetworkConstants.PACKET_CREATE_GROUP_ID, buffer);
    }

    public static void sendRequestToUpdateMicrochips() {
        ClientPlayNetworking.send(NetworkConstants.PACKET_UPDATE_ALL_MICROCHIPS_ID, PacketByteBufs.create());
    }

    public static void sendUpdateGroupPacket(MicrochipGroup group, String groupName, GroupColor color) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeUuid(group.getId());
        buffer.writeString(groupName);
        buffer.writeInt(color.ordinal());

        ClientPlayNetworking.send(NetworkConstants.PACKET_UPDATE_GROUP_ID, buffer);
    }

    public static void sendDeleteGroupPacket(UUID groupId) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeUuid(groupId);

        ClientPlayNetworking.send(NetworkConstants.PACKET_DELETE_GROUP_ID, buffer);
    }

    public static void sendRequestForEntityStatuses(UUID entityId) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeUuid(entityId);

        ClientPlayNetworking.send(NetworkConstants.PACKET_REQUEST_ENTITY_STATUSES_ID, buffer);
    }
}
