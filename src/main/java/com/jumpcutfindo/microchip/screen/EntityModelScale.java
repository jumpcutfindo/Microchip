package com.jumpcutfindo.microchip.screen;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;

import java.util.Map;

public class EntityModelScale {
    /**
     * This map contains the scale factor for the rendering of the mobs within Microchip related
     * screens and windows.
     *
     * Due to the way that hitboxes and the dimensions of the mobs are being
     * calculated, there are situations where the mob is either too small or too large for the
     * window they're supposed to fit in. The solution is thus to adjust it slightly so that we have
     * a more favorable display of the mobs being rendered.
     */
    public static final Map<Class<? extends LivingEntity>, Float> ENTITY_SCALES = ImmutableMap.<Class<? extends LivingEntity>, Float>builder()
            .put(AxolotlEntity.class, 0.8f)
            .put(ChickenEntity.class, 0.8f)
            .put(CaveSpiderEntity.class, 0.55f)
            .put(CatEntity.class, 0.85f)
            .put(CodEntity.class, 0.9f)
            .put(CowEntity.class, 0.95f)
            .put(DolphinEntity.class, 0.6f)
            .put(DonkeyEntity.class, 0.75f)
            .put(ElderGuardianEntity.class, 0.4f)
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

    public static final Map<Class<? extends LivingEntity>, Integer> ENTITY_OFFSETS = ImmutableMap.<Class<? extends LivingEntity>, Integer>builder()
            .put(ElderGuardianEntity.class, 10)
            .build();
}
