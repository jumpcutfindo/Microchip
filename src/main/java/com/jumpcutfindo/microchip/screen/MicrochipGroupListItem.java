package com.jumpcutfindo.microchip.screen;

import com.jumpcutfindo.microchip.data.MicrochipGroup;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class MicrochipGroupListItem extends ListItem {
    private final MicrochipGroup microchipGroup;
    private final int index;

    public MicrochipGroupListItem(MicrochipsMenuScreen screen, MicrochipGroup microchipGroup, int index) {
        super(screen, MicrochipGroupListView.TEXTURE, 0, 178, 124, 18);
        this.microchipGroup = microchipGroup;
        this.index = index;
    }

    @Override
    public boolean onClick(int x, int y, double mouseX, double mouseY) {
        if (MicrochipsMenuScreen.isWithin(mouseX, mouseY, x, y, this.width, this.height)) {
            screen.setSelectedGroup(index);
            return true;
        }
        return false;
    }

    @Override
    public void renderContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        screen.getTextRenderer().drawWithShadow(matrices, new LiteralText(this.microchipGroup.getDisplayName()), (float) (x + 4), (float) (y + 5), 0xFFFFFF);
    }
}
