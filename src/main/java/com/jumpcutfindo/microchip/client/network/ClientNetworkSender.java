package com.jumpcutfindo.microchip.client.network;

import com.google.gson.Gson;
import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.jumpcutfindo.microchip.packets.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class ClientNetworkSender implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

    }

    public static final class MicrochipsActions {
        public static void addEntityToGroup(UUID groupId, UUID entityId) {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeUuid(groupId);
            buffer.writeUuid(entityId);

            ClientPlayNetworking.send(new AddEntityToGroupPacket(buffer));
        }

        public static void moveEntitiesBetweenGroups(UUID fromGroup, UUID toGroup, List<UUID> microchipIds) {
            Gson gson = new Gson();
            String microchipIdsSerialized = gson.toJson(microchipIds);

            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeUuid(fromGroup);
            buffer.writeUuid(toGroup);
            buffer.writeString(microchipIdsSerialized);

            ClientPlayNetworking.send(new MoveEntitiesBetweenGroupsPacket(buffer));
        }

        public static void removeEntitiesFromGroup(UUID groupId, List<UUID> microchipIds) {
            Gson gson = new Gson();
            String microchipIdsSerialized = gson.toJson(microchipIds);

            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeUuid(groupId);
            buffer.writeString(microchipIdsSerialized);

            ClientPlayNetworking.send(new RemoveEntitiesFromGroupPacket(buffer));
        }

        public static void createGroup(String groupName, GroupColor color) {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeString(groupName);
            buffer.writeInt(color.ordinal());

            ClientPlayNetworking.send(new CreateGroupPacket(buffer));
        }

        public static void updateGroup(MicrochipGroup group, String groupName, GroupColor color) {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeUuid(group.getId());
            buffer.writeString(groupName);
            buffer.writeInt(color.ordinal());

            ClientPlayNetworking.send(new UpdateGroupPacket(buffer));
        }

        public static void deleteGroup(UUID groupId) {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeUuid(groupId);

            ClientPlayNetworking.send(new DeleteGroupPacket(buffer));
        }

        public static void reorderGroup(int from, int to) {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeInt(from);
            buffer.writeInt(to);

            ClientPlayNetworking.send(new ReorderGroupsPacket(buffer));
        }

        public static void reorderMicrochips(UUID groupId, int from, int to) {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeUuid(groupId);
            buffer.writeInt(from);
            buffer.writeInt(to);

            ClientPlayNetworking.send(new ReorderMicrochipsPacket(buffer));
        }

        public static void updateMicrochips() {
            ClientPlayNetworking.send(new UpdateMicrochipsPacket(PacketByteBufs.create()));
        }
    }

    public static final class EntityActions {
        public static void glowEntity(LivingEntity entity) {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeUuid(entity.getUuid());
            ClientPlayNetworking.send(new GlowEntityPacket(buffer));
        }

        public static void glowEntity(Microchip microchip) {
            sendSimpleEntityPacket(GlowEntityPacket::new, microchip.getEntityId());
        }

        public static void locateEntity(Microchip microchip) {
            sendSimpleEntityPacket(LocateEntityPacket::new, microchip.getEntityId());
        }

        public static void teleportToEntity(Microchip microchip) {
            sendSimpleEntityPacket(TeleportPlayerToEntityPacket::new, microchip.getEntityId());
        }

        public static void healEntity(Microchip microchip) {
            sendSimpleEntityPacket(HealEntityPacket::new, microchip.getEntityId());
        }

        public static void killEntity(Microchip microchip) {
            sendSimpleEntityPacket(KillEntityPacket::new, microchip.getEntityId());
        }

        private static void sendSimpleEntityPacket(Function<PacketByteBuf, CustomPayload> packetCreator, UUID entityId) {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeUuid(entityId);

            ClientPlayNetworking.send(packetCreator.apply(buffer));
        }
    }

    public static final class RequestActions {
        public static void requestEntityData(UUID entityId) {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeUuid(entityId);

            ClientPlayNetworking.send(new RequestEntityDataPacket(buffer));
        }
    }

}
