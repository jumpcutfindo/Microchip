package com.jumpcutfindo.microchip.screen.window.info;

import com.google.common.collect.ImmutableList;
import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.screen.MicrochipScreen;
import com.jumpcutfindo.microchip.screen.window.MicrochipInfoWindow;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;

import java.util.List;

import static com.jumpcutfindo.microchip.screen.window.MicrochipInfoWindow.TEXTURE;

public class InventoryTab extends InfoTab {
    private List<ItemStack> inventoryList;

    public InventoryTab(MicrochipScreen screen, MicrochipInfoWindow window, Microchip microchip, GroupColor color, LivingEntity entity) {
        super(screen, window, microchip, color, entity);
        this.inventoryList = ImmutableList.of();
    }

    @Override
    public void renderContent(MatrixStack matrices, int mouseX, int mouseY) {
        // Draw inventory spaces
        screen.getTextRenderer().drawWithShadow(matrices, new TranslatableText("microchip.menu.microchipInfo.inventoryTab"), (float) (window.getX() + 7), (float) (window.getY() + 105), 0xFFFFFF);

        RenderSystem.setShaderTexture(0, TEXTURE);
        int xOffset = 12;
        int yOffset = 117;
        for (int i = 0; i < 2; i++) {
            screen.drawTexture(matrices, window.getX() + xOffset + i * 18, window.getY() + yOffset, 168, 159, 18, 18);
        }

        for (int i = 0; i < 4; i++) {
            screen.drawTexture(matrices, window.getX() + xOffset + 54 + i * 18, window.getY() + yOffset, 168, 159, 18, 18);
        }

        RenderSystem.setShaderTexture(0, TEXTURE);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 8; j++) {
                screen.drawTexture(matrices, window.getX() + xOffset + j * 18, window.getY() + yOffset + 24 + i * 18, 168, 159, 18, 18);
            }
        }

        xOffset += 1;
        yOffset += 1;
        if (entity != null) {
            // Draw hand items
            int handItemOffset = 0;
            for (ItemStack itemStack : entity.getItemsHand()) {
                drawItem(itemStack, window.getX() + xOffset + handItemOffset * 18, window.getY() + yOffset, "");
                handItemOffset ++;
            }

            // Draw armor items
            int armorItemOffset = 3;
            for (ItemStack itemStack : entity.getArmorItems()) {
                drawItem(itemStack, window.getX() + xOffset + 54 + armorItemOffset * 18, window.getY() + yOffset, "");
                armorItemOffset --;
            }

            // Draw inventory items
            for (int i = 0; i < inventoryList.size(); i++) {
                ItemStack itemStack = inventoryList.get(i);
                drawItem(itemStack, window.getX() + xOffset + (i % 8) * 18, window.getY() + yOffset + 24 + (i / 8) * 18, Integer.toString(itemStack.getCount()));
            }
        }
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

    public void setInventoryList(List<ItemStack> inventoryList) {
        this.inventoryList = inventoryList;
    }

    private void drawItem(ItemStack stack, int x, int y, String amountText) {
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.translate(0.0, 0.0, 32.0);
        RenderSystem.applyModelViewMatrix();
        this.screen.getItemRenderer().zOffset = 200.0F;
        this.screen.getItemRenderer().renderInGuiWithOverrides(stack, x, y);
        this.screen.getItemRenderer().renderGuiItemOverlay(this.screen.getTextRenderer(), stack, x, y, amountText);

        this.screen.getItemRenderer().zOffset = 0.0F;
    }
}
