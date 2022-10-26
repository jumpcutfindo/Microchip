package com.jumpcutfindo.microchip.screen;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class MicrochipCreateGroupWindow extends Window {
    public static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_create_group.png");
    protected MicrochipCreateGroupWindow(MicrochipsMenuScreen screen) {
        super(screen, new TranslatableText("microchip.menu.createGroup.windowTitle"));
        this.width = 138;
        this.height = 84;
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
        this.screen.getTextRenderer().drawWithShadow(matrices, this.title, (float) (x + this.titleX), (float) (y + this.titleY), 0xFFFFFF);
        this.screen.getTextRenderer().draw(matrices, new TranslatableText("microchip.menu.createGroup.title"), (float) (x + this.titleX), (float) (y + 36), 0x404040);
    }
}
