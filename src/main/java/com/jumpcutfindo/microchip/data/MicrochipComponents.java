package com.jumpcutfindo.microchip.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

public class MicrochipComponents implements EntityComponentInitializer {
    public static final ComponentKey<Microchips> MICROCHIPS =
            ComponentRegistry.getOrCreate(Identifier.of("microchip", "microchips"), Microchips.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(PlayerEntity.class, MICROCHIPS).impl(PlayerMicrochips.class).end(PlayerMicrochips::new);
    }
}
