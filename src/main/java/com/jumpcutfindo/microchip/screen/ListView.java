package com.jumpcutfindo.microchip.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.List;

public class ListView {
    private final Screen screen;
    private final Identifier texture;
    protected final int textureU, textureV, textureWidth, textureHeight;
    protected final int listX, listY, scrollbarX, scrollbarY;
    protected final List<ListItem> listItems;

    public ListView(Screen screen, Identifier texture, int textureU, int textureV, int textureWidth, int textureHeight, int listX, int listY, int scrollbarX, int scrollbarY, List<ListItem> listItems) {
        this.screen = screen;
        this.texture = texture;
        this.textureU = textureU;
        this.textureV = textureV;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.listX = listX;
        this.listY = listY;
        this.scrollbarX = scrollbarX;
        this.scrollbarY = scrollbarY;
        this.listItems = listItems;
    }

    public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        this.renderBackground(matrices, x, y);
        this.renderItems(matrices, x + listX, y + listY, mouseX, mouseY);
    }

    private void renderBackground(MatrixStack matrices, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, this.texture);
        screen.drawTexture(matrices, x, y, this.textureU, this.textureV, this.textureWidth, this.textureHeight);
    }

    private void renderItems(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, this.texture);

        int offsetY = 0;
        ListItem item = this.listItems.get(0);
        for (int i = 0; i < 10; i++) {
            item.render(matrices, x, y + offsetY, mouseX, mouseY);
            offsetY += item.getHeight();
        }
    }

    public int getTextureWidth() {
        return textureWidth;
    }

    public int getTextureHeight() {
        return textureHeight;
    }
}
