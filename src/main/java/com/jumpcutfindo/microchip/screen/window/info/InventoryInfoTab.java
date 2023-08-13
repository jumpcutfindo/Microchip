package com.jumpcutfindo.microchip.screen.window.info;

import com.google.common.collect.ImmutableList;
import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.screen.MicrochipScreen;
import com.jumpcutfindo.microchip.screen.ScreenUtils;
import com.jumpcutfindo.microchip.screen.window.MicrochipInfoWindow;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static com.jumpcutfindo.microchip.screen.window.MicrochipInfoWindow.TEXTURE;

public class InventoryInfoTab extends InfoTab {
    private List<ItemStack> inventoryList;
    private List<ItemSlot> handSlots, armorSlots, inventorySlots;
    private int inventorySize;

    public InventoryInfoTab(MicrochipScreen screen, MicrochipInfoWindow window, Microchip microchip, GroupColor color, LivingEntity entity) {
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
            ItemSlot handSlot = new ItemSlot(xOffset + i * 18, yOffset);
            handSlot.setSpecial(true);
            handSlot.setSpecialUV(168 + i * 16, 177);
            handSlots.add(handSlot);
        }

        armorSlots = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ItemSlot armorSlot = new ItemSlot(xOffset + 54 + i * 18, yOffset);
            armorSlot.setSpecial(true);
            armorSlot.setSpecialUV(168 + i * 16, 193);
            armorSlots.add(armorSlot);
        }

        inventorySlots = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 8; j++) {
                int x = xOffset + j * 18;
                int y = yOffset + 32 + i * 18;
                inventorySlots.add(new ItemSlot(x, y));
            }
        }
    }

    private void populateSlots() {
        if (entity != null) {
            int handIndex = 0;
            for (ItemStack itemStack : entity.getHandItems()) {
                handSlots.get(handIndex).setItemStack(itemStack);
                handIndex++;
            }

            int armorIndex = 3;
            for (ItemStack itemStack : entity.getArmorItems()) {
                armorSlots.get(armorIndex).setItemStack(itemStack);
                armorIndex--;
            }

            int inventoryIndex = 0;
            for (ItemStack itemStack : this.inventoryList) {
                inventorySlots.get(inventoryIndex).setItemStack(itemStack);
                inventoryIndex++;
                // TODO: Improve this handling (in the situation where inventory is larger than size 16)
                if (inventoryIndex >= inventoryList.size()) break;
            }

            for (int i = 0; i < inventorySlots.size(); i++) {
                this.inventorySlots.get(i).setDisabled(i >= inventorySize);
            }
        }
    }
    @Override
    public void renderContent(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.screen.getTextRenderer(), Text.translatable("microchip.menu.microchipInfo.inventoryTab"), (window.getX() + 7), (window.getY() + 105), 0xFFFFFF, true);

        // Draw inventory spaces
        List<ItemSlot> slots = getAllSlots();

        int windowX = window.getX();
        int windowY = window.getY();

        for (ItemSlot itemSlot : slots) {
            int slotX = itemSlot.getX(windowX);
            int slotY = itemSlot.getY(windowY);

            if (itemSlot.isDisabled()) context.drawTexture(TEXTURE, slotX, slotY, 186, 159, 18, 18);
            else context.drawTexture(TEXTURE, slotX, slotY, 168, 159, 18, 18);

            if (itemSlot.isSpecial() && itemSlot.isEmpty()) context.drawTexture(TEXTURE, slotX + 1, slotY + 1, itemSlot.getSpecialU(), itemSlot.getSpecialV(), 16, 16);
        }

        RenderSystem.disableDepthTest();
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(0.0f, 0.0f, 200.0f);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for (ItemSlot itemSlot : slots) {
            int slotX = itemSlot.getX(windowX);
            int slotY = itemSlot.getY(windowY);
            ItemStack itemStack = itemSlot.getItemStack();

            drawItem(context, itemStack, slotX + 1, slotY + 1, itemStack.getCount() <= 1 ? "" : Integer.toString(itemStack.getCount()));

            if (itemSlot.isHovered(windowX, windowY, mouseX, mouseY)) drawSlotHighlight(context, slotX, slotY, 0);
        }

        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableDepthTest();
    }

    @Override
    public void renderTooltips(DrawContext context, int mouseX, int mouseY) {
        List<ItemSlot> slots = getAllSlots();
        for (ItemSlot itemSlot : slots) {
            if (itemSlot.isHovered(window.getX(), window.getY(), mouseX, mouseY) && !itemSlot.isEmpty()) {
                drawItemTooltip(context, itemSlot.getItemStack(), mouseX, mouseY);
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

    public void setInventoryList(List<ItemStack> inventoryList, int inventorySize) {
        this.inventoryList = inventoryList;
        this.inventorySize = inventorySize;
        populateSlots();
    }

    private void drawItem(DrawContext context, ItemStack stack, int x, int y, String amountText) {
        context.drawItem(screen.getPlayer(), stack, x, y, 0);
        context.drawItemInSlot(screen.getTextRenderer(), stack, x, y);
    }

    private void drawSlotHighlight(DrawContext context, int x, int y, int z) {
        context.fillGradient(RenderLayer.getGuiOverlay(), x + 1, y + 1, x + 17, y + 17, -2130706433, -2130706433, z);
    }

    protected void drawItemTooltip(DrawContext context, ItemStack stack, int x, int y) {
        context.drawTooltip(this.screen.getTextRenderer(), Screen.getTooltipFromItem(MinecraftClient.getInstance(), stack), stack.getTooltipData(), x, y);
    }

    private static class ItemSlot {
        private final int x, y;
        private ItemStack itemStack;
        private boolean isSpecial;

        private boolean isDisabled;
        private int specialU, specialV;

        public ItemSlot(int x, int y) {
            this.x = x;
            this.y = y;
            this.itemStack = ItemStack.EMPTY;
        }

        public void setItemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        public void setSpecial(boolean special) {
            isSpecial = special;
        }

        public void setDisabled(boolean disabled) {
            isDisabled = disabled;
        }

        public void setSpecialUV(int u, int v) {
            this.specialU = u;
            this.specialV = v;
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

        public boolean isSpecial() {
            return isSpecial;
        }

        public boolean isDisabled() {
            return isDisabled;
        }

        public int getSpecialU() {
            return specialU;
        }

        public int getSpecialV() {
            return specialV;
        }

        public boolean isHovered(int windowX, int windowY, int mouseX, int mouseY) {
            return ScreenUtils.isWithin(mouseX, mouseY, getX(windowX), getY(windowY), 18, 18);
        }

        public boolean isEmpty() {
            return itemStack.isEmpty();
        }
    }
}
