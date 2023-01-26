package com.jumpcutfindo.microchip.server.network;

import com.jumpcutfindo.microchip.constants.NetworkConstants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

public class ServerNetworkSender {
    public static void sendEntityStatusesResponse(ServerPlayerEntity player, Collection<StatusEffectInstance> effects) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeCollection(effects, ((packetByteBuf, statusEffectInstance) -> {
            NbtCompound nbt = new NbtCompound();
            nbt = statusEffectInstance.writeNbt(nbt);

            packetByteBuf.writeNbt(nbt);
        }));

        ServerPlayNetworking.send(player, NetworkConstants.PACKET_REQUEST_ENTITY_STATUSES_ID, buffer);
    }
}
