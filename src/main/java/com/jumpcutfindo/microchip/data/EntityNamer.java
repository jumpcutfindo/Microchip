package com.jumpcutfindo.microchip.data;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.village.VillagerProfession;

import java.util.Map;
import java.util.Optional;
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
                    .put(WolfEntity.class, EntityNamer::getWolfType)
                    .put(HorseEntity.class, EntityNamer::getHorseType)
                    .put(SheepEntity.class, EntityNamer::getSheepType)
                    .put(TropicalFishEntity.class, EntityNamer::getTropicalFishType)
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
        CatVariant catType = ((CatEntity) entity).getVariant().value();

        if (catType == Registries.CAT_VARIANT.get(CatVariant.TABBY)) return "Tabby";
        else if (catType == Registries.CAT_VARIANT.get(CatVariant.BLACK)) return "Black";
        else if (catType == Registries.CAT_VARIANT.get(CatVariant.RED)) return "Red";
        else if (catType == Registries.CAT_VARIANT.get(CatVariant.SIAMESE)) return "Siamese";
        else if (catType == Registries.CAT_VARIANT.get(CatVariant.BRITISH_SHORTHAIR)) return "British Shorthair";
        else if (catType == Registries.CAT_VARIANT.get(CatVariant.CALICO)) return "Calico";
        else if (catType == Registries.CAT_VARIANT.get(CatVariant.PERSIAN)) return "Persian";
        else if (catType == Registries.CAT_VARIANT.get(CatVariant.RAGDOLL)) return "Ragdoll";
        else if (catType == Registries.CAT_VARIANT.get(CatVariant.WHITE)) return "White";
        else if (catType == Registries.CAT_VARIANT.get(CatVariant.JELLIE)) return "Jellie";
        else if (catType == Registries.CAT_VARIANT.get(CatVariant.ALL_BLACK)) return "All Black";
        else return "Cat";
    }}

    private static String getHorseType(LivingEntity entity) {
        HorseColor color = ((HorseEntity) entity).getVariant();
        return nameWithVariant("Horse", color.name());
    }

    private static String getSheepType(LivingEntity entity) {
        DyeColor color = ((SheepEntity) entity).getColor();
        return nameWithVariant("Sheep", color.name());
    }

    private static String getTropicalFishType(LivingEntity entity) {
        TropicalFishEntity.Variety variety = ((TropicalFishEntity) entity).getVariant();
        String name = variety.asString();
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    private static String getVillagerType(LivingEntity entity) {
        VillagerProfession profession = ((VillagerEntity) entity).getVillagerData().getProfession();
        if (profession == VillagerProfession.NONE) return "Villager";
        else return profession.toString().substring(0, 1).toUpperCase() + profession.toString().substring(1);
    }

    private static String getWolfType(LivingEntity entity) {
        RegistryEntry<WolfVariant> variant = ((WolfEntity) entity).getVariant();
        Optional<RegistryKey<WolfVariant>> keyOptional = variant.getKey();

        if (keyOptional.isPresent()) {
            RegistryKey<WolfVariant> key = keyOptional.get();
            if (key == WolfVariants.DEFAULT || key == WolfVariants.PALE) return "Pale";
            else if (key == WolfVariants.SPOTTED) return "Spotted";
            else if (key == WolfVariants.SNOWY) return "Snowy";
            else if (key == WolfVariants.BLACK) return "Black";
            else if (key == WolfVariants.ASHEN) return "Ashen";
            else if (key == WolfVariants.RUSTY) return "Rusty";
            else if (key == WolfVariants.WOODS) return "Woods";
            else if (key == WolfVariants.CHESTNUT) return "Chestnut";
            else if (key == WolfVariants.STRIPED) return "Striped";
        }

        return "Wolf";
    }

    private static String nameWithVariant(String name, String variant) {
        return String.format("%s (%s%s)", name, variant.substring(0, 1).toUpperCase(), variant.substring(1).toLowerCase());
    }
}
