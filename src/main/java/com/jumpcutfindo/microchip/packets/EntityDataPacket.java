package com.jumpcutfindo.microchip.packets;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import javax.swing.text.html.Option;
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
        this.entityStatuses = buf.readCollection((size) -> new ArrayList<>(), (packetByteBuf -> StatusEffectInstance.fromNbt(packetByteBuf.readNbt())));

        // Get breeding age
        this.breedingAge = buf.readInt();

        // Get entity's inventory
        RegistryWrapper.WrapperLookup wrapperLookup = BuiltinRegistries.createWrapperLookup();
        NbtCompound inventory = buf.readNbt();
        this.inventoryList = new ArrayList<>();
        if (inventory != null && inventory.contains("Inventory")) {
            NbtList inventoryNbtList = inventory.getList("Inventory", 10);
            inventoryNbtList.forEach(element -> {
                Optional<ItemStack> itemStackOpt =ItemStack.fromNbt(wrapperLookup, (NbtCompound) element);
                itemStackOpt.ifPresent(this.inventoryList::add);
            });
        }

        // Get entity's inventory size
        this.inventorySize = buf.readInt();
    }

    private void write(PacketByteBuf buf) {
        // Write entity statuses
        buf.writeCollection(entityStatuses, (packetByteBuf, statusEffectInstance) -> {
            NbtCompound nbt = new NbtCompound();
            nbt.put("statusEffect", statusEffectInstance.writeNbt());

            packetByteBuf.writeNbt(nbt);
        });

        // Write breeding age
        buf.writeInt(this.breedingAge);

        NbtCompound inventoryNbt = new NbtCompound();
        RegistryWrapper.WrapperLookup wrapperLookup = BuiltinRegistries.createWrapperLookup();

        // Write entity's inventory
        for (ItemStack itemStack : this.inventoryList) {
            itemStack.encode(wrapperLookup, inventoryNbt);
        }

        inventoryNbt.put("Inventory", inventoryNbt);
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
