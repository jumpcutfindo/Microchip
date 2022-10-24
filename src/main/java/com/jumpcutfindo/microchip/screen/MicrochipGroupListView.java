package com.jumpcutfindo.microchip.screen;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.Microchips;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class MicrochipGroupListView extends ListView {
    protected static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_group_list.png");
    private final TranslatableText title;
    private final int titleX, titleY;

    public MicrochipGroupListView(MicrochipsMenuScreen screen, Microchips microchips) {
        super(screen, TEXTURE, 0, 0, 160, 178,
                8, 26, 138, 25, new ArrayList<>());

        // Set various variables
        this.title = new TranslatableText("microchip.gui.groupTitle");
        this.titleX = 7;
        this.titleY = 9;

        // Create items for list view
        microchips.getGroups().forEach(group -> this.listItems.add(new MicrochipGroupListItem(screen, group)));
    }
    @Override
    public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        super.render(matrices, x, y, mouseX, mouseY);

        this.screen.getTextRenderer().draw(matrices, this.title, (float) (x + this.titleX), (float) (y + this.titleY), 0x404040);
    }
}
