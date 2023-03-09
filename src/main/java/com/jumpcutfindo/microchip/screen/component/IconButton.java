package com.jumpcutfindo.microchip.screen.component;

import com.jumpcutfindo.microchip.screen.Interactable;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.ScreenUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;

public class IconButton implements Interactable {
    private final MicrochipsMenuScreen screen;
    private final int x, y;
    private final int u, v;
    private final int width, height;

    private boolean disabled;
    private final Runnable action;

    private final MutableText tooltip;

    public IconButton(MicrochipsMenuScreen screen, int x, int y, int u, int v, Runnable action, MutableText tooltip) {
        this.screen = screen;

        this.x = x;
        this.y = y;

        this.u = u;
        this.v = v;

        this.width = 26;
        this.height = 16;

        this.disabled = false;

        this.action = action;
        this.tooltip = tooltip;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, int delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, MicrochipsMenuScreen.BUTTONS_TEXTURE);

        if (isDisabled()) {
            // Disabled
            screen.drawTexture(matrices, x, y, u + 78, v, width, height);
            return;
        }

        if (isMouseWithin(mouseX, mouseY) && !screen.isWindowOpen()) {
            // Hovered
            screen.drawTexture(matrices, x, y, u + 26, v, width, height);
        } else {
            // Default
            screen.drawTexture(matrices, x, y, u, v, width, height);
        }
    }

    public boolean renderTooltip(MatrixStack matrices, int mouseX, int mouseY, int delta) {
        if (isMouseWithin(mouseX, mouseY)) {
            screen.renderTooltip(matrices, tooltip, mouseX, mouseY);
            return true;
        }
        return false;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (!isDisabled() && isMouseWithin(mouseX, mouseY)) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            action.run();
            return true;
        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }

    private boolean isMouseWithin(int mouseX, int mouseY) {
        return ScreenUtils.isWithin(mouseX, mouseY, x, y, this.width, this.height);
    }
}
