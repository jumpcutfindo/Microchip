package com.jumpcutfindo.microchip.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.nbt.NbtCompound;
import org.ladysnake.cca.api.v3.component.Component;

import java.lang.reflect.Type;
import java.util.*;

public abstract class Microchips implements Component {
    private static final String DEFAULT_GROUP_NAME = "Uncategorised";
    private MicrochipGroup defaultGroup;
    private List<MicrochipGroup> userGroups;

    private Map<UUID, Microchip> idMap;
    private Map<Microchip, MicrochipGroup> groupMap;

    private int groupCount = 1, chipCount = 0;
    public Microchips() {
        checkAndCreateDefaultGroup();
        checkAndCreateUserGroups();
        createHelperObjects();
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

    /**
     * Creates a bunch of helper objects that are meant to improve querying of different items
     */
    private void createHelperObjects() {
        idMap = new HashMap<>();
        groupMap = new HashMap<>();

        getAllGroups().forEach(group -> {
            group.getMicrochips().forEach(microchip -> {
                idMap.put(microchip.getEntityId(), microchip);
                groupMap.put(microchip, group);
            });
        });
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

    public MicrochipGroup getGroup(UUID groupId) {
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

        return group;
    }

    public MicrochipGroup getGroupOfEntity(UUID entityId) {
        Microchip microchip = idMap.get(entityId);
        if (microchip == null) return null;

        return groupMap.get(microchip);
    }

    public Microchip getMicrochipOf(UUID entityId) {
        return idMap.get(entityId);
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
        MicrochipGroup group = getGroup(groupId);

        if (group == null) return false;

        group.setDisplayName(name);
        group.setColor(color);
        return true;
    }

    public boolean deleteGroup(UUID groupId) {
        MicrochipGroup group = getGroup(groupId);
        if (group == null) return false;

        groupCount--;
        group.getMicrochips().forEach(microchip -> {
            idMap.remove(microchip.getEntityId());
            groupMap.remove(microchip);
        });

        return userGroups.remove(group);
    }

    public void reorderGroup(int from, int to) {
        if (from < 0 || to < 0 || from >= this.userGroups.size() || to >= this.userGroups.size()) return;

        Collections.swap(this.userGroups, from, to);
    }

    public void reorderMicrochips(UUID groupId, int from, int to) {
        MicrochipGroup group = getGroup(groupId);
        if (group == null) return;
        if (from < 0 || to < 0 || from >= group.getMicrochips().size() || to >= group.getMicrochips().size()) return;

        Collections.swap(group.getMicrochips(), from, to);
    }

    public int getChipCount() {
        return chipCount;
    }

    public int getGroupCount() {
        return groupCount;
    }

    public boolean addToGroup(UUID groupId, Microchip microchip) {
        MicrochipGroup group = getGroup(groupId);
        if (group == null) return false;
        idMap.put(microchip.getEntityId(), microchip);
        groupMap.put(microchip, group);
        return group.add(microchip);
    }

    public boolean removeFromGroup(UUID groupId, List<UUID> microchipIds) {
        MicrochipGroup group = getGroup(groupId);
        if (group == null) return false;
        microchipIds.forEach(id -> {
            Microchip microchip = idMap.remove(id);
            groupMap.remove(microchip);
        });
        return group.removeAll(microchipIds);
    }

    public boolean moveBetweenGroups(UUID fromId, UUID toId, List<UUID> microchipIds) {
        MicrochipGroup fromGroup = getGroup(fromId);
        MicrochipGroup toGroup = getGroup(toId);

        if (fromGroup == null || toGroup == null) return false;

        fromGroup.getMicrochips().forEach(microchip -> {
            groupMap.remove(microchip);
            groupMap.put(microchip, toGroup);
        });

        List<Microchip> microchips = fromGroup.getMicrochipsWithIds(microchipIds);
        fromGroup.removeAll(microchipIds);
        toGroup.addAll(microchips);

        return true;
    }

    public List<Microchip> getAllMicrochips() {
        return idMap.values().stream().toList();
    }

    public abstract void sync();

    public static void fromNbt(NbtCompound cpd, Microchips microchips) {
        Gson gson = new Gson();

        MicrochipGroup defaultGroup = gson.fromJson(cpd.getString("defaultGroup"), MicrochipGroup.class);
        microchips.setDefaultGroup(defaultGroup);

        Type groupsType = new TypeToken<List<MicrochipGroup>>(){}.getType();
        List<MicrochipGroup> groups = gson.fromJson(cpd.getString("userGroups"), groupsType);
        microchips.setUserGroups(groups);

        microchips.updateChipCount();
        microchips.createHelperObjects();
    }

    public static NbtCompound toNbt(Microchips microchips) {
        Gson gson = new Gson();

        NbtCompound cpd = new NbtCompound();
        cpd.putString("defaultGroup", gson.toJson(microchips.getDefaultGroup()));
        cpd.putString("userGroups", gson.toJson(microchips.getUserGroups()));

        return cpd;
    }
}
