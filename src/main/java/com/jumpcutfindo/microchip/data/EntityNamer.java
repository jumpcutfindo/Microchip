package com.jumpcutfindo.microchip.data;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.*;
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
                    .put(LivingEntity.class, EntityNamer::getDefaultType)
                    .put(AxolotlEntity.class, EntityNamer::getAxolotlType)
                    .put(CatEntity.class, EntityNamer::getCatType)
                    .put(HorseEntity.class, EntityNamer::getHorseType)
                    .put(VillagerEntity.class, EntityNamer::getVillagerType)
                    .build();

    public static String getDisplayName(LivingEntity entity) {
        return entity.getDisplayName().getString();
    }

    public static String getTypeName(LivingEntity entity) {
        if (ENTITY_TYPE_NAMES.containsKey(entity.getClass())) return ENTITY_TYPE_NAMES.get(entity.getClass()).apply(entity);
        else return ENTITY_TYPE_NAMES.get(LivingEntity.class).apply(entity);
    }

    private static String getDefaultType(LivingEntity entity) {
        return entity.getType().getName().getString();
    }

    private static String getAxolotlType(LivingEntity entity) {
        AxolotlEntity.Variant variant = ((AxolotlEntity) entity).getVariant();
        return nameWithVariant("Axolotl", variant.getName());
    }

    private static String getCatType(LivingEntity entity) {{
        int catType = ((CatEntity) entity).getCatType();
        switch (catType) {
            case 0: return "Tabby";
            case 1: return "Black";
            case 2: return "Red";
            case 3: return "Siamese";
            case 4: return "British Shorthair";
            case 5: return "Calico";
            case 6: return "Persian";
            case 7: return "Ragdoll";
            case 8: return "White";
            case 9: return "Jellie";
            case 10: return "All Black";
            default: return "Cat";
        }
    }}

    private static String getHorseType(LivingEntity entity) {
        HorseColor color = ((HorseEntity) entity).getColor();
        return nameWithVariant("Horse", color.name());
    }

    private static String getVillagerType(LivingEntity entity) {
        VillagerProfession profession = ((VillagerEntity) entity).getVillagerData().getProfession();
        if (profession == VillagerProfession.NONE) return "Villager";
        else return profession.toString().substring(0, 1).toUpperCase() + profession.toString().substring(1);
    }

    private static String nameWithVariant(String name, String variant) {
        return String.format("%s (%s%s)", name, variant.substring(0, 1).toUpperCase(), variant.substring(1).toLowerCase());
    }
}
