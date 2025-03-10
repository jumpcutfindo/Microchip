package com.jumpcutfindo.microchip.packets;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

import static com.jumpcutfindo.microchip.MicrochipMod.MOD_ID;

public class AddEntityToGroupPacket implements CustomPayload {
    public static final Id<AddEntityToGroupPacket> PACKET_ID = new Id<>(Identifier.of(MOD_ID, "add_entity_to_group"));
    public static final PacketCodec<RegistryByteBuf, AddEntityToGroupPacket> PACKET_CODEC = PacketCodec.of(AddEntityToGroupPacket::write, AddEntityToGroupPacket::new);

    private final UUID groupId, entityId;

    public AddEntityToGroupPacket(PacketByteBuf buf) {
        this.groupId = buf.readUuid();
        this.entityId = buf.readUuid();
    }

    private void write(PacketByteBuf buf) {
        Uuids.PACKET_CODEC.encode(buf, this.groupId);
        Uuids.PACKET_CODEC.encode(buf, this.entityId);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public UUID groupId() {
        return groupId;
    }

    public UUID entityId() {
        return entityId;
    }
}
