package com.jumpcutfindo.microchip.screen.list;

import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.ScreenUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 * An item that can be used by ListView. Note that this takes in the x, y position values so that dynamic updating
 * of the position is possible.
 */
public abstract class ListItem<T> {
    protected MicrochipsMenuScreen screen;
    protected T item;
    protected int index;
    private Identifier texture;
    protected int u, v, width, height;
    protected boolean isSelected;

    public ListItem(MicrochipsMenuScreen screen, T item, int index) {
        this.screen = screen;
        this.item = item;
        this.index = index;
    }

    protected void setBackground(Identifier texture, int u, int v, int width, int height) {
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
    }

    public T getItem() {
        return item;
    }

    public int getIndex() {
        return index;
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
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, this.texture);

        if (isSelected) {
            // Selected
            renderSelectedBackground(matrices, x, y, mouseX, mouseY);
            return;
        }

        if (ScreenUtils.isWithin(mouseX, mouseY, x, y, this.width, this.height)) {
            // Hovered
            renderHoveredBackground(matrices, x, y, mouseX, mouseY);
        } else {
            // Normal
            screen.drawTexture(matrices, x, y, this.u, this.v, this.width, this.height);
        }
    }

    public void renderSelectedBackground(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        screen.drawTexture(matrices, x, y, this.u, this.v, this.width, this.height);
    }

    public void renderHoveredBackground(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        screen.drawTexture(matrices, x, y, this.u, this.v, this.width, this.height);
    }

    public abstract void renderContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY);

    public abstract boolean mouseClicked(int x, int y, double mouseX, double mouseY);
    public abstract boolean mouseSelected(int x, int y, double mouseX, double mouseY);
}
