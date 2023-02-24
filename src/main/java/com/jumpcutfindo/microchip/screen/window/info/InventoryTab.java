package com.jumpcutfindo.microchip.screen.window.info;

import com.google.common.collect.ImmutableList;
import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.screen.MicrochipScreen;
import com.jumpcutfindo.microchip.screen.ScreenUtils;
import com.jumpcutfindo.microchip.screen.window.MicrochipInfoWindow;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jumpcutfindo.microchip.screen.window.MicrochipInfoWindow.TEXTURE;

public class InventoryTab extends InfoTab {
    private List<ItemStack> inventoryList;
    private List<ItemSlot> handSlots, armorSlots, inventorySlots;

    public InventoryTab(MicrochipScreen screen, MicrochipInfoWindow window, Microchip microchip, GroupColor color, LivingEntity entity) {
        super(screen, window, microchip, color, entity);
        this.inventoryList = ImmutableList.of();

        createEmptySlots();
        populateSlots();
    }

    private void createEmptySlots() {
        handSlots = new ArrayList<>();

        int xOffset = 12;
        int yOffset = 117;

        for (int i = 0; i < 2; i++) {
            int x = xOffset + i * 18;
            int y = yOffset;
            handSlots.add(new ItemSlot(x, y));
        }

        armorSlots = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int x = xOffset + 54 + i * 18;
            int y = yOffset;
            armorSlots.add(new ItemSlot(x, y));
        }

        inventorySlots = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 8; j++) {
                int x = xOffset + j * 18;
                int y = yOffset + 24 + i * 18;
                inventorySlots.add(new ItemSlot(x, y));
            }
        }
    }

    private void populateSlots() {
        if (entity != null) {
            int handIndex = 0;
            for (ItemStack itemStack : entity.getItemsHand()) {
                handSlots.get(handIndex).setItemStack(itemStack);
                handIndex++;
            }

            int armorIndex = 0;
            for (ItemStack itemStack : entity.getArmorItems()) {
                armorSlots.get(armorIndex).setItemStack(itemStack);
                armorIndex++;
            }

            int inventoryIndex = 0;
            for (ItemStack itemStack : this.inventoryList) {
                inventorySlots.get(inventoryIndex).setItemStack(itemStack);
                inventoryIndex++;

                // TODO: Improve this handling (in the situation where inventory is larger than size 16)
                if (inventoryIndex >= inventoryList.size()) break;
            }
        }
    }
    @Override
    public void renderContent(MatrixStack matrices, int mouseX, int mouseY) {
        screen.getTextRenderer().drawWithShadow(matrices, new TranslatableText("microchip.menu.microchipInfo.inventoryTab"), (float) (window.getX() + 7), (float) (window.getY() + 105), 0xFFFFFF);

        // Draw inventory spaces
        List<ItemSlot> slots = getAllSlots();

        int windowX = window.getX();
        int windowY = window.getY();

        RenderSystem.disableDepthTest();
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for (ItemSlot itemSlot : slots) {
            RenderSystem.setShaderTexture(0, TEXTURE);
            int slotX = itemSlot.getX(windowX);
            int slotY = itemSlot.getY(windowY);

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            screen.drawTexture(matrices, slotX, slotY, 168, 159, 18, 18);

            ItemStack itemStack = itemSlot.getItemStack();

            drawItem(itemStack, slotX + 1, slotY + 1, itemStack.getCount() <= 1 ? "" : Integer.toString(itemStack.getCount()));

            if (itemSlot.isHovered(windowX, windowY, mouseX, mouseY)) drawSlotHighlight(matrices, slotX, slotY, 0);
        }

        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableDepthTest();
    }

    @Override
    public void renderTooltips(MatrixStack matrices, int mouseX, int mouseY) {
        List<ItemSlot> slots = getAllSlots();
        for (ItemSlot itemSlot : slots) {
            if (itemSlot.isHovered(window.getX(), window.getY(), mouseX, mouseY)) {
                drawItemTooltip(matrices, itemSlot.getItemStack(), mouseX, mouseY);
            }
        }
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

    private List<ItemSlot> getAllSlots() {
        return Stream.of(handSlots, armorSlots, inventorySlots)
                .flatMap(Collection::stream).toList();
    }

    public void setInventoryList(List<ItemStack> inventoryList) {
        this.inventoryList = inventoryList;
        populateSlots();
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

    private void drawSlotHighlight(MatrixStack matrices, int x, int y, int z) {
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        screen.drawGradient(matrices, x + 1, y + 1, x + 17, y + 17, -2130706433, -2130706433, z);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }

    protected void drawItemTooltip(MatrixStack matrices, ItemStack stack, int x, int y) {
        screen.renderTooltip(matrices, screen.getTooltipFromItem(stack), stack.getTooltipData(), x, y);
    }

    private static class ItemSlot {
        private final int x, y;
        private ItemStack itemStack;

        public ItemSlot(int x, int y) {
            this.x = x;
            this.y = y;
            this.itemStack = ItemStack.EMPTY;
        }

        public void setItemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        public int getX(int windowX) {
            return windowX + x;
        }

        public int getY(int windowY) {
            return windowY + y;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public boolean isHovered(int windowX, int windowY, int mouseX, int mouseY) {
            return ScreenUtils.isWithin(mouseX, mouseY, getX(windowX), getY(windowY), 18, 18);
        }
    }
}
