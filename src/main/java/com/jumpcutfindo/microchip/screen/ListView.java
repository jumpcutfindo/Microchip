package com.jumpcutfindo.microchip.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public abstract class ListView {
    protected final MicrochipsMenuScreen screen;
    private final Identifier texture;
    protected final int textureU, textureV, textureWidth, textureHeight;
    protected final int listX, listY;
    protected final int scrollbarWidth, scrollbarHeight, scrollbarX, scrollbarY;
    protected final int maxItems;

    private int step;
    private final int maxSteps;
    private final float stepAmount;

    private float scrollPosition; // Range from 0.0 to 1.0
    private boolean scrolling;
    protected final List<ListItem> listItems;
    protected List<ListItem> visibleItems;

    public ListView(
            MicrochipsMenuScreen screen,
            Identifier texture, int textureU, int textureV, int textureWidth, int textureHeight,
            int listX, int listY,
            int scrollbarX, int scrollbarY,
            List<ListItem> listItems, int maxItems) {
        this.screen = screen;

        this.texture = texture;
        this.textureU = textureU;
        this.textureV = textureV;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;

        this.listX = listX;
        this.listY = listY;

        this.scrollbarWidth = 14;
        this.scrollbarHeight = 144;
        this.scrollbarX = scrollbarX;
        this.scrollbarY = scrollbarY;

        this.listItems = listItems;
        this.maxItems = maxItems;
        this.visibleItems = new ArrayList<>();

        this.maxSteps = listItems.size() - maxItems;
        this.stepAmount = (float) (this.scrollbarHeight - 15) / (float) (this.maxSteps);
    }

    public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        this.renderBackground(matrices, x, y);
        this.renderItems(matrices, x + listX, y + listY, mouseX, mouseY);

        this.renderScrollbar(matrices, x + scrollbarX, y + scrollbarY + (int) (this.scrollPosition * (scrollbarHeight - 15)), mouseX, mouseY);
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

        if (this.listItems.size() == 0) return;

        int offsetY = 0;
        for (int i = step; i < step + maxItems; i++) {
            if (i >= this.listItems.size()) break;

            ListItem item = this.listItems.get(i);
            item.render(matrices, x, y + offsetY, mouseX, mouseY);
            offsetY += item.getHeight();
        }
    }

    private void renderScrollbar(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        if (!this.hasScrollbar()) return;

        RenderSystem.setShaderTexture(0, this.texture);
        screen.drawTexture(matrices, x, y, 216, 0, 13, 15);
    }

    public boolean mouseClicked(int x, int y, double mouseX, double mouseY, int button) {
        if (this.isClickInScrollbar(x, y, (int) mouseX, (int) mouseY)) {
            this.scrolling = this.hasScrollbar();
            return true;
        }
        return false;
    }

    public boolean mouseDragged(int x, int y, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrolling) {
            this.scrollPosition += deltaY / (this.scrollbarHeight - 15);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            this.step = (int) (this.scrollPosition * this.maxSteps);
            return true;
        } else {
            return false;
        }
    }

    public boolean mouseScrolled(int x, int y, double mouseX, double mouseY, double amount) {
        if (!this.hasScrollbar()) {
            return false;
        } else {
            this.scrollPosition += (1.0f / (float) this.maxSteps) * -amount;
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            this.step += (int) -amount;
            this.step = MathHelper.clamp(this.step, 0, this.maxSteps);
            return true;
        }
    }

    public boolean isClickInScrollbar(int x, int y, int mouseX, int mouseY) {
        return mouseX >= x + scrollbarX && mouseX < x + scrollbarX + scrollbarWidth
                && mouseY >= y + scrollbarY && mouseY < y + scrollbarY + scrollbarHeight;
    }

    public boolean hasScrollbar() {
        return listItems.size() > maxItems;
    }

    public int getTextureWidth() {
        return textureWidth;
    }

    public int getTextureHeight() {
        return textureHeight;
    }
}
