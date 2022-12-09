package com.jumpcutfindo.microchip.screen.window;

import java.util.ArrayList;
import java.util.List;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class MicrochipInfoWindow extends Window {
    protected static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_info_window.png");

    private final Microchip microchip;
    public MicrochipInfoWindow(MicrochipsMenuScreen screen, Microchip microchip) {
        super(screen, new TranslatableText("microchip.menu.microchipInfo.windowTitle"));

        this.width = 160;
        this.height = 200;

        this.microchip = microchip;

        // TODO: Info Window
        /*
            Info window should contain name, type, health (full), armour
         */
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        this.screen.drawTexture(matrices, x, y, 0, 0, this.width, this.height);
    }

    @Override
    public void renderContent(MatrixStack matrices, int mouseX, int mouseY) {
        this.screen.getTextRenderer().draw(matrices, this.title, (float) (x + this.titleX), (float) (y + this.titleY), 0x404040);

    }

    @Override
    public void tick() {

    }

    @Override
    public boolean handleMouseScroll(double mouseX, double mouseY, double amount) {
        return false;
    }

    @Override
    public boolean handleMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    @Override
    public boolean handleClick(int mouseX, int mouseY, int button) {
        return false;
    }

    @Override
    public boolean handleKeyPress(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean handleCharTyped(char chr, int modifiers) {
        return false;
    }

    @Override
    public List<ClickableWidget> getWidgets() {
        return new ArrayList<>();
    }
}
