package com.jumpcutfindo.microchip.screen.window;

import java.util.List;

import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.widget.ClickableWidget;
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
        this.renderBackgroundGradient(matrices);
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

    public abstract void tick();

    public abstract boolean handleMouseScroll(double mouseX, double mouseY, double amount);

    public abstract boolean handleMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY);

    public abstract boolean handleClick(int mouseX, int mouseY, int button);

    public abstract boolean handleKeyPress(int keyCode, int scanCode, int modifiers);

    public abstract boolean handleCharTyped(char chr, int modifiers);

    public abstract List<ClickableWidget> getWidgets();

    private void renderBackgroundGradient(MatrixStack matrices) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        screen.drawBackgroundGradient(matrices);
    }
}
