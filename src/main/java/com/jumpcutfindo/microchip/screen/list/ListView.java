package com.jumpcutfindo.microchip.screen.list;

import com.jumpcutfindo.microchip.screen.Interactable;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.ScreenUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class ListView<T extends ListItem<?>> implements Interactable {
    protected final MicrochipsMenuScreen screen;

    // Texture position
    public int x, y;

    // Texture information
    protected Identifier texture;
    protected int textureU, textureV, textureWidth, textureHeight;
    protected int listX, listY;
    protected int scrollbarWidth, scrollbarHeight, scrollbarU, scrollbarV, scrollbarX, scrollbarY;
    protected int maxItems;

    private boolean isSingleSelect;

    private int step;
    private int maxSteps;
    private float stepAmount;

    private float scrollPosition; // Range from 0.0 to 1.0
    private boolean scrolling;

    protected List<T> listItems;
    protected List<T> visibleItems;

    private List<T> selectedItems;
    private List<Integer> selectedIndices;

    public ListView(MicrochipsMenuScreen screen) {
        this.screen = screen;

        this.selectedItems = new ArrayList<>();
        this.selectedIndices = new ArrayList<>();
    }

    protected ListView<T> setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    protected ListView<T> setTexture(Identifier texture, int u, int v, int width, int height) {
        this.texture = texture;
        this.textureU = u;
        this.textureV = v;
        this.textureWidth = width;
        this.textureHeight = height;
        return this;
    }

    protected ListView<T> setListPosition(int x, int y) {
        this.listX = x;
        this.listY = y;
        return this;
    }

    protected ListView<T> setScrollbar(int x, int y, int u, int v, int width, int height) {
        this.scrollbarX = x;
        this.scrollbarY = y;
        this.scrollbarU = u;
        this.scrollbarV = v;
        this.scrollbarWidth = width;
        this.scrollbarHeight = height;
        return this;
    }

    protected ListView<T> setList(List<T> listItems, int maxItems) {
        this.listItems = listItems;
        this.maxItems = maxItems;
        this.visibleItems = new ArrayList<>();
        this.maxSteps = listItems.size() - maxItems;
        this.stepAmount = (float) (this.scrollbarHeight - 15) / (float) (this.maxSteps);
        return this;
    }

    protected ListView<T> setSingleSelect(boolean isSingleSelect) {
        this.isSingleSelect = isSingleSelect;
        return this;
    }

    public void renderBackground(MatrixStack matrices, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.texture);
        screen.drawTexture(matrices, x, y, this.textureU, this.textureV, this.textureWidth, this.textureHeight);
        this.renderScrollbar(matrices, mouseX, mouseY);
    }

    public void renderItems(MatrixStack matrices, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, this.texture);

        if (this.listItems.size() == 0) return;

        this.step = (int) (this.scrollPosition * this.maxSteps);

        int offsetY = 0;
        for (int i = step; i < step + maxItems; i++) {
            if (i >= this.listItems.size()) break;

            T item = this.listItems.get(i);
            item.render(matrices, x + listX, y + listY + offsetY, mouseX, mouseY);
            offsetY += item.getHeight();
        }
    }

    private void renderScrollbar(MatrixStack matrices, int mouseX, int mouseY) {
        if (!this.hasScrollbar()) return;

        RenderSystem.setShaderTexture(0, this.texture);
        screen.drawTexture(matrices, x + scrollbarX, y + scrollbarY + (int) (this.scrollPosition * (scrollbarHeight - 15)), scrollbarU, scrollbarV, 13, 15);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (this.isClickInScrollbar(mouseX, mouseY)) {
            this.scrolling = this.hasScrollbar();
            return true;
        }

        int offsetY = 0;
        for (int i = step; i < step + maxItems; i++) {
            if (i >= this.listItems.size()) break;

            T item = this.listItems.get(i);
            if (item.mouseSelected(x + listX, y + listY + offsetY, mouseX, mouseY)) {
                this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                this.setSelected(i);
                return true;
            }

            if (item.mouseClicked(x + listX, y + listY + offsetY, mouseX, mouseY)) {
                return true;
            }

            offsetY += item.getHeight();
        }

        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrolling) {
            this.scrollPosition += deltaY / (this.scrollbarHeight - 15);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            return true;
        } else {
            return false;
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
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

    public boolean isClickInScrollbar(int mouseX, int mouseY) {
        return ScreenUtils.isWithin(mouseX, mouseY, x + scrollbarX, y + scrollbarY, scrollbarWidth, scrollbarHeight);
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

    public boolean setSelected(int index) {
        if (index >= this.listItems.size()) return false;

        T item = this.listItems.get(index);

        if (isSingleSelect) {
            this.resetSelection();
            this.selectedItems.add(item);
            this.selectedIndices.add(index);
            item.setSelected(true);
        } else {
            if (item.isSelected()) {
                this.selectedItems.remove(item);
                this.selectedIndices.remove((Integer) index);
                item.setSelected(false);
            } else {
                this.selectedItems.add(item);
                this.selectedIndices.add(index);
                item.setSelected(true);
            }
        }

        return true;
    }

    public boolean isAnySelected() {
       return selectedItems.size() > 0;
    }

    public List<T> getSelectedItems() {
        return selectedItems;
    }

    public List<Integer> getSelectedIndices() {
        return selectedIndices;
    }

    private void resetSelection() {
        for (T item : listItems) item.setSelected(false);
        this.selectedItems = new ArrayList<>();
        this.selectedIndices = new ArrayList<>();
    }

    public void playDownSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
