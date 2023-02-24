package com.jumpcutfindo.microchip.screen.window.info;

import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.screen.MicrochipScreen;
import com.jumpcutfindo.microchip.screen.window.MicrochipInfoWindow;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.TranslatableText;

import static com.jumpcutfindo.microchip.screen.window.MicrochipInfoWindow.TEXTURE;

public class InventoryTab extends InfoTab {

    public InventoryTab(MicrochipScreen screen, MicrochipInfoWindow window, Microchip microchip, GroupColor color, LivingEntity entity) {
        super(screen, window, microchip, color, entity);
    }

    @Override
    public void renderContent(MatrixStack matrices, int mouseX, int mouseY) {
        screen.getTextRenderer().drawWithShadow(matrices, new TranslatableText("microchip.menu.microchipInfo.inventoryTab"), (float) (window.getX() + 7), (float) (window.getY() + 105), 0xFFFFFF);

    }

    @Override
    public void renderTooltips(MatrixStack matrices, int mouseX, int mouseY) {

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
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
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

    @Override
    public void tick() {

    }
}
