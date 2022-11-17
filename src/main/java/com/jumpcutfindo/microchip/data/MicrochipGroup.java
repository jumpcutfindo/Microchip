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

    public String getDisplayName() {
        return displayName;
    }

    public GroupColor getColor() {
        return color == null ? GroupColor.GRAY : color;
    }

    public Collection<Microchip> getMicrochips() {
        return this.microchips;
    }

    public boolean add(Microchip microchip) {
        LOGGER.info(String.format("Adding Microchip(%s) to group(%s)", microchip, displayName));
        return this.microchips.add(microchip);
    }

    public boolean remove(UUID microchipId) {
        LOGGER.info(String.format("Removing Microchip(%s) from group(%s)", microchipId, displayName));
        return this.microchips.removeIf(mc -> mc.getEntityId().equals(microchipId));
    }

    public boolean remove(List<UUID> microchipIds) {
        boolean flag = true;
        for (UUID uuid : microchipIds) flag &= this.remove(uuid);
        return flag;
    }

    public boolean contains(Microchip microchip) {
        return this.microchips.contains(microchip);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MicrochipGroup other && other.getId().equals(this.id);
    }
}
