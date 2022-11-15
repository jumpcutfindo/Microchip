package com.jumpcutfindo.microchip.screen;

import org.lwjgl.glfw.GLFW;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.Microchips;
import com.jumpcutfindo.microchip.helper.Tagger;
import com.jumpcutfindo.microchip.screen.list.MicrochipGroupListView;
import com.jumpcutfindo.microchip.screen.list.MicrochipsListView;
import com.jumpcutfindo.microchip.screen.window.Window;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class MicrochipsMenuScreen extends Screen {
    public static final Identifier BUTTONS_TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_screen_buttons.png");

    private final int titleX, titleY;
    private int x, y;
    private Microchips microchips;
    private int groupCount, chipCount;
    private int selectedGroup;

    private MicrochipGroupListView microchipGroupList;
    private MicrochipsListView microchipsList;

    private Window activeWindow;
    public MicrochipsMenuScreen(PlayerEntity player) {
        super(new TranslatableText("microchip.menu.title"));
        this.microchips = Tagger.getMicrochips(player);
        this.groupCount = this.microchips.getGroupCount();
        this.chipCount = this.microchips.getChipCount();

        this.selectedGroup = 0;

        this.titleX = 7;
        this.titleY = 9;
    }

    @Override
    protected void init() {
        super.init();

        this.refreshScreen(RefreshType.BOTH);

        this.x = (this.width - (this.microchipGroupList.getTextureWidth() + this.microchipsList.getTextureWidth())) / 2;
        this.y = (this.height - this.microchipGroupList.getTextureHeight()) / 2;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (hasUpdates()) refreshScreen(RefreshType.BOTH);

        this.drawBackgroundGradient(matrices);
        this.microchipGroupList.renderBackground(matrices, this.x, this.y, mouseX, mouseY);
        this.microchipsList.renderBackground(matrices, this.x + this.microchipGroupList.getTextureWidth(), this.y, mouseX, mouseY);

        this.microchipGroupList.renderItems(matrices, this.x, this.y, mouseX, mouseY);
        this.microchipsList.renderItems(matrices, this.x + this.microchipGroupList.getTextureWidth(), this.y, mouseX, mouseY);

        if (this.activeWindow != null) {
            int windowX = (this.width - this.activeWindow.getWidth()) / 2;
            int windowY = (this.height - this.activeWindow.getHeight()) / 2;
            this.activeWindow.setPos(windowX, windowY);
            this.activeWindow.render(matrices, mouseX, mouseY);
        }
    }

    public void drawBackgroundGradient(MatrixStack matrices) {
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

    public void setSelectedGroup(int index) {
        this.selectedGroup = index;
        // Only refresh microchips since the selection changed
        this.refreshScreen(RefreshType.MICROCHIPS);
    }

    public PlayerEntity getPlayer() {
        return client.player;
    }

    public TextRenderer getTextRenderer() {
        return this.textRenderer;
    }

    private boolean hasUpdates() {
        Microchips clientMicrochips = Tagger.getMicrochips(client.player);
        return this.groupCount != clientMicrochips.getGroupCount()
                || this.chipCount != clientMicrochips.getChipCount();
    }

    public void refreshScreen(RefreshType refreshType) {
        this.microchips = Tagger.getMicrochips(client.player);
        this.groupCount = this.microchips.getGroupCount();
        this.chipCount = this.microchips.getChipCount();

        switch (refreshType) {
        case GROUP -> refreshGroups();
        case MICROCHIPS -> refreshMicrochips();
        case BOTH -> {
            refreshGroups();
            refreshMicrochips();
        }
        }
    }

    private void refreshGroups() {
        this.microchipGroupList = new MicrochipGroupListView(this, this.microchips);
    }

    private void refreshMicrochips() {
        int index = Math.min(this.microchips.getGroupCount() - 1, this.selectedGroup);
        index = Math.max(0, index);
        this.selectedGroup = index;

        if (this.microchips.getAllGroups().size() == 0) {
            this.microchipsList = new MicrochipsListView(this, null);
        } else {
            this.microchipsList = new MicrochipsListView(this, this.microchips.getAllGroups().get(index));
        }
    }

    public boolean isBlockedByWindow(int x, int y) {
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

    public static boolean isWithin(double x, double y, int textureX, int textureY, int textureWidth, int textureHeight) {
        return x >= textureX && x < textureX + textureWidth
                && y >= textureY && y < textureY + textureHeight;
    }

    public enum RefreshType {
        GROUP, MICROCHIPS, BOTH;
    }
}
