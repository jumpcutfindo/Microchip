package com.jumpcutfindo.microchip.packets;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

import static com.jumpcutfindo.microchip.MicrochipMod.MOD_ID;

public class RemoveEntitiesFromGroupPacket implements CustomPayload {
    public static final Id<RemoveEntitiesFromGroupPacket> PACKET_ID = new Id<>(Identifier.of(MOD_ID, "remove_entities_from_groups"));
    public static final PacketCodec<RegistryByteBuf, RemoveEntitiesFromGroupPacket> PACKET_CODEC = PacketCodec.of(RemoveEntitiesFromGroupPacket::write, RemoveEntitiesFromGroupPacket::new);

    private final UUID groupId;
    private final String microchipIdsSerialized;

    public RemoveEntitiesFromGroupPacket(PacketByteBuf buf) {
        this.groupId = buf.readUuid();

        this.microchipIdsSerialized = buf.readString();
    }

    private void write(PacketByteBuf buf) {
        Uuids.PACKET_CODEC.encode(buf, this.groupId);

        buf.writeString(microchipIdsSerialized);
    }

    public UUID groupId() {
        return this.groupId;
    }

    public String microchipIdsSerialized() {
        return microchipIdsSerialized;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
