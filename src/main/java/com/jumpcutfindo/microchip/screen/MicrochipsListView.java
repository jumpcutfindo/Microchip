package com.jumpcutfindo.microchip.screen;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class MicrochipsListView extends ListView {
    protected static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_list.png");
    private final MicrochipGroup group;

    private final int titleX, titleY;
    private final LiteralText title;
    public MicrochipsListView(MicrochipsMenuScreen screen, MicrochipGroup microchipGroup) {
        super(screen, TEXTURE, 0, 0, 216, 178,
                8, 26, 195, 27, new ArrayList<>());

        // Set various variables
        if (microchipGroup != null)  {
            this.title = new LiteralText(microchipGroup.getDisplayName());
        } else {
            this.title = new LiteralText("");
        }
        this.titleX = 7;
        this.titleY = 9;

        // Create list items for the contents of that group
        this.group = microchipGroup;
        if (this.group != null) this.group.getMicrochips().forEach(microchip -> this.listItems.add(new MicrochipListItem(screen, microchip)));
    }

    @Override
    public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        super.render(matrices, x, y, mouseX, mouseY);

        this.screen.getTextRenderer().draw(matrices, this.title, (float) (x + this.titleX), (float) (y + this.titleY), 0x404040);
    }
}
