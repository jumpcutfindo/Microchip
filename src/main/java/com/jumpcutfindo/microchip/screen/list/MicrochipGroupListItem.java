package com.jumpcutfindo.microchip.screen.list;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;

import com.jumpcutfindo.microchip.screen.ScreenUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class MicrochipGroupListItem extends ListItem {
    private static final Identifier GROUP_LIST_ITEMS_TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_group_list.png");
    private final MicrochipGroup microchipGroup;
    private final int index;

    public MicrochipGroupListItem(MicrochipsMenuScreen screen, MicrochipGroup microchipGroup, int index) {
        super(screen);

        this.setBackground(GROUP_LIST_ITEMS_TEXTURE, 0, 178, 124, 18)
                .setHoveredCoords(0, 178)
                .setSelectedCoords(0, 196);

        this.microchipGroup = microchipGroup;
        this.index = index;
    }

    @Override
    public boolean mouseClicked(int x, int y, double mouseX, double mouseY) {
        return false;
    }

    @Override
    public boolean mouseSelected(int x, int y, double mouseX, double mouseY) {
        return ScreenUtils.isWithin(mouseX, mouseY, x, y, this.width, this.height);
    }

    public MicrochipGroup getGroup() {
        return this.microchipGroup;
    }

    @Override
    public void renderBackground(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        ScreenUtils.setShaderColor(this.getGroup().getColor(), false);
        super.renderBackground(matrices, x, y, mouseX, mouseY);

        screen.drawTexture(matrices, x + 1, y + 1, this.microchipGroup.getColor().ordinal() * 16, 214, 16, 16);
    }

    @Override
    public void renderContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        screen.getTextRenderer().draw(matrices, new LiteralText(this.microchipGroup.getDisplayName()), (float) (x + 19), (float) (y + 5), this.microchipGroup.getColor().getShadowColor());
    }
}
