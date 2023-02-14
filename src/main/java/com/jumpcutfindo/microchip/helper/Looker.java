package com.jumpcutfindo.microchip.helper;

import java.util.*;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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
