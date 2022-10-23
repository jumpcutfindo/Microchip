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

    private final MicrochipGroupList groupList;
    private final MicrochipItemsList itemsList;
    public MicrochipsMenuScreen(PlayerEntity player) {
        super(new TranslatableText("microchip.menuTitle"));
        this.player = player;

        this.groupList = new MicrochipGroupList(this, this.getMicrochips());
        this.itemsList = new MicrochipItemsList(this);

        this.titleX = 7;
        this.titleY = 9;
    }

    @Override
    protected void init() {
        super.init();

        this.x = (this.width - (this.groupList.getWidth() + this.itemsList.getWidth())) / 2;
        this.y = (this.height - this.groupList.getHeight()) / 2;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.drawBackground(matrices, delta, mouseX, mouseY);
        this.drawForeground(matrices, mouseX, mouseY);
    }


    private void drawGroup(MatrixStack matrices, int x, int y) {

    }

    private void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        // Draw title
        this.textRenderer.draw(matrices, this.title, (float) (this.x + this.titleX), (float) (this.y + this.titleY), 0x404040);

        this.groupList.drawForeground(matrices, this.x, this.y, mouseX, mouseY);
    }

    private void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);

        this.groupList.drawBackground(matrices, x, y);
        this.itemsList.drawBackground(matrices, x + this.groupList.getWidth(), y);
    }

    protected TextRenderer getTextRenderer() {
        return this.textRenderer;
    }

    private Microchips getMicrochips() {
        return Tagger.getMicrochips(this.player);
    }
}
