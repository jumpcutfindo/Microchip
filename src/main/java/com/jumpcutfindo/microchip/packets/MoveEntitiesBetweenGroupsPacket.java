package com.jumpcutfindo.microchip.packets;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;

import java.util.UUID;

import static com.jumpcutfindo.microchip.MicrochipMod.MOD_ID;

public class MoveEntitiesBetweenGroupsPacket implements CustomPayload {
    public static final CustomPayload.Id<MoveEntitiesBetweenGroupsPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "move_entities_between_groups"));
    public static final PacketCodec<RegistryByteBuf, MoveEntitiesBetweenGroupsPacket> PACKET_CODEC = PacketCodec.of(MoveEntitiesBetweenGroupsPacket::write, MoveEntitiesBetweenGroupsPacket::new);

    private final UUID fromId, toId;
    private final String microchipIdsSerialized;

    public MoveEntitiesBetweenGroupsPacket(PacketByteBuf buf) {
        this.fromId = buf.readUuid();
        this.toId = buf.readUuid();

        this.microchipIdsSerialized = buf.readString();
    }

    private void write(PacketByteBuf buf) {
        Uuids.PACKET_CODEC.encode(buf, this.fromId);
        Uuids.PACKET_CODEC.encode(buf, this.toId);

        buf.writeString(microchipIdsSerialized);
    }

    public String microchipIdsSerialized() {
        return microchipIdsSerialized;
    }

    public UUID toId() {
        return toId;
    }

    public UUID fromId() {
        return fromId;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
