package com.jumpcutfindo.microchip.server.network;

import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.ComponentKey;

import java.util.Collection;
import java.util.Iterator;

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
        effectsCpd.put("Effects", effectListNbt);

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
        NbtCompound entityInventory = new NbtCompound();
        RegistryWrapper.WrapperLookup wrapperLookup = BuiltinRegistries.createWrapperLookup();

        // Populate inventory items, if present
        if (entity instanceof InventoryOwner io) {
            SimpleInventory inventory = io.getInventory();
            entityInventory.put("Inventory", inventory.toNbtList(wrapperLookup));
        }

        buffer.writeNbt(entityInventory);

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
