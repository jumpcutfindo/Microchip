package com.jumpcutfindo.microchip.data;

import java.util.Objects;
import java.util.UUID;

public class Microchip {
    private UUID entityId;

    public Microchip(UUID entityId) {
        this.entityId = entityId;
    }

    public UUID getEntityId() {
        return entityId;
    }

    @Override
    public String toString() {
        return this.entityId.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return entityId.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityId);
    }
}
