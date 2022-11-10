package com.jumpcutfindo.microchip.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public abstract class ListItem {
    protected MicrochipsMenuScreen screen;
    private Identifier texture;
    protected int u, v, width, height;
    protected int hoverU, hoverV;
    protected int selectedU, selectedV;

    protected boolean isSelected;

    public ListItem(MicrochipsMenuScreen screen, Identifier texture, int u, int v, int hoverU, int hoverV, int selectedU, int selectedV, int width, int height) {
        this.screen = screen;
        this.texture = texture;

        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;

        this.hoverU = hoverU;
        this.hoverV = hoverV;

        this.selectedU = selectedU;
        this.selectedV = selectedV;
    }

    public int getHeight() {
        return height;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        this.renderBackground(matrices, x, y, mouseX, mouseY);
        this.renderContent(matrices, x, y, mouseX, mouseY);
    }

    public void renderBackground(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, this.texture);

        if (isSelected) {
            // Selected
            screen.drawTexture(matrices, x, y, this.selectedU, this.selectedV, this.width, this.height);
            return;
        }

        if (MicrochipsMenuScreen.isWithin(mouseX, mouseY, x, y, this.width, this.height)) {
            // Hovered
            screen.drawTexture(matrices, x, y, this.hoverU, this.hoverV, this.width, this.height);
        } else {
            // Normal
            screen.drawTexture(matrices, x, y, this.u, this.v, this.width, this.height);
        }
    }

    public abstract void renderContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY);

    public abstract boolean onClick(int x, int y, double mouseX, double mouseY);
}
