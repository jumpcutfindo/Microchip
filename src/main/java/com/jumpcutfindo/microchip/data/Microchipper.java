package com.jumpcutfindo.microchip.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class Microchipper {
    public static Microchips getMicrochips(PlayerEntity playerEntity) {
        Microchips microchips = MicrochipsComponentInitializer.MICROCHIPS.get(playerEntity);
        return microchips;
    }
}
