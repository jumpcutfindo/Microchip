package com.jumpcutfindo.microchip.screen;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class MicrochipsListView extends ListView {
    protected static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_list.png");
    private final MicrochipGroup group;
    public MicrochipsListView(MicrochipsMenuScreen screen, MicrochipGroup microchipGroup) {
        super(screen, TEXTURE, 0, 0, 216, 178,
                7, 26, 195, 27, new ArrayList<>());

        this.group = microchipGroup;
        this.group.getMicrochips().forEach(microchip -> this.listItems.add(new MicrochipListItem(screen, microchip)));
    }
}
