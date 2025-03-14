package com.jumpcutfindo.microchip.server.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.data.MicrochipEntityData;
import com.jumpcutfindo.microchip.data.Microchips;
import com.jumpcutfindo.microchip.helper.StringUtils;
import com.jumpcutfindo.microchip.helper.Tagger;
import com.jumpcutfindo.microchip.packets.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

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
        onReorderGroups();

        onRequestEntityData();
        onReorderMicrochips();
        onUpdateMicrochips();
    }

    /**
     * Helper function to help register and handle a packet received from the client
     * @param payloadId ID of the payload
     * @param payloadCodec Codec of the payload
     * @param handler How to handle the payload
     */
    private static <T extends CustomPayload> void registerAndHandle(
            CustomPayload.Id<T> payloadId,
            PacketCodec<? super RegistryByteBuf, T> payloadCodec,
            ServerPlayNetworking.PlayPayloadHandler<T> handler
    ) {
        PayloadTypeRegistry.playC2S().register(payloadId, payloadCodec);
        ServerPlayNetworking.registerGlobalReceiver(payloadId, handler);
    }

    private static void onGlowEntity() {
        registerAndHandle(
            GlowEntityPacket.PACKET_ID,
            GlowEntityPacket.PACKET_CODEC,
            (payload, context) -> {
                LivingEntity entity = findEntity(context.player(), payload.entityId());

                if (entity == null) return;

                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 40, 1), context.player());
            }
        );
    }

    private static void onLocateEntity() {
        registerAndHandle(
            LocateEntityPacket.PACKET_ID,
            LocateEntityPacket.PACKET_CODEC,
            (payload, context) -> {
                LivingEntity entity = findEntity(context.player(), payload.entityId());

                if (entity == null) return;

                // Apply a glowing effect to the entity for 60 seconds
                StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.GLOWING, 1200, 0);
                entity.addStatusEffect(statusEffectInstance, context.player());
                context.player().sendMessage(Text.translatable("microchip.menu.microchipInfo.actionTab.locate.applied", entity.getDisplayName()), false);
            }
        );
    }

    private static void onTeleportToEntity() {
        registerAndHandle(
            TeleportPlayerToEntityPacket.PACKET_ID,
            TeleportPlayerToEntityPacket.PACKET_CODEC,
            (payload, context) -> {
                LivingEntity entity = findEntity(context.player(), payload.entityId());

                if (entity == null) return;

                // Teleport the player to the entity
                context.player().requestTeleport(entity.getX(), entity.getY(), entity.getZ());
                context.player().sendMessage(Text.translatable("microchip.menu.microchipInfo.actionTab.teleportTo.applied", context.player().getDisplayName(), entity.getDisplayName(), StringUtils.coordinatesAsFancyText(entity.getX(), entity.getY(), entity.getZ())), false);
            }
        );
    }

    private static void onHealEntity() {
        registerAndHandle(
            HealEntityPacket.PACKET_ID,
            HealEntityPacket.PACKET_CODEC,
            (payload, context) -> {
                LivingEntity entity = findEntity(context.player(), payload.entityId());

                if (entity == null) return;

                // Heal entity
                entity.setHealth(entity.getMaxHealth());
                context.player().sendMessage(Text.translatable("microchip.menu.microchipInfo.actionTab.heal.applied", entity.getDisplayName()), false);
            }
        );
    }

    private static void onKillEntity() {
        registerAndHandle(
            KillEntityPacket.PACKET_ID,
                KillEntityPacket.PACKET_CODEC,
            (payload, context) -> {
                LivingEntity entity = findEntity(context.player(), payload.entityId());

                if (entity == null) return;

                // Kill entity
                entity.kill(context.player().getServerWorld());
                context.player().sendMessage(Text.translatable("microchip.menu.microchipInfo.actionTab.kill.applied", entity.getDisplayName()), false);
            }
        );
    }

    private static void onAddEntityToGroup() {
        registerAndHandle(
            AddEntityToGroupPacket.PACKET_ID,
            AddEntityToGroupPacket.PACKET_CODEC,
            (payload, context) -> {
                UUID groupId = payload.groupId();
                UUID entityId = payload.entityId();
                if (groupId == null || entityId == null) return;

                Microchips microchips = Tagger.getMicrochips(context.player());
                LivingEntity entity = getEntityByUUID(context.player(), entityId);
                if (entity != null) microchips.addToGroup(groupId, Microchip.of(entity));}
        );
    }

    private static void onMoveEntitiesBetweenGroups() {
        registerAndHandle(
            MoveEntitiesBetweenGroupsPacket.PACKET_ID,
            MoveEntitiesBetweenGroupsPacket.PACKET_CODEC,
            (payload, context) -> {
                Gson gson = new Gson();
                Type idsType = new TypeToken<List<UUID>>(){}.getType();
                List<UUID> microchipIds = gson.fromJson(payload.microchipIdsSerialized(), idsType);

                Microchips microchips = Tagger.getMicrochips(context.player());
                microchips.moveBetweenGroups(payload.fromId(), payload.toId(), microchipIds);
            }
        );
    }

    private static void onRemoveEntitiesFromGroup() {
        registerAndHandle(
            RemoveEntitiesFromGroupPacket.PACKET_ID,
            RemoveEntitiesFromGroupPacket.PACKET_CODEC,
            (payload, context) -> {
                Gson gson = new Gson();
                Type idsType = new TypeToken<List<UUID>>(){}.getType();
                List<UUID> microchipIds = gson.fromJson(payload.microchipIdsSerialized(), idsType);

                if (payload.groupId() == null || microchipIds == null) return;

                Microchips microchips = Tagger.getMicrochips(context.player());
                microchips.removeFromGroup(payload.groupId(), microchipIds);
            }
        );
    }

    private static void onCreateGroup() {
        registerAndHandle(
            CreateGroupPacket.PACKET_ID,
            CreateGroupPacket.PACKET_CODEC,
            (payload, context) -> {
                String groupName = payload.groupName();
                GroupColor color = GroupColor.values()[payload.groupColorIndex()];

                Microchips microchips = Tagger.getMicrochips(context.player());
                microchips.createGroup(groupName, color);
            }
        );
    }

    private static void onUpdateGroup() {
        registerAndHandle(
            UpdateGroupPacket.PACKET_ID,
            UpdateGroupPacket.PACKET_CODEC,
            (payload, context) -> {
                UUID groupId = payload.groupId();
                String groupName = payload.groupName();
                GroupColor color = GroupColor.values()[payload.groupColorIndex()];

                Microchips microchips = Tagger.getMicrochips(context.player());
                microchips.updateGroup(groupId, groupName, color);
            }
        );
    }

    private static void onDeleteGroup() {
        registerAndHandle(
            DeleteGroupPacket.PACKET_ID,
            DeleteGroupPacket.PACKET_CODEC,
            (payload, context) -> {
                UUID groupId = payload.groupId();
                Microchips microchips = Tagger.getMicrochips(context.player());
                microchips.deleteGroup(groupId);
            }
        );
    }

    private static void onReorderGroups() {
        registerAndHandle(
            ReorderGroupsPacket.PACKET_ID,
            ReorderGroupsPacket.PACKET_CODEC,
            (payload, context) -> {
                Microchips microchips = Tagger.getMicrochips(context.player());
                microchips.reorderGroup(payload.fromIndex(), payload.toIndex());
            }
        );
    }

    public static void onReorderMicrochips() {
        registerAndHandle(
            ReorderMicrochipsPacket.PACKET_ID,
            ReorderMicrochipsPacket.PACKET_CODEC,
            (payload, context) -> {

                Microchips microchips = Tagger.getMicrochips(context.player());
                microchips.reorderMicrochips(payload.groupId(), payload.fromIndex(), payload.toIndex());
            }
        );
    }

    private static void onUpdateMicrochips() {
        registerAndHandle(
            UpdateMicrochipsPacket.PACKET_ID,
            UpdateMicrochipsPacket.PACKET_CODEC,
            (payload, context) -> {
                Microchips microchips = Tagger.getMicrochips(context.player());
                List<Microchip> microchipList = microchips.getAllMicrochips();
                microchipList.forEach(microchip -> {
                    LivingEntity entity = getEntityByUUID(context.player(), microchip.getEntityId());
                    if (entity != null) microchip.setEntityData(MicrochipEntityData.from(entity));
                });

                microchips.sync();
            }
        );
    }

    private static void onRequestEntityData() {
        registerAndHandle(
                RequestEntityDataPacket.PACKET_ID,
                RequestEntityDataPacket.PACKET_CODEC,
                (payload, context) -> {
                    UUID entityId = payload.entityId();
                    LivingEntity target = getEntityByUUID(context.player(), entityId);

                    if (target != null) {
                        ServerNetworkSender.sendEntityDataResponse(context.player(), target);
                    }
                }
        );
    }

    private static LivingEntity findEntity(ServerPlayerEntity player, UUID entityId) {
        if (entityId != null) {
            LivingEntity entity = getEntityByUUID(player, entityId);
            if (entity != null) return entity;
        }

        player.sendMessage(Text.translatable("microchip.menu.microchipInfo.actionTab.cannotFindMob"), false);
        return null;
    }

    private static LivingEntity getEntityByUUID(ServerPlayerEntity player, UUID entityId) {
        List<LivingEntity> entities = player.getWorld()
                .getEntitiesByType(
                        TypeFilter.instanceOf(LivingEntity.class),
                        player.getBoundingBox().expand(128.0d),
                        (e) -> e.getUuid().equals(entityId)
                );

        if (entities.size() == 0) return null;
        else return entities.get(0);
    }
}
