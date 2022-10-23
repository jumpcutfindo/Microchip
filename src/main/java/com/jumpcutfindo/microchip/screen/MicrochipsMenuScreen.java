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
    protected static final Identifier TEXTURE_GROUP_LIST_BACKGROUND = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_group_list.png");
    protected static final Identifier TEXTURE_MICROCHIP_LIST_BACKGROUND = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_list.png");
    protected static final Identifier BACKGROUND_TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_menu.png");

    private final int titleX, titleY, groupListWidth, groupListHeight, microchipListWidth, microchipListHeight;
    private int x, y;
    private PlayerEntity player;

    private List<MicrochipMenuGroup> menuGroups;
    public MicrochipsMenuScreen(PlayerEntity player) {
        super(new TranslatableText("microchip.menuTitle"));
        this.player = player;

        this.groupListWidth = 160;
        this.groupListHeight = 178;

        this.microchipListWidth = 216;
        this.microchipListHeight = 180;

        this.titleX = 7;
        this.titleY = 9;

        this.menuGroups = new ArrayList<>();
    }

    @Override
    protected void init() {
        super.init();

        this.x = (this.width - (this.groupListWidth + this.microchipListWidth)) / 2;
        this.y = (this.height - this.groupListHeight) / 2;

        Microchips microchips = this.getMicrochips();
        for (MicrochipGroup group : microchips.getGroups()) {
            this.menuGroups.add(new MicrochipMenuGroup(this, group));
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.drawBackground(matrices, delta, mouseX, mouseY);
        this.drawForeground(matrices, mouseX, mouseY);
        this.drawGroups(matrices, mouseX, mouseY);
    }

    private void drawGroups(MatrixStack matrices, int mouseX, int mouseY) {
        int groupsX = this.x + 8;
        int groupsY = this.y + 26;

        for (int i = 0; i < 10; i++) {
            MicrochipMenuGroup menuGroup = this.menuGroups.get(0);
            menuGroup.draw(matrices, groupsX, groupsY + i * 18, mouseX, mouseY);
        }
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
        RenderSystem.setShaderTexture(0, TEXTURE_GROUP_LIST_BACKGROUND);
        this.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
        int i = this.x;
        int j = this.y;
        this.drawTexture(matrices, i, j, 0, 0, this.groupListWidth, this.groupListHeight);

        RenderSystem.setShaderTexture(0, TEXTURE_MICROCHIP_LIST_BACKGROUND);
        this.drawTexture(matrices, i + this.groupListWidth, j, 0, 0, this.microchipListWidth, this.microchipListHeight);
    }

    protected TextRenderer getTextRenderer() {
        return this.textRenderer;
    }

    private Microchips getMicrochips() {
        return Tagger.getMicrochips(this.player);
    }
}
