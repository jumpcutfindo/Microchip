package com.jumpcutfindo.microchip.helper;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Looker {
    public static LivingEntity getLookingAt(World world, PlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        if (world == null) return null;
        List<LivingEntity> entities = world
                .getEntitiesByClass(LivingEntity.class, new Box(playerPos).expand(15.0), (entity) -> {
                    return !entity.getUuid().equals(player.getUuid());
                });

        for (LivingEntity entity : entities) {
            Vec3d vec3d = player.getRotationVec(1.0F).normalize();
            Vec3d vec3d2 = new Vec3d(
                    entity.getX() - player.getX(),
                    entity.getEyeY() - player.getEyeY(),
                    entity.getZ() - player.getZ()
            );
            double d = vec3d2.length();
            vec3d2 = vec3d2.normalize();
            double e = vec3d.dotProduct(vec3d2);
            boolean isLooking = e > 1.0 - 0.025 / d && player.canSee(entity);

            if (isLooking) {
                return entity;
            }
        }

        return null;
    }

    public static LivingEntity getEntityByUuid(World world, PlayerEntity player, UUID entityId) {
        List<LivingEntity> entityList = world.getEntitiesByClass(LivingEntity.class, Box.from(player.getPos()).expand(256), entity -> entity.getUuid().equals(entityId));
        if (entityList.size() > 0) return entityList.get(0);
        return null;
    }

}
