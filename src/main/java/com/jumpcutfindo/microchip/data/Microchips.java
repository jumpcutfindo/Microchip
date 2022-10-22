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
    private List<MicrochipGroup> groups;

    public Microchips() {
        this.groups = new ArrayList<>();
    }

    public List<MicrochipGroup> getGroups() {
        return groups;
    }

    private void setGroups(List<MicrochipGroup> groups) {
        this.groups = groups;
    }

    public boolean createGroup(String name) {
        MicrochipGroup group = new MicrochipGroup(name);
        return groups.add(group);
    }

    public boolean deleteGroup(UUID id) {
        return groups.removeIf(group -> group.getId().equals(id));
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

        microchips.setGroups(groups);
    }

    public static NbtCompound toNbt(Microchips microchips) {
        Gson gson = new Gson();

        NbtCompound cpd = new NbtCompound();
        cpd.putString("groups", gson.toJson(microchips.getGroups()));

        return cpd;
    }
}
