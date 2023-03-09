package com.jumpcutfindo.microchip.client;

import com.jumpcutfindo.microchip.client.network.ClientNetworkSender;
import com.jumpcutfindo.microchip.data.Microchips;
import com.jumpcutfindo.microchip.helper.Looker;
import com.jumpcutfindo.microchip.helper.TagResult;
import com.jumpcutfindo.microchip.helper.Tagger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class ClientTagger {

    public static TagResult tag(PlayerEntity player) {
        List<Entity> entityList = Looker.getLookingAt(player);

        if (entityList.size() == 0 || !(entityList.get(0) instanceof LivingEntity entity)) {
            return TagResult.NOTHING;
        } else {
            Tagger.LOGGER.info("Found an entity to tag!");
            Microchips microchips = Tagger.getMicrochips(player);

            if (Tagger.canTag(player, entity)) {
                ClientNetworkSender.MicrochipsActions.addEntityToGroup(microchips.getDefaultGroup().getId(), entity.getUuid());
                ClientNetworkSender.EntityActions.glowEntity(entity);
            } else {
                return TagResult.DUPLICATE;
            }
            return TagResult.ADDED;
        }
    }

    public static Text getEntityTypeText(LivingEntity entity) {
        Text mobType;
        if (entity instanceof VillagerEntity villager) {
            String profession = villager.getVillagerData().getProfession().id();
            mobType = Text.translatable("entity.minecraft.villager." + profession);
        } else {
            mobType = entity.getType().getName();
        }

        return mobType;
    }

    public static LivingEntity getEntity(World world, Vec3d pos, UUID uuid) {
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, Box.from(pos).expand(256.0d), entity -> entity.getUuid().equals(uuid));
        if (entities.size() == 0) return null;
        else return entities.get(0);
    }
}
