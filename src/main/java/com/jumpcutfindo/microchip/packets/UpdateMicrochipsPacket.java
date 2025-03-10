package com.jumpcutfindo.microchip.packets;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

import static com.jumpcutfindo.microchip.MicrochipMod.MOD_ID;

public class UpdateMicrochipsPacket implements CustomPayload {
    public static final Id<UpdateMicrochipsPacket> PACKET_ID = new Id<>(Identifier.of(MOD_ID, "update_microchips"));
    public static final PacketCodec<RegistryByteBuf, UpdateMicrochipsPacket> PACKET_CODEC = PacketCodec.of(UpdateMicrochipsPacket::write, UpdateMicrochipsPacket::new);

    public UpdateMicrochipsPacket(PacketByteBuf buf) {}

    private void write(PacketByteBuf buf) {}

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
