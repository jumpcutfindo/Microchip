package com.jumpcutfindo.microchip.data;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class MicrochipComponents implements EntityComponentInitializer {
    public static final ComponentKey<Microchips> MICROCHIPS =
            ComponentRegistry.getOrCreate(new Identifier("microchip", "microchips"), Microchips.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(PlayerEntity.class, MICROCHIPS).impl(PlayerMicrochips.class).end(PlayerMicrochips::new);
    }
}
