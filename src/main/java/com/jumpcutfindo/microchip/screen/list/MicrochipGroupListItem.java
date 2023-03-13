package com.jumpcutfindo.microchip.screen.list;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.jumpcutfindo.microchip.helper.StringUtils;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;

import com.jumpcutfindo.microchip.screen.ScreenUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class MicrochipGroupListItem extends ListItem {
    private static final Identifier GROUP_LIST_ITEMS_TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_group_list.png");
    private final MicrochipGroup microchipGroup;
    private final int index;

    private boolean isReordering;

    public MicrochipGroupListItem(MicrochipsMenuScreen screen, MicrochipGroup microchipGroup, int index) {
        super(screen);

        this.setBackground(GROUP_LIST_ITEMS_TEXTURE, 0, 178, 124, 18);

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
    public void renderSelectedBackground(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        screen.drawTexture(matrices, x, y, u, v + height, this.width, this.height);
    }

    @Override
    public void renderContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        String displayName = this.microchipGroup.getDisplayName();
        screen.getTextRenderer().draw(matrices, new LiteralText(StringUtils.truncatedName(displayName, 14)), (float) (x + 19), (float) (y + 5), this.microchipGroup.getColor().getShadowColor());

        if (!isReordering) {
            // Draw microchip count
            int microchipCount = microchipGroup.getMicrochips().size();
            int offset = (Integer.toString(microchipCount).length() - 1) * 6;
            screen.getTextRenderer().draw(matrices, new LiteralText(Integer.toString(microchipCount)), (float) (x + 114 - offset), (float) (y + 5), this.microchipGroup.getColor().getShadowColor());
        } else {
            // Draw reordering arrows
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, GROUP_LIST_ITEMS_TEXTURE);
            ScreenUtils.setShaderColor(this.getGroup().getColor(), false);

            int arrowWidth = 9, arrowHeight = 9;
            int upX = x + 99, upY = y + 4;
            int downX = x + 110, downY = y + 4;

            if (ScreenUtils.isWithin(mouseX, mouseY, upX, upY, arrowWidth, arrowHeight)) {
                screen.drawTexture(matrices, upX, upY, 169, 15, arrowWidth, arrowHeight);
            } else {
                screen.drawTexture(matrices, upX, upY, 160, 15, arrowWidth, arrowHeight);
            }

            if (ScreenUtils.isWithin(mouseX, mouseY, downX, downY, arrowWidth, arrowHeight)) {
                screen.drawTexture(matrices, downX, downY, 169, 24, arrowWidth, arrowHeight);
            } else {
                screen.drawTexture(matrices, downX, downY, 160, 24, arrowWidth, arrowHeight);
            }

        }
    }

    public void setReordering(boolean reordering) {
        isReordering = reordering;
    }
}
