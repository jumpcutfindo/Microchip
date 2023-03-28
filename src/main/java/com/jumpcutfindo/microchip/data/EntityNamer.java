package com.jumpcutfindo.microchip.data;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;

import java.util.Map;
import java.util.function.Function;

public class EntityNamer {
    /**
     * This map contains a set of Suppliers tagged to specific mobs that allows the mod to retrieve the breed / type of
     * mob. For example, instead of showing "Villager" as the mob type, we will try to show "Librarian" or whatever else
     * based on their profession.
     */
    public static final Map<Class<? extends LivingEntity>, Function<LivingEntity, String>> ENTITY_TYPE_NAMES =
            ImmutableMap.<Class<? extends LivingEntity>, Function<LivingEntity, String>>builder()
                    .put(LivingEntity.class, (entity) -> entity.getType().getName().getString())
                    .put(VillagerEntity.class, (entity -> {
                        VillagerProfession profession = ((VillagerEntity) entity).getVillagerData().getProfession();
                        if (profession == VillagerProfession.NONE) return "Villager";
                        else return profession.toString().substring(0, 1).toUpperCase() + profession.toString().substring(1);
                    }))
                    .build();

    public static String getDisplayName(LivingEntity entity) {
        return entity.getDisplayName().getString();
    }

    public static String getTypeName(LivingEntity entity) {
        if (ENTITY_TYPE_NAMES.containsKey(entity.getClass())) return ENTITY_TYPE_NAMES.get(entity.getClass()).apply(entity);
        else return ENTITY_TYPE_NAMES.get(LivingEntity.class).apply(entity);
    }
}
