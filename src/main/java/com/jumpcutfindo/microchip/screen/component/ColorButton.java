package com.jumpcutfindo.microchip.screen.component;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class ColorButton {
    public static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_create_group.png");
    private final MicrochipsMenuScreen screen;
    private final int width, height;

    public int x;
    public int y;
    private int u, v;

    private GroupColor color;

    private boolean isSelected;

    public ColorButton(int x, int y, MicrochipsMenuScreen screen, GroupColor color) {
        this.screen = screen;
        this.color = color;

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

    public GroupColor getColor() {
        return color;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        this.screen.drawTexture(matrices, x, y, u, v, this.width, this.height);
        if (this.isSelected) this.screen.drawTexture(matrices, x, y + 12, u, v + 10, this.width, this.height);
    }


    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (MicrochipsMenuScreen.isWithin(mouseX, mouseY, x, y, width, height)) {
            this.isSelected = !this.isSelected;
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            return true;
        }

        return false;
    }

    private void playDownSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}