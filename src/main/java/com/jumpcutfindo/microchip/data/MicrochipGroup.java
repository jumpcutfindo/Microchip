package com.jumpcutfindo.microchip.data;

import java.util.*;

import org.slf4j.Logger;

import com.jumpcutfindo.microchip.MicrochipMod;

public class MicrochipGroup {
    public static Logger LOGGER = MicrochipMod.LOGGER;
    private UUID id;
    private String displayName;
    private GroupColor color;
    private Set<Microchip> microchips;
    private boolean isDefault;

    public MicrochipGroup(String displayName, GroupColor color) {
        this.id = UUID.randomUUID();
        this.displayName = displayName;
        this.microchips = new HashSet<>();
        this.color = color;
    }
    public MicrochipGroup(String displayName) {
        this(displayName, GroupColor.GRAY);
    }

    protected void setDefault() {
        this.isDefault = true;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public UUID getId() {
        return id;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setColor(GroupColor color) {
        this.color = color;
    }

    public GroupColor getColor() {
        return color == null ? GroupColor.GRAY : color;
    }

    public List<Microchip> getMicrochips() {
        return this.microchips.stream().toList();
    }

    public List<Microchip> getMicrochipsWithIds(List<UUID> ids) {
        return this.microchips.stream().filter(microchip -> ids.contains(microchip.getEntityId())).toList();
    }

    public boolean add(Microchip microchip) {
        LOGGER.info(String.format("Adding Microchip(%s) to group(%s)", microchip, displayName));
        return this.microchips.add(microchip);
    }

    public boolean addAll(List<Microchip> microchips) {
        boolean flag = true;
        for (Microchip microchip : microchips) flag &= this.add(microchip);
        return flag;
    }

    public boolean remove(UUID microchipId) {
        LOGGER.info(String.format("Removing Microchip(%s) from group(%s)", microchipId, displayName));
        return this.microchips.removeIf(mc -> mc.getEntityId().equals(microchipId));
    }

    public boolean removeAll(List<UUID> microchipIds) {
        boolean flag = true;
        for (UUID uuid : microchipIds) flag &= this.remove(uuid);
        return flag;
    }

    public boolean contains(Microchip microchip) {
        return this.microchips.contains(microchip);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MicrochipGroup other
                && other.getId().equals(this.id)
                && other.getColor().equals(this.color)
                && other.getDisplayName().equals(this.displayName)
                && other.getMicrochips().size() == this.microchips.size();
    }
}
