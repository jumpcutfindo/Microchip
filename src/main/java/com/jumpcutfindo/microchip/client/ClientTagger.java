package com.jumpcutfindo.microchip.client;

import com.jumpcutfindo.microchip.helper.Looker;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class ClientTagger {
    private ClientWorld world;
    private ClientPlayerEntity player;

    public ClientTagger(ClientWorld world, ClientPlayerEntity player) {
        this.world = world;
        this.player = player;
    }

    public boolean tag() {
        LivingEntity target = this.getTarget();

        // No target
        if (target == null) return false;

        return true;
    }

    private LivingEntity getTarget() {
        return Looker.getLookingAt(world, player);
    }
}
