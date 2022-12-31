package com.jumpcutfindo.microchip.data;

import net.minecraft.entity.LivingEntity;

public class MicrochipEntityData {
    private String displayName = "", typeName = "";
    private float maxHealth;

    private double x, y, z;

    public MicrochipEntityData() {
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTypeName() {
        return typeName;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public static MicrochipEntityData from(LivingEntity entity) {
        MicrochipEntityData data = new MicrochipEntityData();

        data.displayName = entity.getDisplayName().getString();
        data.typeName = entity.getType().getName().getString();

        data.maxHealth = entity.getMaxHealth();

        data.x = entity.getX();
        data.y = entity.getY();
        data.z = entity.getZ();

        return data;
    }
}
