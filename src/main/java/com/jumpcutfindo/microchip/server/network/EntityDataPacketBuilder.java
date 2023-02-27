package com.jumpcutfindo.microchip.server.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

public class EntityDataPacketBuilder {
    private ServerPlayerEntity player;
    private LivingEntity entity;
    private final PacketByteBuf buffer;
    public EntityDataPacketBuilder(ServerPlayerEntity player, LivingEntity entity) {
        this.player = player;
        this.entity = entity;

        this.buffer = PacketByteBufs.create();
    }

    public EntityDataPacketBuilder withStatusEffects() {
        Collection<StatusEffectInstance> effects = entity.getStatusEffects();
        buffer.writeCollection(effects, ((packetByteBuf, statusEffectInstance) -> {
            NbtCompound nbt = new NbtCompound();
            nbt = statusEffectInstance.writeNbt(nbt);

            packetByteBuf.writeNbt(nbt);
        }));

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
        else if (entity instanceof HorseBaseEntity) inventorySize = 15;
        buffer.writeInt(inventorySize);

        return this;
    }

    public PacketByteBuf build() {
        return this.buffer;
    }
}
