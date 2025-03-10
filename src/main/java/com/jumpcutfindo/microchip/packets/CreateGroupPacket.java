package com.jumpcutfindo.microchip.packets;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

import static com.jumpcutfindo.microchip.MicrochipMod.MOD_ID;

public class CreateGroupPacket implements CustomPayload {
    public static final Id<CreateGroupPacket> PACKET_ID = new Id<>(Identifier.of(MOD_ID, "create_group"));
    public static final PacketCodec<RegistryByteBuf, CreateGroupPacket> PACKET_CODEC = PacketCodec.of(CreateGroupPacket::write, CreateGroupPacket::new);

    private final String groupName;
    private final int groupColorIndex;

    public CreateGroupPacket(PacketByteBuf buf) {
        this.groupName = buf.readString();
        this.groupColorIndex = buf.readInt();
    }

    private void write(PacketByteBuf buf) {
        buf.writeString(this.groupName);
        buf.writeInt(this.groupColorIndex);
    }

    public String groupName() {
        return groupName;
    }

    public int groupColorIndex() { return groupColorIndex; }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
