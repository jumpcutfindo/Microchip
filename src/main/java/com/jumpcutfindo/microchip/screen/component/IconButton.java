package com.jumpcutfindo.microchip.screen.component;

import com.jumpcutfindo.microchip.helper.SoundUtils;
import com.jumpcutfindo.microchip.screen.Interactable;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.ScreenUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.MutableText;

public class IconButton implements Interactable {
    private final MicrochipsMenuScreen screen;
    private final int x, y;
    private final int u, v;
    private final int width, height;

    private boolean active, disabled;
    private final Runnable action;

    private final MutableText tooltip;

    public IconButton(MicrochipsMenuScreen screen, int x, int y, int u, int v, Runnable action, MutableText tooltip) {
        this.screen = screen;

        this.x = x;
        this.y = y;

        this.u = u;
        this.v = v;

        this.width = 16;
        this.height = 16;

        this.disabled = false;

        this.action = action;
        this.tooltip = tooltip;
    }

    public void render(DrawContext context, int mouseX, int mouseY, int delta) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (isDisabled()) {
            // Disabled
            context.drawTexture(RenderLayer::getGuiTextured, MicrochipsMenuScreen.BUTTONS_TEXTURE, x, y, u + 48, v, width, height, MicrochipsMenuScreen.BUTTONS_TEXTURE_WIDTH, MicrochipsMenuScreen.BUTTONS_TEXTURE_HEIGHT);
            return;
        }

        if (isActive()) {
            context.drawTexture(RenderLayer::getGuiTextured, MicrochipsMenuScreen.BUTTONS_TEXTURE, x, y, u + 32, v, width, height, MicrochipsMenuScreen.BUTTONS_TEXTURE_WIDTH, MicrochipsMenuScreen.BUTTONS_TEXTURE_HEIGHT);
            return;
        }

        if (isMouseWithin(mouseX, mouseY) && !screen.isWindowOpen()) {
            // Hovered
            context.drawTexture(RenderLayer::getGuiTextured, MicrochipsMenuScreen.BUTTONS_TEXTURE, x, y, u + 16, v, width, height, MicrochipsMenuScreen.BUTTONS_TEXTURE_WIDTH, MicrochipsMenuScreen.BUTTONS_TEXTURE_HEIGHT);
        } else {
            // Default
            context.drawTexture(RenderLayer::getGuiTextured, MicrochipsMenuScreen.BUTTONS_TEXTURE, x, y, u, v, width, height, MicrochipsMenuScreen.BUTTONS_TEXTURE_WIDTH, MicrochipsMenuScreen.BUTTONS_TEXTURE_HEIGHT);
        }
    }

    public boolean renderTooltip(DrawContext context, int mouseX, int mouseY, int delta) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (isMouseWithin(mouseX, mouseY)) {
            context.drawTooltip(this.screen.getTextRenderer(), tooltip, mouseX, mouseY);
            return true;
        }
        return false;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (!isDisabled() && isMouseWithin(mouseX, mouseY)) {
            SoundUtils.playClickSound(MinecraftClient.getInstance().getSoundManager());
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
