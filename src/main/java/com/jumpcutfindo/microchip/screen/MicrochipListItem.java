package com.jumpcutfindo.microchip.screen;

import com.jumpcutfindo.microchip.data.Microchip;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class MicrochipListItem extends ListItem {
    public MicrochipListItem(MicrochipsMenuScreen screen, Microchip microchip) {
        super(screen, MicrochipsListView.TEXTURE, 0, 178, 180, 18);
    }

    @Override
    public void renderContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {

    }
}
