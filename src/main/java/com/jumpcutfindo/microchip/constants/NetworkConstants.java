package com.jumpcutfindo.microchip.constants;

import static com.jumpcutfindo.microchip.MicrochipMod.MOD_ID;

import net.minecraft.util.Identifier;

public class NetworkConstants {
    public static final Identifier PACKET_GLOW_ENTITY_ID = new Identifier(MOD_ID, "glow_entity");

    public static final Identifier PACKET_ADD_ENTITY_TO_GROUP_ID = new Identifier(MOD_ID, "add_entity_to_group");
    public static final Identifier PACKET_REMOVE_ENTITY_FROM_GROUP_ID = new Identifier(MOD_ID, "remove_entity_from_group");
    public static final Identifier PACKET_CREATE_GROUP_ID = new Identifier(MOD_ID, "create_group");
    public static final Identifier PACKET_DELETE_GROUP_ID = new Identifier(MOD_ID, "delete_group");

    public static final Identifier PACKET_REFRESH_SCREEN = new Identifier(MOD_ID, "refresh_screen");
}
