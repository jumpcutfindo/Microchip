package com.jumpcutfindo.microchip.screen.component;

import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;

public class IconButton {
    private final MicrochipsMenuScreen screen;
    private final int x, y;
    private final int u, v;
    private final int width, height;

    private boolean disabled;
    private final Runnable action;

    private final TranslatableText tooltip;

    public IconButton(MicrochipsMenuScreen screen, int x, int y, int u, int v, Runnable action, TranslatableText tooltip) {
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
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, MicrochipsMenuScreen.BUTTONS_TEXTURE);

        if (isDisabled()) {
            // Disabled
            screen.drawTexture(matrices, x, y, u + 78, v, width, height);
            return;
        }

        if (isMouseWithin(mouseX, mouseY)) {
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

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (!isDisabled() && isMouseWithin(mouseX, mouseY)) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            action.run();
            return true;
        }

        return false;
    }

    private boolean isMouseWithin(int mouseX, int mouseY) {
        return MicrochipsMenuScreen.isWithin(mouseX, mouseY, x, y, this.width, this.height);
    }
}
