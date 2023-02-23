package com.jumpcutfindo.microchip.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public abstract class Microchips implements Component {
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

    public Microchip getMicrochipOf(UUID entityId) {
        MicrochipGroup group = getGroupOf(entityId);
        if (group == null) return null;

        List<Microchip> microchips = group.getMicrochipsWithIds(List.of(entityId));

        if (microchips.size() == 0) return null;
        else return microchips.get(0);
    }

    public MicrochipGroup getGroupOf(UUID entityId) {
        List<MicrochipGroup> groups = getAllGroups().stream().filter(group -> group.getMicrochips().stream().anyMatch(m -> m.getEntityId().equals(entityId))).toList();
        return groups.size() != 0 ?  groups.get(0) : null;
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

    private void updateChipCount() {
        this.chipCount = this.defaultGroup.getMicrochips().size() + this.userGroups.stream().mapToInt(group -> group.getMicrochips().size()).sum();
    }

    public boolean createGroup(String name, GroupColor color) {
        MicrochipGroup group = new MicrochipGroup(name, color);
        groupCount++;
        return userGroups.add(group);
    }

    public boolean updateGroup(UUID groupId, String name, GroupColor color) {
        MicrochipGroup group = null;

        if (this.defaultGroup.getId().equals(groupId)) group = this.defaultGroup;
        else {
            for (MicrochipGroup g : this.userGroups) {
                if (g.getId().equals(groupId)) {
                    group = g;
                    break;
                }
            }
        }

        if (group == null) return false;

        group.setDisplayName(name);
        group.setColor(color);
        return true;
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

    public boolean removeFromGroup(UUID groupId, List<UUID> microchipIds) {
        if (groupId.equals(defaultGroup.getId())) {
            chipCount--;
            return defaultGroup.removeAll(microchipIds);
        }

        for (MicrochipGroup group : this.userGroups) {
            if (group.getId().equals(groupId)) {
                chipCount--;
                return group.removeAll(microchipIds);
            }
        }
        return false;
    }

    public boolean moveBetweenGroups(UUID fromId, UUID toId, List<UUID> microchipIds) {
        Optional<MicrochipGroup> fromGroupOpt = getAllGroups().stream().filter(g -> g.getId().equals(fromId)).findFirst();
        Optional<MicrochipGroup> toGroupOpt = getAllGroups().stream().filter(g -> g.getId().equals(toId)).findFirst();

        if (fromGroupOpt.isEmpty() || toGroupOpt.isEmpty()) return false;

        MicrochipGroup fromGroup = fromGroupOpt.get();
        MicrochipGroup toGroup = toGroupOpt.get();

        List<Microchip> microchips = fromGroup.getMicrochipsWithIds(microchipIds);
        fromGroup.removeAll(microchipIds);
        toGroup.addAll(microchips);

        return true;
    }

    public List<Microchip> getAllMicrochips() {
        List<Microchip> microchips = new ArrayList<>(this.defaultGroup.getMicrochips());
        userGroups.forEach(group -> microchips.addAll(group.getMicrochips()));

        return microchips;
    }

    public abstract void sync();

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

        microchips.updateChipCount();
    }

    public static NbtCompound toNbt(Microchips microchips) {
        Gson gson = new Gson();

        NbtCompound cpd = new NbtCompound();
        cpd.putString("defaultGroup", gson.toJson(microchips.getDefaultGroup()));
        cpd.putString("userGroups", gson.toJson(microchips.getUserGroups()));

        return cpd;
    }
}
