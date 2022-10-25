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
            this.activeWindow.render(matrices, windowX, windowY, mouseX, mouseY);
        }
    }

    protected void drawBackgroundGradient(MatrixStack matrices) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.microchipGroupList.handleClick(this.x, this.y, (int) mouseX, (int) mouseY, button);

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.activeWindow != null) {
            this.activeWindow = null;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void setActiveWindow(Window window) {
        this.activeWindow = window;
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

    protected static boolean isWithin(int x, int y, int textureX, int textureY, int textureWidth, int textureHeight) {
        return x >= textureX && x < textureX + textureWidth
                && y >= textureY && y < textureY + textureHeight;
    }
}
