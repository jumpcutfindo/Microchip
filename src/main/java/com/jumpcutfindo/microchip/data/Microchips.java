package com.jumpcutfindo.microchip.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Microchips {
    private List<MicrochipGroup> groups;

    public Microchips() {
        this.groups = new ArrayList<>();
    }

    public boolean createGroup(String name) {
        MicrochipGroup group = new MicrochipGroup(name);
        return groups.add(group);
    }

    public boolean deleteGroup(UUID id) {
        return groups.removeIf(group -> group.getId().equals(id));
    }
}
