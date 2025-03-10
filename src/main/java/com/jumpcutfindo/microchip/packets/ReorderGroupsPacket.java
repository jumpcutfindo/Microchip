package com.jumpcutfindo.microchip.packets;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

import static com.jumpcutfindo.microchip.MicrochipMod.MOD_ID;

public class ReorderGroupsPacket implements CustomPayload {
    public static final Id<ReorderGroupsPacket> PACKET_ID = new Id<>(Identifier.of(MOD_ID, "reorder_groups"));
    public static final PacketCodec<RegistryByteBuf, ReorderGroupsPacket> PACKET_CODEC = PacketCodec.of(ReorderGroupsPacket::write, ReorderGroupsPacket::new);

    private final int fromIndex, toIndex;

    public ReorderGroupsPacket(PacketByteBuf buf) {
        this.fromIndex = buf.readInt();
        this.toIndex = buf.readInt();
    }

    private void write(PacketByteBuf buf) {
        buf.writeInt(this.fromIndex);
        buf.writeInt(this.toIndex);
    }

    public int fromIndex() {
        return fromIndex;
    }

    public int toIndex() {
        return toIndex;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
