package com.jumpcutfindo.microchip.screen;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.Tagger;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.jumpcutfindo.microchip.data.Microchips;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class MicrochipsMenuScreen extends Screen {

    private final int titleX, titleY;
    private int x, y;
    private PlayerEntity player;

    private final MicrochipGroupListView microchipGroupList;
    private final MicrochipsListView microchipsList;
    public MicrochipsMenuScreen(PlayerEntity player) {
        super(new TranslatableText("microchip.menuTitle"));
        this.player = player;

        this.microchipGroupList = new MicrochipGroupListView(this, this.getMicrochips());
        this.microchipsList = new MicrochipsListView(this, null);

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
        this.microchipGroupList.render(matrices, this.x, this.y, mouseX, mouseY);
    }


    private void drawGroup(MatrixStack matrices, int x, int y) {

    }

    private void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        // Draw title
        this.textRenderer.draw(matrices, this.title, (float) (this.x + this.titleX), (float) (this.y + this.titleY), 0x404040);
    }

    private void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
    }

    protected TextRenderer getTextRenderer() {
        return this.textRenderer;
    }

    private Microchips getMicrochips() {
        return Tagger.getMicrochips(this.player);
    }
}
