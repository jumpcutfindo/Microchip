package com.jumpcutfindo.microchip.screen;

import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class MicrochipMenuGroup {
    private MicrochipsMenuScreen screen;
    private MicrochipGroup microchipGroup;
    private boolean isExpanded;

    public MicrochipMenuGroup(MicrochipsMenuScreen screen, MicrochipGroup microchipGroup) {
        this.screen = screen;
        this.microchipGroup = microchipGroup;
    }

    public void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, MicrochipGroupList.TEXTURE_GROUP_LIST_BACKGROUND);

        if (mouseX >= x && mouseX < x + 124 && mouseY >= y && mouseY < y + 18) {
            screen.drawTexture(matrices, x, y, 0, 196, 124, 18);
        } else {
            screen.drawTexture(matrices, x, y, 0, 178, 124, 18);
        }

        screen.getTextRenderer().drawWithShadow(matrices, new LiteralText(this.microchipGroup.getDisplayName()), (float) (x + 4), (float) (y + 4), 0xFFFFFF);
    }

    public boolean toggleExpanded() {
        this.isExpanded = !this.isExpanded;
        return this.isExpanded;
    }
}
