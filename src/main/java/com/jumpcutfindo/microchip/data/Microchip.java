package com.jumpcutfindo.microchip.data;

import net.minecraft.entity.LivingEntity;

import java.util.Objects;
import java.util.UUID;

public class Microchip {
    private final UUID entityId;
    private MicrochipEntityData entityData;

    private Microchip(UUID entityId, MicrochipEntityData entityData) {
        this.entityId = entityId;
        this.entityData = entityData;
    }
    public UUID getEntityId() {
        return entityId;
    }

    public MicrochipEntityData getEntityData() {
        return entityData;
    }

    public void setEntityData(MicrochipEntityData entityData) {
        this.entityData = entityData;
    }

    @Override
    public String toString() {
        return this.entityId.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Microchip other && other.getEntityId().equals(this.entityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityId);
    }

    public static Microchip of(LivingEntity entity) {
        return new Microchip(entity.getUuid(), MicrochipEntityData.from(entity));
    }
}
