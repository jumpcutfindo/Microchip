package com.jumpcutfindo.microchip.screen.list;

import java.util.ArrayList;
import java.util.List;

import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public abstract class ListView {
    protected final MicrochipsMenuScreen screen;
    private final Identifier texture;
    protected final int textureU, textureV, textureWidth, textureHeight;
    protected final int listX, listY;
    protected final int scrollbarWidth, scrollbarHeight, scrollbarU, scrollbarV, scrollbarX, scrollbarY;
    protected final int maxItems;

    private final boolean isSingleSelect;

    private int step;
    private final int maxSteps;
    private final float stepAmount;

    private float scrollPosition; // Range from 0.0 to 1.0
    private boolean scrolling;

    protected final List<ListItem> listItems;
    protected List<ListItem> visibleItems;

    private final List<ListItem> selectedItems;

    public ListView(
            MicrochipsMenuScreen screen,
            Identifier texture, int textureU, int textureV, int textureWidth, int textureHeight,
            int listX, int listY,
            int scrollbarU, int scrollbarV, int scrollbarX, int scrollbarY,
            List<ListItem> listItems, int maxItems,
            boolean isSingleSelect) {
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
        this.scrollbarU = scrollbarU;
        this.scrollbarV = scrollbarV;
        this.scrollbarX = scrollbarX;
        this.scrollbarY = scrollbarY;

        this.listItems = listItems;
        this.maxItems = maxItems;
        this.visibleItems = new ArrayList<>();

        this.maxSteps = listItems.size() - maxItems;
        this.stepAmount = (float) (this.scrollbarHeight - 15) / (float) (this.maxSteps);

        this.isSingleSelect = isSingleSelect;
        this.selectedItems = new ArrayList<>();
    }

    public void renderBackground(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, this.texture);
        screen.drawTexture(matrices, x, y, this.textureU, this.textureV, this.textureWidth, this.textureHeight);
        this.renderScrollbar(matrices, x + scrollbarX, y + scrollbarY + (int) (this.scrollPosition * (scrollbarHeight - 15)), mouseX, mouseY);
    }

    public void renderItems(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, this.texture);

        if (this.listItems.size() == 0) return;

        int offsetY = 0;
        for (int i = step; i < step + maxItems; i++) {
            if (i >= this.listItems.size()) break;

            ListItem item = this.listItems.get(i);
            item.render(matrices, x + listX, y + listY + offsetY, mouseX, mouseY);
            offsetY += item.getHeight();
        }
    }

    private void renderScrollbar(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        if (!this.hasScrollbar()) return;

        RenderSystem.setShaderTexture(0, this.texture);
        screen.drawTexture(matrices, x + scrollbarX, y + scrollbarY, scrollbarU, scrollbarV, 13, 15);
    }

    public boolean mouseClicked(int x, int y, double mouseX, double mouseY, int button) {
        if (this.isClickInScrollbar(x, y, (int) mouseX, (int) mouseY)) {
            this.scrolling = this.hasScrollbar();
            return true;
        }

        int offsetY = 0;
        for (int i = step; i < step + maxItems; i++) {
            if (i >= this.listItems.size()) break;

            ListItem item = this.listItems.get(i);
            if (item.onSelect(x + listX, y + listY + offsetY, mouseX, mouseY)) {
                this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                if (isSingleSelect) {
                    this.resetSelection();
                    item.setSelected(true);
                } else {
                    if (item.isSelected()) {
                        this.selectedItems.remove(item);
                        item.setSelected(false);
                    } else {
                        this.selectedItems.add(item);
                        item.setSelected(true);
                    }
                }
                return true;
            }

            offsetY += item.getHeight();
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

    public boolean isAnySelected() {
       return selectedItems.size() > 0;
    }

    public List<ListItem> getSelectedItems() {
        return selectedItems;
    }

    private void resetSelection() {
        for (ListItem item : listItems) item.setSelected(false);
    }

    public void playDownSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
