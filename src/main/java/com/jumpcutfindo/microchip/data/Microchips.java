package com.jumpcutfindo.microchip.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.NbtCompound;

public class Microchips implements Component {
    private UUID defaultGroupId;
    private List<MicrochipGroup> groups;
    public Microchips() {
        this.groups = new ArrayList<>();

        MicrochipGroup defaultGroup = new MicrochipGroup("No category");
        this.groups.add(defaultGroup);
        this.defaultGroupId = defaultGroup.getId();
    }

    public UUID getDefaultGroupId() {
        return defaultGroupId;
    }

    private void setDefaultGroupId(UUID id) {
        this.defaultGroupId = id;
    }

    public List<MicrochipGroup> getGroups() {
        return groups;
    }

    private void setGroups(List<MicrochipGroup> groups) {
        this.groups = groups;
    }

    public boolean createGroup(String name, GroupColor color) {
        MicrochipGroup group = new MicrochipGroup(name, color);
        return groups.add(group);
    }

    public boolean deleteGroup(UUID id) {
        return groups.removeIf(group -> group.getId().equals(id));
    }

    public boolean addToGroup(UUID groupId, Microchip microchip) {
        for (MicrochipGroup group : this.groups) {
            if (group.getId().equals(groupId)) {
                return group.add(microchip);
            }
        }
        return false;
    }

    public boolean removeFromGroup(UUID groupId, Microchip microchip) {
        for (MicrochipGroup group : this.groups) {
            if (group.getId().equals(groupId)) {
                return group.remove(microchip);
            }
        }
        return false;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        Gson gson = new Gson();

        NbtCompound cpd = tag.getCompound("microchips");
        Microchips.fromNbt(cpd, this);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtCompound cpd = Microchips.toNbt(this);
        tag.put("microchips", cpd);
    }

    public static void fromNbt(NbtCompound cpd, Microchips microchips) {
        Gson gson = new Gson();
        Type groupsType = new TypeToken<List<MicrochipGroup>>(){}.getType();
        List<MicrochipGroup> groups = gson.fromJson(cpd.getString("groups"), groupsType);

        microchips.setDefaultGroupId(cpd.getUuid("defaultGroup"));
        microchips.setGroups(groups);
    }

    public static NbtCompound toNbt(Microchips microchips) {
        Gson gson = new Gson();

        NbtCompound cpd = new NbtCompound();
        cpd.putUuid("defaultGroup", microchips.getDefaultGroupId());
        cpd.putString("groups", gson.toJson(microchips.getGroups()));

        return cpd;
    }
}
