package com.jumpcutfindo.microchip.screen;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.GroupColor;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ColorButton {
    public static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_create_group.png");
    private final MicrochipsMenuScreen screen;
    private final int width, height;

    int x, y;
    private int u, v;

    private boolean isSelected;

    public ColorButton(int x, int y, MicrochipsMenuScreen screen, GroupColor color) {
        this.screen = screen;

        this.width = 10;
        this.height = 10;

        this.v = 0;

        for (int i = 0; i < GroupColor.values().length; i++) {
            if (color.equals(GroupColor.values()[i])) {
                this.u = 138 + i * 10;
                break;
            }
        }
    }
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        this.screen.drawTexture(matrices, x, y, u, v, this.width, this.height);
    }


    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (MicrochipsMenuScreen.isWithin(mouseX, mouseY, x, y, width, height)) {
            this.isSelected = !this.isSelected;
            return true;
        }

        return false;
    }
}
