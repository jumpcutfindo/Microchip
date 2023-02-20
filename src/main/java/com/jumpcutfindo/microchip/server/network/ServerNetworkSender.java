package com.jumpcutfindo.microchip.server.network;

import com.jumpcutfindo.microchip.constants.NetworkConstants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

public class ServerNetworkSender {
    public static void sendEntityDataResponse(ServerPlayerEntity player, LivingEntity entity) {
        Collection<StatusEffectInstance> effects = entity.getStatusEffects();

        // Entity status effects
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeCollection(effects, ((packetByteBuf, statusEffectInstance) -> {
            NbtCompound nbt = new NbtCompound();
            nbt = statusEffectInstance.writeNbt(nbt);

            packetByteBuf.writeNbt(nbt);
        }));

        // Entity breeding time
        if (entity instanceof PassiveEntity passiveEntity) {
            buffer.writeInt(passiveEntity.getBreedingAge());
        } else {
            buffer.writeInt(0);
        }

        ServerPlayNetworking.send(player, NetworkConstants.PACKET_REQUEST_ENTITY_DATA_ID, buffer);
    }
}
