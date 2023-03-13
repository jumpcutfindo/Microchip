package com.jumpcutfindo.microchip.data;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.UUID;

public class PlayerMicrochips extends Microchips implements PlayerComponent<Microchips>, AutoSyncedComponent {
    protected PlayerEntity owner;

    public PlayerMicrochips(PlayerEntity owner) {
        this.owner = owner;
    }

    @Override
    public boolean createGroup(String name, GroupColor color) {
        boolean flag = super.createGroup(name, color);
        MicrochipComponents.MICROCHIPS.sync(this.owner);
        return flag;
    }

    @Override
    public boolean updateGroup(UUID groupId, String name, GroupColor color) {
        boolean flag = super.updateGroup(groupId, name, color);
        MicrochipComponents.MICROCHIPS.sync(this.owner);
        return flag;
    }

    @Override
    public boolean deleteGroup(UUID id) {
        boolean flag = super.deleteGroup(id);
        MicrochipComponents.MICROCHIPS.sync(this.owner);
        return flag;
    }

    @Override
    public void reorderGroup(int from, int to) {
        super.reorderGroup(from, to);
        MicrochipComponents.MICROCHIPS.sync(this.owner);
    }

    @Override
    public boolean addToGroup(UUID groupId, Microchip microchip) {
        boolean flag = super.addToGroup(groupId, microchip);
        MicrochipComponents.MICROCHIPS.sync(this.owner);
        return flag;
    }

    @Override
    public boolean removeFromGroup(UUID groupId, List<UUID> microchipIds) {
        boolean flag = super.removeFromGroup(groupId, microchipIds);
        MicrochipComponents.MICROCHIPS.sync(this.owner);
        return flag;
    }

    @Override
    public boolean moveBetweenGroups(UUID fromId, UUID toId, List<UUID> microchipIds) {
        boolean flag = super.moveBetweenGroups(fromId, toId, microchipIds);
        MicrochipComponents.MICROCHIPS.sync(this.owner);
        return flag;
    }

    @Override
    public void sync() {
        MicrochipComponents.MICROCHIPS.sync(this.owner);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.owner;
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        NbtCompound cpd = Microchips.toNbt(this);
        if (recipient == this.owner) buf.writeNbt(cpd);
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        NbtCompound cpd = buf.readNbt();
        if (cpd != null) Microchips.fromNbt(cpd,this);
    }

    @Override
    public boolean shouldCopyForRespawn(boolean lossless, boolean keepInventory, boolean sameCharacter) {
        return true;
    }
}
