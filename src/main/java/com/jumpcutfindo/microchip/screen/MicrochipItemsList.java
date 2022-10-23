package com.jumpcutfindo.microchip.screen;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class MicrochipItemsList {
    protected static final Identifier TEXTURE_MICROCHIP_LIST_BACKGROUND = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_list.png");
    private final MicrochipsMenuScreen screen;
    private final int width, height;
    private MicrochipGroup group;

    public MicrochipItemsList(MicrochipsMenuScreen screen) {
        this.screen = screen;

        this.width = 216;
        this.height = 178;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setGroup(MicrochipGroup group) {
        this.group = group;
    }

    public void drawBackground(MatrixStack matrices, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE_MICROCHIP_LIST_BACKGROUND);
        screen.drawTexture(matrices, x, y, 0, 0, this.width, this.height);
    }
}
