package com.jumpcutfindo.microchip.screen.component;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.helper.SoundUtils;
import com.jumpcutfindo.microchip.screen.Interactable;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.ScreenUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class ColorButton implements Interactable {
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

    public boolean renderTooltip(MatrixStack matrices, int mouseX, int mouseY, int delta) {
        if (isMouseWithin(mouseX, mouseY)) {
            screen.renderTooltip(matrices, new TranslatableText("microchip.menu.color." + color.getColorName()), mouseX, mouseY);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (ScreenUtils.isWithin(mouseX, mouseY, x, y, width, height)) {
            this.isSelected = !this.isSelected;
            SoundUtils.playClickSound(MinecraftClient.getInstance().getSoundManager());
            return true;
        }

        return false;
    }

    private boolean isMouseWithin(int mouseX, int mouseY) {
        return ScreenUtils.isWithin(mouseX, mouseY, x, y, this.width, this.height);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }
}
