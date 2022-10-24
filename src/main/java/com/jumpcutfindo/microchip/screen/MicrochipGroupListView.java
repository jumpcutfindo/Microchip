package com.jumpcutfindo.microchip.screen;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.Microchips;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class MicrochipGroupListView extends ListView {
    protected static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_group_list.png");

    public MicrochipGroupListView(MicrochipsMenuScreen screen, Microchips microchips) {
        super(screen, TEXTURE, 0, 0, 160, 178,
                8, 26, 138, 25, new ArrayList<>());

        microchips.getGroups().forEach(group -> this.listItems.add(new MicrochipGroupListItem(screen, group)));
    }
}
