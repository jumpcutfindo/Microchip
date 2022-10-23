package com.jumpcutfindo.microchip;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.data.MicrochipComponents;
import com.jumpcutfindo.microchip.data.Microchips;
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
        LivingEntity entity = Looker.getLookingAt(world, player);

        if (entity == null) {
            LOGGER.info("Failed to tag entity! Are you even looking at one?");
            return false;
        } else {
            LOGGER.info("Found an entity to tag!");
            Microchips microchips = getMicrochips(player);

            boolean added = microchips.addToGroup(microchips.getDefaultGroupId(), new Microchip(entity.getUuid()));
            if (added) entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 2, 1), player);
            return added;
        }
    }
    private static Microchips getMicrochips(PlayerEntity player) {
        return MicrochipComponents.MICROCHIPS.get(player);
    }
}
