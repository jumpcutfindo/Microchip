package com.jumpcutfindo.microchip.data;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.helper.Looker;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class Tagger {
    public static final Logger LOGGER = MicrochipMod.LOGGER;
    public static boolean tag(World world, PlayerEntity player) {
        LOGGER.debug("Attempting to tag an entity...");

        LivingEntity entity = Looker.getLookingAt(world, player);

        if (entity == null) {
            LOGGER.debug("Failed to tag entity! Are you even looking at one?");
            return false;
        } else {
            LOGGER.debug(String.format("We'll be tagging the entity(%s) you're looking at!", entity.getUuid().toString()));
            Microchips microchips = getMicrochips(player);

            boolean added = microchips.addToGroup(microchips.getDefaultGroupId(), new Microchip(entity.getUuid()));
            if (added) entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 2, 1), player);
            return added;
        }
    }
    private static Microchips getMicrochips(PlayerEntity player) {
        Microchips microchips = MicrochipComponents.MICROCHIPS.get(player);
        return microchips;
    }
}
