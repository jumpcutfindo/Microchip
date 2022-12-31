package com.jumpcutfindo.microchip.helper;

import org.slf4j.Logger;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.MicrochipComponents;
import com.jumpcutfindo.microchip.data.Microchips;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class Tagger {
    public static final Logger LOGGER = MicrochipMod.LOGGER;

    public static boolean canTag(PlayerEntity player, LivingEntity entity) {
        Microchips microchips = getMicrochips(player);
        return microchips.getAllMicrochips().stream().noneMatch(microchip -> entity.getUuid().equals(microchip.getEntityId()));
    }

    public static Microchips getMicrochips(PlayerEntity player) {
        return MicrochipComponents.MICROCHIPS.get(player);
    }

}
