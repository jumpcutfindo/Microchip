package com.jumpcutfindo.microchip.packets;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

import static com.jumpcutfindo.microchip.MicrochipMod.MOD_ID;

public class RequestEntityDataPacket implements CustomPayload {
    public static final Id<RequestEntityDataPacket> PACKET_ID = new Id<>(Identifier.of(MOD_ID, "request_entity_data"));
    public static final PacketCodec<RegistryByteBuf, RequestEntityDataPacket> PACKET_CODEC = PacketCodec.of(RequestEntityDataPacket::write, RequestEntityDataPacket::new);

    private final UUID entityId;

    public RequestEntityDataPacket(PacketByteBuf buf) {
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
