package com.jumpcutfindo.microchip.data;

import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerMicrochips extends Microchips implements PlayerComponent<Microchips>  {
    protected PlayerEntity owner;

    public PlayerMicrochips(PlayerEntity owner) {
        this.owner = owner;
    }
}
