package com.jumpcutfindo.microchip.packets;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

import static com.jumpcutfindo.microchip.MicrochipMod.MOD_ID;

public class HealEntityPacket implements CustomPayload {
    public static final CustomPayload.Id<HealEntityPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "heal_entity"));
    public static final PacketCodec<RegistryByteBuf, HealEntityPacket> PACKET_CODEC = PacketCodec.of(HealEntityPacket::write, HealEntityPacket::new);

    private final UUID entityId;

    public HealEntityPacket(PacketByteBuf buf) {
        this.entityId = buf.readUuid();
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.entityId);
    }

    public UUID entityId() {
        return entityId;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
