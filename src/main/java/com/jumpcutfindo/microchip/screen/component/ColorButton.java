package com.jumpcutfindo.microchip.screen.component;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.helper.SoundUtils;
import com.jumpcutfindo.microchip.screen.Interactable;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.ScreenUtils;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ColorButton implements Interactable {
    public static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_create_group.png");
    private final MicrochipsMenuScreen screen;
    private final int width, height;

    public int x;
    public int y;
    private int u;
    private final int v;

    private final GroupColor color;

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

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(TEXTURE, x, y, u, v, this.width, this.height);
        if (this.isSelected) context.drawTexture(TEXTURE, x, y + 12, u, v + 10, this.width, this.height);
    }

    public boolean renderTooltip(DrawContext context, int mouseX, int mouseY, int delta) {
        if (isMouseWithin(mouseX, mouseY)) {
            context.drawTooltip(this.screen.getTextRenderer(), Text.translatable("microchip.menu.color." + color.getColorName()), mouseX, mouseY);
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
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
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
