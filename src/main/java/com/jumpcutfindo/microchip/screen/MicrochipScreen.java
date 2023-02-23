package com.jumpcutfindo.microchip.screen;

import com.jumpcutfindo.microchip.screen.window.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

/**
 * Mod specific screen that introduces some helper methods for use throughout the application.
 */
public class MicrochipScreen extends Screen {
    Window activeWindow;
    protected MicrochipScreen(Text title) {
        super(title);
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

    public PlayerEntity getPlayer() {
        return client.player;
    }

    public TextRenderer getTextRenderer() {
        return this.textRenderer;
    }
}
