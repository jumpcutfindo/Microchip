package com.jumpcutfindo.microchip.data;

import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.UUID;

public class PlayerMicrochips extends Microchips {
    protected PlayerEntity owner;

    public PlayerMicrochips(PlayerEntity owner) {
        this.owner = owner;
    }

    @Override
    public boolean createGroup(String name, GroupColor color) {
        boolean flag = super.createGroup(name, color);
        return flag;
    }

    @Override
    public boolean updateGroup(UUID groupId, String name, GroupColor color) {
        boolean flag = super.updateGroup(groupId, name, color);
        return flag;
    }

    @Override
    public boolean deleteGroup(UUID id) {
        boolean flag = super.deleteGroup(id);
        return flag;
    }

    @Override
    public boolean addToGroup(UUID groupId, Microchip microchip) {
        boolean flag = super.addToGroup(groupId, microchip);
        return flag;
    }

    @Override
    public boolean removeFromGroup(UUID groupId, List<UUID> microchipIds) {
        boolean flag = super.removeFromGroup(groupId, microchipIds);
        return flag;
    }

    @Override
    public boolean moveBetweenGroups(UUID fromId, UUID toId, List<UUID> microchipIds) {
        boolean flag = super.moveBetweenGroups(fromId, toId, microchipIds);
        return flag;
    }

    @Override
    public void sync() {
    }
}
