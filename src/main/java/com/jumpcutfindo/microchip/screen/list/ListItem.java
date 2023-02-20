package com.jumpcutfindo.microchip.screen.list;

import com.jumpcutfindo.microchip.screen.Interactable;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.ScreenUtils;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * An item that can be used by ListView. Note that this takes in the x, y position values so that dynamic updating
 * of the position is possible.
 */
public abstract class ListItem implements Interactable {
    protected MicrochipsMenuScreen screen;
    private Identifier texture;
    protected int u, v, width, height;
    protected boolean isSelected;

    public ListItem(MicrochipsMenuScreen screen) {
        this.screen = screen;
    }

    protected ListItem setBackground(Identifier texture, int u, int v, int width, int height) {
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        return this;
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
