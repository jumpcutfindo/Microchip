package com.jumpcutfindo.microchip.screen;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;

import java.util.Map;

public class EntityModelScaler {
    /**
     * This map contains the scale factor for the rendering of the mobs within Microchip related
     * screens and windows.
     *
     * Due to the way that hitboxes and the dimensions of the mobs are being
     * calculated, there are situations where the mob is either too small or too large for the
     * window they're supposed to fit in. The solution is thus to adjust it slightly so that we have
     * a more favorable display of the mobs being rendered.
     */
    private static final Map<Class<? extends LivingEntity>, Float> ENTITY_SCALES = ImmutableMap.<Class<? extends LivingEntity>, Float>builder()
            .put(AxolotlEntity.class, 0.65f)
            .put(ChickenEntity.class, 0.7f)
            .put(CaveSpiderEntity.class, 0.55f)
            .put(CatEntity.class, 0.85f)
            .put(CodEntity.class, 0.9f)
            .put(CowEntity.class, 0.95f)
            .put(DolphinEntity.class, 0.6f)
            .put(DonkeyEntity.class, 0.75f)
            .put(ElderGuardianEntity.class, 0.45f)
            .put(EndermiteEntity.class, 0.8f)
            .put(FoxEntity.class, 0.65f)
            .put(GhastEntity.class, 0.5f)
            .put(GlowSquidEntity.class, 0.5f)
            .put(GoatEntity.class, 0.9f)
            .put(GuardianEntity.class, 0.6f)
            .put(HoglinEntity.class, 0.75f)
            .put(HorseEntity.class, 0.75f)
            .put(LlamaEntity.class, 0.9f)
            .put(MagmaCubeEntity.class, 0.75f)
            .put(MooshroomEntity.class, 0.75f)
            .put(OcelotEntity.class, 0.65f)
            .put(PandaEntity.class, 0.75f)
            .put(ParrotEntity.class, 0.75f)
            .put(PhantomEntity.class, 0.5f)
            .put(PolarBearEntity.class, 0.75f)
            .put(RabbitEntity.class, 0.75f)
            .put(RavagerEntity.class, 0.75f)
            .put(SilverfishEntity.class, 0.5f)
            .put(SkeletonHorseEntity.class, 0.75f)
            .put(SlimeEntity.class, 0.75f)
            .put(TurtleEntity.class, 0.75f)
            .put(WolfEntity.class, 0.85f)
            .put(ZoglinEntity.class, 0.75f)
            .build();

    private static final Map<Class<? extends LivingEntity>, Float> BABY_ENTITY_SCALES = ImmutableMap.<Class<? extends LivingEntity>, Float>builder()
            .put(VillagerEntity.class, 0.75f)
            .put(TurtleEntity.class, 0.75f)
            .build();

    /**
     * This map contains the offsets to be applied to the rendering of the mobs in different windows.
     *
     * This is meant to ensure that after the scaling has been carried out, we properly position the mobs
     * in their relevant frames.
     */
    private static final Map<Class<? extends LivingEntity>, InterfaceOffset> ENTITY_OFFSETS = ImmutableMap.<Class<? extends LivingEntity>, InterfaceOffset>builder()
            .put(AxolotlEntity.class, new InterfaceOffset(2, -3, 0, -4))
            .put(BlazeEntity.class, new InterfaceOffset(-2, 2, -2, 3))
            .put(DolphinEntity.class, new InterfaceOffset(-2, -2, 0, -3))
            .put(ElderGuardianEntity.class, new InterfaceOffset(0, -5, 0, -15))
            .put(GhastEntity.class, new InterfaceOffset(0, -10, 0, -20))
            .put(MagmaCubeEntity.class, new InterfaceOffset(0, -3, 0, 0))
            .put(ParrotEntity.class, new InterfaceOffset(0, -3, 0, 0))
            .build();

    public static float getScaleModifier(LivingEntity entity) {
        if (entity.isBaby()) return BABY_ENTITY_SCALES.getOrDefault(entity.getClass(), 0.5f);
        return ENTITY_SCALES.getOrDefault(entity.getClass(), 1.0f);
    }

    public static InterfaceOffset getInterfaceOffset(LivingEntity entity) {
        return ENTITY_OFFSETS.getOrDefault(entity.getClass(), EntityModelScaler.InterfaceOffset.EMPTY);
    }

    public static class InterfaceOffset {
        public static InterfaceOffset EMPTY = new InterfaceOffset(0, 0, 0, 0);
        private final int listX, listY;
        private final int windowX, windowY;

        public InterfaceOffset(int listX, int listY, int windowX, int windowY) {
            this.listX = listX;
            this.listY = listY;
            this.windowX = windowX;
            this.windowY = windowY;
        }

        public int getListX() {
            return listX;
        }

        public int getListY() {
            return listY;
        }

        public int getWindowX() {
            return windowX;
        }

        public int getWindowY() {
            return windowY;
        }
    }
}
