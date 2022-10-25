package com.jumpcutfindo.microchip.screen;

import com.jumpcutfindo.microchip.data.MicrochipGroup;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class MicrochipGroupListItem extends ListItem {
    private final MicrochipGroup microchipGroup;

    public MicrochipGroupListItem(MicrochipsMenuScreen screen, MicrochipGroup microchipGroup) {
        super(screen, MicrochipGroupListView.TEXTURE, 0, 178, 124, 18);
        this.microchipGroup = microchipGroup;
    }

    @Override
    public void onClick(int mouseX, int mouseY) {

    }

    @Override
    public void renderContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        screen.getTextRenderer().drawWithShadow(matrices, new LiteralText(this.microchipGroup.getDisplayName()), (float) (x + 4), (float) (y + 5), 0xFFFFFF);
    }
}
