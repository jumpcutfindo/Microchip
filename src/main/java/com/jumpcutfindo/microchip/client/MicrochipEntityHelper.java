package com.jumpcutfindo.microchip.client;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Consumer;

/**
 * Helper class for Microchip entities -- attempts to retrieve all entities of Microchips locally first before sending
 * a packet to the server to retrieve from that end.
 */
public class MicrochipEntityHelper {
    private final Map<UUID, Consumer<LivingEntity>> waitingConsumers = new HashMap<>();
    public MicrochipEntityHelper() {
    }

    public void populateWithEntity(World world, Vec3d pos, UUID uuid, Consumer<LivingEntity> consumer) {
        LivingEntity entity = ClientTagger.getEntity(world, pos, uuid);

        if (entity != null) consumer.accept(entity);
        else {
            waitingConsumers.put(uuid, consumer);
        }
    }

    public void clearWaitingConsumers() {
        Set<UUID> entityIds = waitingConsumers.keySet();
    }
}
