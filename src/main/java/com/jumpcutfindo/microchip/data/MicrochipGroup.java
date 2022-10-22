package com.jumpcutfindo.microchip.data;

import java.util.*;

import org.slf4j.Logger;

import com.jumpcutfindo.microchip.MicrochipMod;

public class MicrochipGroup {
    public static Logger LOGGER = MicrochipMod.LOGGER;
    private UUID id;
    private String displayName;
    private Set<Microchip> microchips;

    public MicrochipGroup(String displayName) {
        this.id = UUID.randomUUID();
        this.displayName = displayName;
        this.microchips = new HashSet<>();
    }

    public UUID getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean add(Microchip microchip) {
        LOGGER.info(String.format("Adding Microchip(%s) to group(%s)", microchip, displayName));
        return this.microchips.add(microchip);
    }

    public boolean remove(Microchip microchip) {
        LOGGER.info(String.format("Removing Microchip(%s) from group(%s)", microchip, displayName));
        return this.microchips.remove(microchip);
    }

    public boolean contains(Microchip microchip) {
        return this.microchips.contains(microchip);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MicrochipGroup other && other.getId().equals(this.id);
    }
}
