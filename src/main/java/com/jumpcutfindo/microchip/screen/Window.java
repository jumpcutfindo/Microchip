package com.jumpcutfindo.microchip.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public abstract class Window {
    protected MicrochipsMenuScreen screen;
    protected int x, y;
    protected int width, height;
    protected Text title;
    protected int titleX, titleY;
    public Window(MicrochipsMenuScreen screen, Text title) {
        this.screen = screen;
        this.title = title;

        this.titleX = 7;
        this.titleY = 9;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY) {
        this.renderBackgroundGradient(matrices, x, y);
        this.renderBackground(matrices);
        this.renderContent(matrices, mouseX, mouseY);
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public abstract void renderBackground(MatrixStack matrices);
    public abstract void renderContent(MatrixStack matrices, int mouseX, int mouseY);

    private void renderBackgroundGradient(MatrixStack matrices, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        screen.drawBackgroundGradient(matrices);
    }
}
