package com.jumpcutfindo.microchip.screen.list;

import com.jumpcutfindo.microchip.helper.SoundUtils;
import com.jumpcutfindo.microchip.screen.Interactable;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.ScreenUtils;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private Set<T> selectedItems;
    private Set<Integer> selectedIndices;

    private int lastToggledIndex = 0;

    public ListView(MicrochipsMenuScreen screen) {
        this.screen = screen;

        this.selectedItems = new HashSet<>();
        this.selectedIndices = new HashSet<>();
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
        this.maxSteps = Math.max(0, listItems.size() - maxItems);
        this.stepAmount = (float) (this.scrollbarHeight - 15) / (float) (this.maxSteps);
        return this;
    }

    protected ListView<T> setSingleSelect(boolean isSingleSelect) {
        this.isSingleSelect = isSingleSelect;
        return this;
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY) {
        context.drawTexture(this.texture, x, y, this.textureU, this.textureV, this.textureWidth, this.textureHeight);
        this.renderScrollbar(context, mouseX, mouseY);
    }

    public void renderItems(DrawContext context, int mouseX, int mouseY) {
        if (this.listItems.size() == 0) return;

        this.step = (int) (this.scrollPosition * this.maxSteps);

        int offsetY = 0;
        for (int i = step; i < step + maxItems; i++) {
            if (i >= this.listItems.size()) break;

            T item = this.listItems.get(i);
            item.render(context, x + listX, y + listY + offsetY, mouseX, mouseY);
            offsetY += item.getHeight();
        }
    }

    private void renderScrollbar(DrawContext context, int mouseX, int mouseY) {
        if (!this.hasScrollbar()) return;

        context.drawTexture(this.texture, x + scrollbarX, y + scrollbarY + (int) (this.scrollPosition * (scrollbarHeight - 15)), scrollbarU, scrollbarV, 13, 15);
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
                SoundUtils.playClickSound(MinecraftClient.getInstance().getSoundManager());
                this.toggleSelected(i);
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

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!this.hasScrollbar()) {
            return false;
        } else {
            this.scrollPosition += (1.0f / (float) this.maxSteps) * -verticalAmount;
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            this.step += (int) -verticalAmount;
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

    public void setSelected(int index, boolean selected) {
        if (index < listItems.size()) {
            T item = listItems.get(index);
            item.setSelected(selected);
            selectedItems.add(item);
            selectedIndices.add(index);
        }
    }

    public boolean toggleSelected(int index) {
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

        this.lastToggledIndex = index;

        return true;
    }

    public boolean isAnySelected() {
       return selectedItems.size() > 0;
    }

    public boolean isAllSelected() {
        return selectedItems.size() == listItems.size();
    }

    public void setLastToggledIndex(int lastToggledIndex) {
        this.lastToggledIndex = lastToggledIndex;
    }

    public int getLastToggledIndex() {
        return lastToggledIndex;
    }

    public List<T> getSelectedItems() {
        return selectedItems.stream().toList();
    }

    public List<Integer> getSelectedIndices() {
        return selectedIndices.stream().toList();
    }

    protected void resetSelection() {
        for (T item : listItems) item.setSelected(false);
        this.selectedItems = new HashSet<>();
        this.selectedIndices = new HashSet<>();
    }

    public float getScrollPosition() {
        return scrollPosition;
    }

    public void setScrollPosition(float scrollPosition) {
        this.scrollPosition = scrollPosition;
    }

    /**
     * Retrieves an NbtCompound that represents the various settings (e.g. state) of the ListView.
     */
    public abstract NbtCompound getSettings();

    /**
     * Applies a given set of settings to the ListView.
     */
    public abstract void applySettings(NbtCompound settings);
}
