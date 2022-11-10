package com.jumpcutfindo.microchip.screen;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.MicrochipGroup;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class MicrochipGroupListItem extends ListItem {
    private static final Identifier GROUP_LIST_ITEMS_TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_group_list_items.png");
    private final MicrochipGroup microchipGroup;
    private final int index;

    public MicrochipGroupListItem(MicrochipsMenuScreen screen, MicrochipGroup microchipGroup, int index) {
        super(screen, GROUP_LIST_ITEMS_TEXTURE,
                0, calculateV(microchipGroup, 18),
                0, calculateV(microchipGroup, 18),
                124, calculateV(microchipGroup, 18),
                124, 18
        );
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

    private static int calculateV(MicrochipGroup group, int height) {
        return group.getColor().ordinal() * height;
    }
}
