package com.jumpcutfindo.microchip.packets;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

import static com.jumpcutfindo.microchip.MicrochipMod.MOD_ID;

public class DeleteGroupPacket implements CustomPayload {
    public static final Id<DeleteGroupPacket> PACKET_ID = new Id<>(Identifier.of(MOD_ID, "delete_group"));
    public static final PacketCodec<RegistryByteBuf, DeleteGroupPacket> PACKET_CODEC = PacketCodec.of(DeleteGroupPacket::write, DeleteGroupPacket::new);

    private final UUID groupId;

    public DeleteGroupPacket(PacketByteBuf buf) {
        this.groupId = buf.readUuid();
    }

    private void write(PacketByteBuf buf) {
        buf.writeUuid(groupId);
    }

    public UUID groupId() {
        return groupId;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
