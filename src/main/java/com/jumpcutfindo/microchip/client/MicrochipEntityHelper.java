package com.jumpcutfindo.microchip.client;

import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.data.MicrochipComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Consumer;

/**
 * Helper class for Microchip entities -- attempts to retrieve all entities of Microchips locally first before sending
 * a packet to the server to retrieve from that end.
 */
public class MicrochipEntityHelper {
    public MicrochipEntityHelper() {
    }

    public void populateWithEntity(PlayerEntity player, Microchip microchip, Consumer<LivingEntity> consumer) {
        LivingEntity entity = ClientTagger.getEntity(player.getWorld(), player.getPos(), microchip.getEntityId());

        if (entity != null) {
            consumer.accept(entity);
        }
    }
}
