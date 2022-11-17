package com.jumpcutfindo.microchip.server;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jumpcutfindo.microchip.constants.NetworkConstants;
import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.jumpcutfindo.microchip.data.Microchips;
import com.jumpcutfindo.microchip.helper.Looker;
import com.jumpcutfindo.microchip.helper.Tagger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerNetworker implements ModInitializer {
    @Override
    public void onInitialize() {
        initializeListeners();
    }

    private static void initializeListeners() {
        onGlowEntityPacket();
        onAddEntityToGroup();
        onRemoveEntitiesFromGroup();
        onCreateGroup();
        onDeleteGroup();
    }

    private static void onGlowEntityPacket() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkConstants.PACKET_GLOW_ENTITY_ID, ((server, player, handler, buf, responseSender) -> {
            UUID entityId = buf.readUuid();
            if (entityId == null) return;

            LivingEntity entity = Looker.getEntityByUuid(player.world, player, entityId);
            if (entity != null) entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 40, 1), player);
        }));
    }

    private static void onAddEntityToGroup() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkConstants.PACKET_ADD_ENTITY_TO_GROUP_ID, ((server, player, handler, buf, responseSender) -> {
            UUID groupId = buf.readUuid();
            UUID entityId = buf.readUuid();
            if (groupId == null || entityId == null) return;

            Microchips microchips = Tagger.getMicrochips(player);
            microchips.addToGroup(groupId, new Microchip(entityId));

            sendScreenRefresh(player);
        }));
    }

    private static void onRemoveEntitiesFromGroup() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkConstants.PACKET_REMOVE_ENTITY_FROM_GROUP_ID, ((server, player, handler, buf, responseSender) -> {
            UUID groupId = buf.readUuid();
            String entityIdsSerialised = buf.readString();

            Gson gson = new Gson();
            Type idsType = new TypeToken<List<UUID>>(){}.getType();
            List<UUID> entityIds = gson.fromJson(entityIdsSerialised, idsType);

            if (groupId == null || entityIds == null) return;

            Microchips microchips = Tagger.getMicrochips(player);
            microchips.removeFromGroup(groupId, entityIds);

            sendScreenRefresh(player);
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

    private static void onDeleteGroup() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkConstants.PACKET_DELETE_GROUP_ID, ((server, player, handler, buf, responseSender) -> {
            UUID groupId = buf.readUuid();

            Microchips microchips = Tagger.getMicrochips(player);
            microchips.deleteGroup(groupId);
        }));
    }

    public static void sendScreenRefresh(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, NetworkConstants.PACKET_REFRESH_SCREEN, PacketByteBufs.create());
    }
}
