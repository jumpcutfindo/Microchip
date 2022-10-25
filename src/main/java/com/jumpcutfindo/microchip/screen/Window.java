package com.jumpcutfindo.microchip.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public abstract class Window {
    protected MicrochipsMenuScreen screen;
    protected int width, height;
    protected Text title;
    protected int titleX, titleY;
    public Window(MicrochipsMenuScreen screen, Text title) {
        this.screen = screen;
        this.title = title;

        this.titleX = 7;
        this.titleY = 9;
    }

    public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        this.renderBackgroundGradient(matrices, x, y);
        this.renderBackground(matrices, x, y);
        this.renderContent(matrices, x, y, mouseX, mouseY);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public abstract void renderBackground(MatrixStack matrices, int x, int y);
    public abstract void renderContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY);

    private void renderBackgroundGradient(MatrixStack matrices, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        screen.drawBackgroundGradient(matrices);
    }
}
