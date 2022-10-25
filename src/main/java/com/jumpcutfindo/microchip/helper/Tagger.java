package com.jumpcutfindo.microchip.helper;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.client.ClientNetworker;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.data.MicrochipComponents;
import com.jumpcutfindo.microchip.data.Microchips;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Tagger {
    public static final Logger LOGGER = MicrochipMod.LOGGER;
    public static TagResult tag(World world, PlayerEntity player) {
        LivingEntity entity = Looker.getLookingAt(world, player);

        if (entity == null) {
            return TagResult.NOTHING;
        } else {
            LOGGER.info("Found an entity to tag!");
            Microchips microchips = getMicrochips(player);

            boolean added = microchips.addToGroup(microchips.getDefaultGroupId(), new Microchip(entity.getUuid()));

            if (!added) return TagResult.DUPLICATE;

            if (world.isClient()) {
                ClientNetworker.sendAddEntityToGroupPacket(microchips.getDefaultGroupId(), entity.getUuid());
                ClientNetworker.sendGlowPacket(entity);
            }
            return TagResult.ADDED;
        }
    }

    public static LivingEntity getEntity(World world, Vec3d pos, UUID uuid) {
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, Box.from(pos).expand(256.0d), entity -> entity.getUuid().equals(uuid));
        if (entities.size() == 0) return null;
        else return entities.get(0);
    }

    public static Microchips getMicrochips(PlayerEntity player) {
        return MicrochipComponents.MICROCHIPS.get(player);
    }
}
