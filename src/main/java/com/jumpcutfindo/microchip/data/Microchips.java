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
    private static final String DEFAULT_GROUP_NAME = "Uncategorised";
    private MicrochipGroup defaultGroup;
    private List<MicrochipGroup> userGroups;

    private int groupCount = 1, chipCount = 0;
    public Microchips() {
        checkAndCreateDefaultGroup();
        checkAndCreateUserGroups();
    }

    private void checkAndCreateDefaultGroup() {
        if (this.defaultGroup == null) {
            MicrochipGroup defaultGroup = new MicrochipGroup(DEFAULT_GROUP_NAME);
            defaultGroup.setDefault();
            this.defaultGroup = defaultGroup;
        }
    }

    private void checkAndCreateUserGroups() {
        if (this.userGroups == null) {
            this.userGroups = new ArrayList<>();
        }
    }

    public List<MicrochipGroup> getAllGroups() {
        List<MicrochipGroup> groups = new ArrayList<>();
        groups.add(this.defaultGroup);
        groups.addAll(this.userGroups);
        return groups;
    }

    public List<MicrochipGroup> getUserGroups() {
        return userGroups;
    }

    public MicrochipGroup getDefaultGroup() {
        return defaultGroup;
    }

    private void setUserGroups(List<MicrochipGroup> groups) {
        this.userGroups = groups;
        checkAndCreateUserGroups();
        updateGroupCount();
    }

    private void setDefaultGroup(MicrochipGroup group) {
        this.defaultGroup = group;
        checkAndCreateDefaultGroup();
        updateGroupCount();
    }

    private void updateGroupCount() {
        this.groupCount = 1 + (this.userGroups == null ? 0 : this.userGroups.size());
    }

    public boolean createGroup(String name, GroupColor color) {
        MicrochipGroup group = new MicrochipGroup(name, color);
        groupCount++;
        return userGroups.add(group);
    }

    public boolean deleteGroup(UUID id) {
        groupCount--;
        return userGroups.removeIf(group -> group.getId().equals(id));
    }

    public int getChipCount() {
        return chipCount;
    }

    public int getGroupCount() {
        return groupCount;
    }

    public boolean addToGroup(UUID groupId, Microchip microchip) {
        if (groupId.equals(defaultGroup.getId())) {
            chipCount++;
            return defaultGroup.add(microchip);
        }

        for (MicrochipGroup group : this.userGroups) {
            if (group.getId().equals(groupId)) {
                chipCount++;
                return group.add(microchip);
            }
        }
        return false;
    }

    public boolean removeFromGroup(UUID groupId, Microchip microchip) {
        if (groupId.equals(defaultGroup.getId())) {
            chipCount--;
            return defaultGroup.remove(microchip);
        }

        for (MicrochipGroup group : this.userGroups) {
            if (group.getId().equals(groupId)) {
                chipCount--;
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

        MicrochipGroup defaultGroup = gson.fromJson(cpd.getString("defaultGroup"), MicrochipGroup.class);
        microchips.setDefaultGroup(defaultGroup);

        Type groupsType = new TypeToken<List<MicrochipGroup>>(){}.getType();
        List<MicrochipGroup> groups = gson.fromJson(cpd.getString("userGroups"), groupsType);
        microchips.setUserGroups(groups);
    }

    public static NbtCompound toNbt(Microchips microchips) {
        Gson gson = new Gson();

        NbtCompound cpd = new NbtCompound();
        cpd.putString("defaultGroup", gson.toJson(microchips.getDefaultGroup()));
        cpd.putString("userGroups", gson.toJson(microchips.getUserGroups()));

        return cpd;
    }
}
