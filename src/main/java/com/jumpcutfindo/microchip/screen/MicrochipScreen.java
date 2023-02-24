package com.jumpcutfindo.microchip.screen;

import com.jumpcutfindo.microchip.screen.window.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * Mod specific screen that introduces some helper methods for use throughout the application.
 * Adds support for Windows that can be added on top of the existing interface.
 */
public class MicrochipScreen extends Screen {
    Window activeWindow;
    boolean isStandalone;
    public MicrochipScreen(Text title) {
        super(title);
    }

    public void setStandalone(boolean standalone) {
        isStandalone = standalone;
    }

    public boolean isStandalone() {
        return isStandalone;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.activeWindow != null) {
            this.activeWindow.setPos(this.getWindowX(this.activeWindow.getWidth()), this.getWindowY(this.activeWindow.getHeight()));
            this.activeWindow.render(matrices, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.activeWindow != null) {
            return this.activeWindow.mouseClicked((int) mouseX, (int) mouseY, button);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.activeWindow != null) {
            return this.activeWindow.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (this.activeWindow != null) {
            return this.activeWindow.mouseScrolled(mouseX, mouseY, amount);
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.activeWindow != null) {
            if (isStandalone) close();
            this.activeWindow = null;
            return true;
        }

        if (this.activeWindow != null) {
            return this.activeWindow.keyPressed(keyCode, scanCode, modifiers);
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (this.activeWindow != null) {
            return this.activeWindow.charTyped(chr, modifiers);
        }

        return super.charTyped(chr, modifiers);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.activeWindow != null) this.activeWindow.tick();
    }

    public void drawBackgroundGradient(MatrixStack matrices) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
    }

    public void setActiveWindow(Window window) {
        this.clearChildren();
        this.activeWindow = window;

        if (window == null) return;

        this.activeWindow.getWidgets().forEach(this::addSelectableChild);
    }

    public Window getActiveWindow() {
        return activeWindow;
    }

    public int getWindowX(int windowWidth) {
        return (this.width - windowWidth) / 2;
    }

    public int getWindowY(int windowHeight) {
        return (this.height - windowHeight) / 2;
    }

    public boolean isWindowOpen() {
        return this.activeWindow != null;
    }

    public boolean isBlockedByWindow(int x, int y) {
        if (this.activeWindow == null) return false;
        else {
            return ScreenUtils.isWithin(x, y, this.activeWindow.getX(), this.activeWindow.getY(), this.activeWindow.getWidth(), this.activeWindow.getHeight());
        }
    }

    public PlayerEntity getPlayer() {
        return client.player;
    }

    public TextRenderer getTextRenderer() {
        return this.textRenderer;
    }
}