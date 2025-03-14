package com.jumpcutfindo.microchip.packets;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.jumpcutfindo.microchip.MicrochipMod.MOD_ID;

public class EntityDataPacket implements CustomPayload {
    public static final CustomPayload.Id<EntityDataPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "entity_data"));
    public static final PacketCodec<RegistryByteBuf, EntityDataPacket> PACKET_CODEC = PacketCodec.of(EntityDataPacket::write, EntityDataPacket::new);

    private final Collection<StatusEffectInstance> entityStatuses;
    private final int breedingAge;

    private final List<ItemStack> inventoryList;

    private final int inventorySize;

    public EntityDataPacket(PacketByteBuf buf) {
        // Get entity statuses
        NbtCompound effectCpd = buf.readNbt();
        NbtList effectListNbt = effectCpd.getList("Effects", NbtElement.COMPOUND_TYPE);
        this.entityStatuses = new ArrayList<>();

        effectListNbt.forEach((effectNbt) -> {
            StatusEffectInstance effectInstance = StatusEffectInstance.fromNbt((NbtCompound) effectNbt);
            this.entityStatuses.add(effectInstance);
        });

        // Get breeding age
        this.breedingAge = buf.readInt();

        // Get entity's inventory
        RegistryWrapper.WrapperLookup wrapperLookup = BuiltinRegistries.createWrapperLookup();
        NbtCompound inventoryCpd = buf.readNbt();

        NbtList inventoryItemListNbt = inventoryCpd.getList("Inventory", NbtElement.COMPOUND_TYPE);
        this.inventoryList = new ArrayList<>();

        if (inventoryItemListNbt != null) {
            inventoryItemListNbt.forEach(itemStackNbt -> {
                Optional<ItemStack> itemStack = ItemStack.fromNbt(wrapperLookup, itemStackNbt);
                itemStack.ifPresent(this.inventoryList::add);
            });
        }

        // Get entity's inventory size
        this.inventorySize = buf.readInt();
    }

    private void write(PacketByteBuf buf) {
        // Write entity statuses
        NbtList effectListNbt = new NbtList();
        for (var effect : this.entityStatuses) {
            effectListNbt.add(effect.writeNbt());
        }

        NbtCompound effectsCpd = new NbtCompound();
        effectsCpd.put("Effects", effectListNbt);

        buf.writeNbt(effectsCpd);

        // Write breeding age
        buf.writeInt(this.breedingAge);

        // Write entity's inventory
        NbtCompound inventoryNbt = new NbtCompound();
        RegistryWrapper.WrapperLookup wrapperLookup = BuiltinRegistries.createWrapperLookup();

        NbtList inventoryItemListNbt = new NbtList();
        for (ItemStack itemStack : this.inventoryList) {
            inventoryItemListNbt.add(itemStack.encode(wrapperLookup));
        }
        inventoryNbt.put("Inventory", inventoryItemListNbt);

        buf.writeNbt(inventoryNbt);

        // Write inventory size
        buf.writeInt(this.inventorySize);
    }

    public Collection<StatusEffectInstance> entityStatuses() {
        return entityStatuses;
    }

    public int breedingAge() {
        return breedingAge;
    }

    public List<ItemStack> inventoryList() {
        return inventoryList;
    }

    public int inventorySize() {
        return inventorySize;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
