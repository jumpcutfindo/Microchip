package com.jumpcutfindo.microchip.screen;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.jumpcutfindo.microchip.data.Microchips;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class MicrochipGroupList {
    protected static final Identifier TEXTURE_GROUP_LIST_BACKGROUND = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_group_list.png");
    private final MicrochipsMenuScreen screen;
    private final Microchips microchips;
    private final int width, height;
    private List<MicrochipMenuGroup> menuGroups;

    public MicrochipGroupList(MicrochipsMenuScreen screen, Microchips microchips) {
        this.screen = screen;
        this.microchips = microchips;

        this.width = 160;
        this.height = 178;

        this.menuGroups = new ArrayList<>();
        for (MicrochipGroup group : microchips.getGroups()) this.menuGroups.add(new MicrochipMenuGroup(this.screen, group));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void drawForeground(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        this.drawGroups(matrices, x, y, mouseX, mouseY);
    }

    private void drawGroups(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        int groupsX = x + 8;
        int groupsY = y + 26;

        int excess = 10 - 8 + 1;
        int scrollBarHeight = 144;

        for (int i = 0; i < 10; i++) {
            MicrochipMenuGroup menuGroup = this.menuGroups.get(0);
            menuGroup.draw(matrices, groupsX, groupsY + i * 18, mouseX, mouseY);
        }
    }

    public void drawBackground(MatrixStack matrices, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE_GROUP_LIST_BACKGROUND);
        screen.drawTexture(matrices, x, y, 0, 0, this.width, this.height);
    }
}
