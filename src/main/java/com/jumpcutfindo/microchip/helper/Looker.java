package com.jumpcutfindo.microchip.helper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.ArrayList;
import java.util.List;

public class Looker {
    public static List<Entity> getLookingAt(PlayerEntity player) {
        if (player.getWorld().isClient()) {
            if (MinecraftClient.getInstance().crosshairTarget != null && MinecraftClient.getInstance().crosshairTarget.getType() == HitResult.Type.ENTITY) {
                return List.of(((EntityHitResult) MinecraftClient.getInstance().crosshairTarget).getEntity());
            }
        }

        return new ArrayList<>();
    }
}
