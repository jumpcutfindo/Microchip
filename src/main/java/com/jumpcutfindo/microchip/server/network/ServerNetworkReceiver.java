package com.jumpcutfindo.microchip.server.network;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jumpcutfindo.microchip.constants.NetworkConstants;
import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.data.MicrochipEntityData;
import com.jumpcutfindo.microchip.data.Microchips;
import com.jumpcutfindo.microchip.helper.Tagger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

/**
 * Instantiates the listeners for packets from the client.
 * Two types of requests are considered: requests that do not need a response, and requests that do. We segregate them
 * by their naming -- requests that require responses are specifically indicated as such. Those that do not should not
 * contain the word "request" in the method name.
 */
public class ServerNetworkReceiver implements ModInitializer {
    @Override
    public void onInitialize() {
        initializeListeners();
    }

    private static void initializeListeners() {
        onGlowEntity();
        onLocateEntity();
        onTeleportToEntity();
        onHealEntity();
        onKillEntity();

        onAddEntityToGroup();
        onMoveEntitiesBetweenGroups();
        onRemoveEntitiesFromGroup();
        onCreateGroup();
        onUpdateGroup();
        onDeleteGroup();

        onRequestEntityStatuses();
        onUpdateMicrochips();
    }

    private static void onGlowEntity() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkConstants.PACKET_GLOW_ENTITY_ID, ((server, player, handler, buf, responseSender) -> {
            UUID entityId = buf.readUuid();
            if (entityId == null) return;

            LivingEntity entity = (LivingEntity) player.getWorld().getEntity(entityId);
            if (entity != null) entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 40, 1), player);
        }));
    }

    private static void onLocateEntity() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkConstants.PACKET_LOCATE_ENTITY_ID, ((server, player, handler, buf, responseSender) -> {

        }));
    }

    private static void onTeleportToEntity() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkConstants.PACKET_TELEPORT_TO_ENTITY_ID, ((server, player, handler, buf, responseSender) -> {

        }));
    }

    private static void onHealEntity() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkConstants.PACKET_HEAL_ENTITY_ID, ((server, player, handler, buf, responseSender) -> {

        }));
    }

    private static void onKillEntity() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkConstants.PACKET_KILL_ENTITY_ID, ((server, player, handler, buf, responseSender) -> {

        }));
    }

    private static void onAddEntityToGroup() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkConstants.PACKET_ADD_ENTITY_TO_GROUP_ID, ((server, player, handler, buf, responseSender) -> {
            UUID groupId = buf.readUuid();
            UUID entityId = buf.readUuid();
            if (groupId == null || entityId == null) return;

            Microchips microchips = Tagger.getMicrochips(player);
            LivingEntity entity = (LivingEntity) player.getWorld().getEntity(entityId);
            if (entity != null) microchips.addToGroup(groupId, Microchip.of(entity));
        }));
    }

    private static void onMoveEntitiesBetweenGroups() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkConstants.PACKET_MOVE_ENTITIES_ID, ((server, player, handler, buf, responseSender) -> {
            UUID fromId = buf.readUuid();
            UUID toId = buf.readUuid();
            String microchipIdsSerialized = buf.readString();

            Gson gson = new Gson();
            Type idsType = new TypeToken<List<UUID>>(){}.getType();
            List<UUID> microchipIds = gson.fromJson(microchipIdsSerialized, idsType);

            Microchips microchips = Tagger.getMicrochips(player);
            microchips.moveBetweenGroups(fromId, toId, microchipIds);
        }));
    }

    private static void onRemoveEntitiesFromGroup() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkConstants.PACKET_REMOVE_ENTITY_FROM_GROUP_ID, ((server, player, handler, buf, responseSender) -> {
            UUID groupId = buf.readUuid();
            String microchipIdsSerialised = buf.readString();

            Gson gson = new Gson();
            Type idsType = new TypeToken<List<UUID>>(){}.getType();
            List<UUID> microchipIds = gson.fromJson(microchipIdsSerialised, idsType);

            if (groupId == null || microchipIds == null) return;

            Microchips microchips = Tagger.getMicrochips(player);
            microchips.removeFromGroup(groupId, microchipIds);
        }));
    }

    private static void onCreateGroup() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkConstants.PACKET_CREATE_GROUP_ID, ((server, player, handler, buf, responseSender) -> {
            String groupName = buf.readString();
            GroupColor color = GroupColor.values()[buf.readInt()];

            Microchips microchips = Tagger.getMicrochips(player);
            microchips.createGroup(groupName, color);
        }));
    }

    private static void onUpdateGroup() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkConstants.PACKET_UPDATE_GROUP_ID, (server, player, handler, buf, responseSender) -> {
            UUID groupId = buf.readUuid();
            String groupName = buf.readString();
            GroupColor color = GroupColor.values()[buf.readInt()];

            Microchips microchips = Tagger.getMicrochips(player);
            microchips.updateGroup(groupId, groupName, color);
        });
    }

    private static void onDeleteGroup() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkConstants.PACKET_DELETE_GROUP_ID, ((server, player, handler, buf, responseSender) -> {
            UUID groupId = buf.readUuid();

            Microchips microchips = Tagger.getMicrochips(player);
            microchips.deleteGroup(groupId);
        }));
    }

    private static void onUpdateMicrochips() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkConstants.PACKET_UPDATE_ALL_MICROCHIPS_ID, ((server, player, handler, buf, responseSender) -> {
            Microchips microchips = Tagger.getMicrochips(player);
            List<Microchip> microchipList = microchips.getAllMicrochips();
            microchipList.forEach(microchip -> {
                LivingEntity entity = (LivingEntity) player.getWorld().getEntity(microchip.getEntityId());
                if (entity != null) microchip.setEntityData(MicrochipEntityData.from(entity));
            });

            microchips.sync();
        }));
    }

    private static void onRequestEntityStatuses() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkConstants.PACKET_REQUEST_ENTITY_STATUSES_ID, ((server, player, handler, buf, responseSender) -> {
            UUID entityId = buf.readUuid();

            LivingEntity target = (LivingEntity) player.getWorld().getEntity(entityId);

            if (target != null) {
                ServerNetworkSender.sendEntityStatusesResponse(player, target.getStatusEffects());
            }
        }));
    }
}
