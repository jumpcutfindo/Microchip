package com.jumpcutfindo.microchip.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public abstract class ListItem {
    protected MicrochipsMenuScreen screen;
    private Identifier texture;
    private int u, v, width, height;

    public ListItem(MicrochipsMenuScreen screen, Identifier texture, int u, int v, int width, int height) {
        this.screen = screen;
        this.texture = texture;

        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        this.renderBackground(matrices, x, y);
        this.renderContent(matrices, x, y, mouseX, mouseY);
    }

    private void renderBackground(MatrixStack matrices, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, this.texture);
        screen.drawTexture(matrices, x, y, this.u, this.v, this.width, this.height);
    }

    public abstract void renderContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY);
}
