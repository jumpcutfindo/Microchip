package com.jumpcutfindo.microchip.server;

import java.util.List;
import java.util.UUID;

import com.jumpcutfindo.microchip.constants.NetworkConstants;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Box;

public class ServerNetworker implements ModInitializer {
    @Override
    public void onInitialize() {
        initializeListeners();
    }

    private static void initializeListeners() {
        glowEntityPacket();
    }

    private static void glowEntityPacket() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkConstants.PACKET_GLOW_ENTITY_ID, ((server, player, handler, buf, responseSender) -> {
            UUID entityId = buf.readUuid();
            if (entityId == null) return;

            List<LivingEntity> entityList = player.world.getEntitiesByClass(LivingEntity.class, Box.from(player.getPos()).expand(256), entity -> entity.getUuid().equals(entityId));
            if (entityList.size() > 0) entityList.get(0).addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20, 1), player);
            })
        );
    }
}
