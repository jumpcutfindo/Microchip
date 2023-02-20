package com.jumpcutfindo.microchip.constants;

import static com.jumpcutfindo.microchip.MicrochipMod.MOD_ID;

import net.minecraft.util.Identifier;

public class NetworkConstants {
    public static final Identifier PACKET_GLOW_ENTITY_ID = new Identifier(MOD_ID, "glow_entity");
    public static final Identifier PACKET_LOCATE_ENTITY_ID = new Identifier(MOD_ID, "locate_entity");
    public static final Identifier PACKET_TELEPORT_TO_ENTITY_ID = new Identifier(MOD_ID, "teleport_to_entity");
    public static final Identifier PACKET_HEAL_ENTITY_ID = new Identifier(MOD_ID, "heal_entity");
    public static final Identifier PACKET_KILL_ENTITY_ID = new Identifier(MOD_ID, "kill_entity");

    public static final Identifier PACKET_ADD_ENTITY_TO_GROUP_ID = new Identifier(MOD_ID, "add_entity_to_group");
    public static final Identifier PACKET_REMOVE_ENTITY_FROM_GROUP_ID = new Identifier(MOD_ID, "remove_entity_from_group");
    public static final Identifier PACKET_MOVE_ENTITIES_ID = new Identifier(MOD_ID, "move_entities");
    public static final Identifier PACKET_CREATE_GROUP_ID = new Identifier(MOD_ID, "create_group");

    public static final Identifier PACKET_UPDATE_GROUP_ID = new Identifier(MOD_ID, "update_group");
    public static final Identifier PACKET_DELETE_GROUP_ID = new Identifier(MOD_ID, "delete_group");

    public static final Identifier PACKET_REQUEST_ENTITY_DATA_ID = new Identifier(MOD_ID, "request_entity_data");

    public static final Identifier PACKET_UPDATE_ALL_MICROCHIPS_ID = new Identifier(MOD_ID, "update_all_microchips");
}
