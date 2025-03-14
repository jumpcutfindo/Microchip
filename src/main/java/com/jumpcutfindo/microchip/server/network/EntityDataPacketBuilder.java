package com.jumpcutfindo.microchip.server.network;

import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;

import java.util.Collection;

public class EntityDataPacketBuilder {
    private final LivingEntity entity;
    private final PacketByteBuf buffer;

    public EntityDataPacketBuilder(PacketByteBuf buf, LivingEntity entity) {
        this.entity = entity;
        this.buffer = buf;
    }

    public EntityDataPacketBuilder withStatusEffects() {
        Collection<StatusEffectInstance> effects = entity.getStatusEffects();

        NbtList effectListNbt = new NbtList();
        for (var effect : effects) {
            effectListNbt.add(effect.writeNbt());
        }

        NbtCompound effectsCpd = new NbtCompound();
        effectsCpd.put("effects", effectListNbt);

        buffer.writeNbt(effectsCpd);

        return this;
    }

    public EntityDataPacketBuilder withBreedingCooldown() {
        if (entity instanceof PassiveEntity passiveEntity) {
            buffer.writeInt(passiveEntity.getBreedingAge());
        } else {
            buffer.writeInt(0);
        }

        return this;
    }

    public EntityDataPacketBuilder withInventory() {
        NbtCompound entityNbt = entity.writeNbt(new NbtCompound());
        NbtCompound inventoryNbt = new NbtCompound();
        NbtList itemListNbt = new NbtList();

        if (entityNbt.contains("Inventory")) itemListNbt = entityNbt.getList("Inventory", 10);
        else if (entityNbt.contains("Items")) itemListNbt = entityNbt.getList("Items", 10);

        inventoryNbt.put("Inventory", itemListNbt);
        buffer.writeNbt(inventoryNbt);

        return this;
    }

    public EntityDataPacketBuilder withInventorySize() {
        int inventorySize = 16;
        if (entity instanceof InventoryOwner) inventorySize = ((InventoryOwner) entity).getInventory().size();
        else if (entity instanceof AbstractHorseEntity) inventorySize = 15;
        buffer.writeInt(inventorySize);

        return this;
    }

    public PacketByteBuf build() {
        return this.buffer;
    }
}
