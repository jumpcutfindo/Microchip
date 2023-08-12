package com.jumpcutfindo.microchip.screen.list;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.jumpcutfindo.microchip.helper.SoundUtils;
import com.jumpcutfindo.microchip.helper.StringUtils;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;

import com.jumpcutfindo.microchip.screen.ScreenUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

public class MicrochipGroupListItem extends ListItem<MicrochipGroup> {
    private static final Identifier GROUP_LIST_ITEMS_TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_group_list.png");

    private boolean isReorderable = true, isReordering;
    private BiConsumer<Integer, Integer> moveAction;

    public MicrochipGroupListItem(MicrochipsMenuScreen screen, MicrochipGroup microchipGroup, int index) {
        super(screen, microchipGroup, index);

        this.setBackground(GROUP_LIST_ITEMS_TEXTURE, 0, 178, 124, 18);

        this.item = microchipGroup;
        this.index = index;
    }

    @Override
    public boolean mouseClicked(int x, int y, double mouseX, double mouseY) {
        if (isReorderable && isReordering) {
            int arrowWidth = 9, arrowHeight = 9;
            int upX = x + 99, upY = y + 4;
            int downX = x + 110, downY = y + 4;

            // Offset by 1 as we don't consider default group
            if (ScreenUtils.isWithin(mouseX, mouseY, upX, upY, arrowWidth, arrowHeight)) {
                // Up
                SoundUtils.playClickSound(MinecraftClient.getInstance().getSoundManager());
                this.moveAction.accept(this.index - 1, this.index - 2);
                return true;
            } else if (ScreenUtils.isWithin(mouseX, mouseY, downX, downY, arrowWidth, arrowHeight)) {
                // Down
                SoundUtils.playClickSound(MinecraftClient.getInstance().getSoundManager());
                this.moveAction.accept(this.index - 1, this.index);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseSelected(int x, int y, double mouseX, double mouseY) {
        return !this.isReordering && ScreenUtils.isWithin(mouseX, mouseY, x, y, this.width, this.height);
    }

    public MicrochipGroup getGroup() {
        return this.item;
    }

    @Override
    public void renderBackground(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        ScreenUtils.setShaderColor(this.getGroup().getColor(), false);
        super.renderBackground(matrices, x, y, mouseX, mouseY);

        screen.drawTexture(matrices, x + 1, y + 1, this.item.getColor().ordinal() * 16, 214, 16, 16);
    }

    @Override
    public void renderSelectedBackground(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        screen.drawTexture(matrices, x, y, u, v + height, this.width, this.height);
    }

    @Override
    public void renderContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        String displayName = this.item.getDisplayName();
        screen.getTextRenderer().draw(matrices, new LiteralText(StringUtils.truncatedName(displayName, 14)), (float) (x + 19), (float) (y + 5), this.item.getColor().getShadowColor());

        if (!isReorderable || !isReordering) {
            // Draw microchip count
            int microchipCount = this.item.getMicrochips().size();
            int offset = (Integer.toString(microchipCount).length() - 1) * 6;
            screen.getTextRenderer().draw(matrices, new LiteralText(Integer.toString(microchipCount)), (float) (x + 114 - offset), (float) (y + 5), this.item.getColor().getShadowColor());
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

    public void setReorderable(boolean reorderable) {
        isReorderable = reorderable;
    }

    public void setReordering(boolean reordering) {
        isReordering = reordering;
    }

    public void setMoveAction(BiConsumer<Integer, Integer> moveAction) {
        this.moveAction = moveAction;
    }
}
