package com.jumpcutfindo.microchip.screen;

import org.lwjgl.glfw.GLFW;

import com.jumpcutfindo.microchip.data.Microchips;
import com.jumpcutfindo.microchip.helper.Tagger;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;

public class MicrochipsMenuScreen extends Screen {

    private final int titleX, titleY;
    private int x, y;
    private PlayerEntity player;

    private final MicrochipGroupListView microchipGroupList;
    private final MicrochipsListView microchipsList;

    private Window activeWindow;
    public MicrochipsMenuScreen(PlayerEntity player) {
        super(new TranslatableText("microchip.menu.title"));
        this.player = player;

        this.microchipGroupList = new MicrochipGroupListView(this, this.getMicrochips());
        this.microchipsList = new MicrochipsListView(this, this.getMicrochips().getGroups().get(0));

        this.titleX = 7;
        this.titleY = 9;
    }

    @Override
    protected void init() {
        super.init();

        this.x = (this.width - (this.microchipGroupList.getTextureWidth() + this.microchipsList.getTextureWidth())) / 2;
        this.y = (this.height - this.microchipGroupList.getTextureHeight()) / 2;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.drawBackgroundGradient(matrices);
        this.microchipGroupList.render(matrices, this.x, this.y, mouseX, mouseY);
        this.microchipsList.render(matrices, this.x + this.microchipGroupList.getTextureWidth(), this.y, mouseX, mouseY);

        if (this.activeWindow != null) {
            int windowX = (this.width - this.activeWindow.getWidth()) / 2;
            int windowY = (this.height - this.activeWindow.getHeight()) / 2;
            this.activeWindow.setPos(windowX, windowY);
            this.activeWindow.render(matrices, mouseX, mouseY);
        }
    }

    protected void drawBackgroundGradient(MatrixStack matrices) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.activeWindow != null) {
            return this.activeWindow.handleClick((int) mouseX, (int) mouseY, button);
        }

        if (isMouseInGroupList(mouseX, mouseY)) {
            return this.microchipGroupList.mouseClicked(getGroupListX(), getListY(), (int) mouseX, (int) mouseY, button);
        }

        if (isMouseInMicrochipList(mouseX, mouseY)) {
            return this.microchipsList.mouseClicked(getMicrochipListX(), getListY(), (int) mouseX, (int) mouseY, button);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isMouseInGroupList(mouseX, mouseY)) {
            return this.microchipGroupList.mouseDragged(getGroupListX(), getListY(), mouseX, mouseY, button, deltaX, deltaY);
        }

        if (isMouseInMicrochipList(mouseX, mouseY)) {
            return this.microchipsList.mouseDragged(getMicrochipListX(), getListY(), mouseX, mouseY, button, deltaX, deltaY);
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (isMouseInGroupList(mouseX, mouseY)) {
            return this.microchipGroupList.mouseScrolled(getGroupListX(), getListY(), mouseX, mouseY, amount);
        }

        if (isMouseInMicrochipList(mouseX, mouseY)) {
            return this.microchipsList.mouseScrolled(getMicrochipListX(), getListY(), mouseX, mouseY, amount);
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.activeWindow != null) {
            this.activeWindow = null;
            return true;
        }

        if (this.activeWindow != null) {
           return this.activeWindow.handleKeyPress(keyCode, scanCode, modifiers);
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (this.activeWindow != null) {
            return this.activeWindow.handleCharTyped(chr, modifiers);
        }

        return super.charTyped(chr, modifiers);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.activeWindow != null) this.activeWindow.tick();
    }

    public void setActiveWindow(Window window) {
        this.clearChildren();
        this.activeWindow = window;

        if (window == null) return;

        this.activeWindow.getWidgets().forEach(this::addSelectableChild);
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    protected TextRenderer getTextRenderer() {
        return this.textRenderer;
    }

    private Microchips getMicrochips() {
        return Tagger.getMicrochips(this.player);
    }


    protected boolean isBlockedByWindow(int x, int y) {
        if (this.activeWindow == null) return false;
        else {
            return isWithin(x, y, this.activeWindow.getX(), this.activeWindow.getY(), this.activeWindow.getWidth(), this.activeWindow.getHeight());
        }
    }

    protected int getGroupListX() {
        return this.x;
    }

    protected int getMicrochipListX() {
        return this.x + this.microchipGroupList.getTextureWidth();
    }

    protected int getListY() {
        return this.y;
    }

    protected boolean isMouseInGroupList(double mouseX, double mouseY) {
        return isWithin(mouseX, mouseY, getGroupListX(), getListY(), this.microchipGroupList.getTextureWidth(), this.microchipGroupList.getTextureHeight());
    }

    protected boolean isMouseInMicrochipList(double mouseX, double mouseY) {
        return isWithin(mouseX, mouseY, getMicrochipListX(), getListY(), this.microchipsList.getTextureWidth(), this.microchipsList.getTextureHeight());
    }


    protected static boolean isWithin(double x, double y, int textureX, int textureY, int textureWidth, int textureHeight) {
        return x >= textureX && x < textureX + textureWidth
                && y >= textureY && y < textureY + textureHeight;
    }

}
