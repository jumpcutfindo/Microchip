package com.jumpcutfindo.microchip.screen.list;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.client.network.ClientNetworkSender;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.jumpcutfindo.microchip.data.Microchips;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.component.IconButton;
import com.jumpcutfindo.microchip.screen.window.MicrochipModifyGroupWindow;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MicrochipGroupListView extends ListView<MicrochipGroupListItem> {
    protected static final Identifier TEXTURE = Identifier.of(MicrochipMod.MOD_ID, "textures/gui/microchip_group_list.png");
    protected static final int TEXTURE_WIDTH = 256;
    protected static final int TEXTURE_HEIGHT = 256;

    private MutableText title;
    private final int titleX, titleY;

    private final IconButton createGroupButton, reorderGroupButton;
    private boolean canCreate;
    private boolean isReordering;

    public MicrochipGroupListView(MicrochipsMenuScreen screen, Microchips microchips, int x, int y) {
        super(screen);

        this.setPosition(x, y)
                .setTexture(TEXTURE, 0, 0, 160, 178)
                .setListPosition(8, 26)
                .setScrollbar(139, 26, 160, 0, 14, 144)
                .setList(createItems(screen, microchips), 8)
                .setSingleSelect(true);

        // Set various variables
        this.title = Text.translatable("microchip.menu.groupTitle");
        this.titleX = 7;
        this.titleY = 10;

        this.createGroupButton = new IconButton(screen, x + 118, y + 6, 0, 0, this::onCreateGroup, Text.translatable("microchip.menu.createGroup.tooltip"));
        this.reorderGroupButton = new IconButton(screen, x + 136, y + 6, 0, 48, this::toggleReordering, Text.translatable("microchip.menu.reorderGroup.tooltip"));
        this.canCreate = true;

        this.toggleSelected(0);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        super.renderBackground(context, mouseX, mouseY);

        context.drawText(this.screen.getTextRenderer(), this.title, (x + this.titleX), (y + this.titleY), 0x404040, false);
    }

    @Override
    public void renderItems(DrawContext context, int mouseX, int mouseY) {
        super.renderItems(context, mouseX, mouseY);
        this.drawButtons(context, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (this.canCreate) {
            if (this.createGroupButton.mouseClicked(mouseX, mouseY, button)) return true;
            else if (this.reorderGroupButton.mouseClicked(mouseX, mouseY, button)) return true;
        }

        boolean flag = super.mouseClicked(mouseX, mouseY, button);
        if (flag && this.canCreate) screen.setSelectedGroup(this.getSelectedIndex());
        return flag;
    }

    @Override
    public NbtCompound getSettings() {
        NbtCompound cpd = new NbtCompound();
        cpd.putUuid("SelectedItem", getSelectedItem().getItem().getId());
        cpd.putBoolean("IsReordering", isReordering);
        cpd.putFloat("ScrollPosition", this.getScrollPosition());

        return cpd;
    }

    @Override
    public void applySettings(NbtCompound settings) {
        this.setSelected(settings.getUuid("SelectedItem"));
        this.setReorderGroups(settings.getBoolean("IsReordering"));
        this.setScrollPosition(settings.getFloat("ScrollPosition"));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }

    public void setSelected(UUID groupId) {
        for (int i = 0; i < this.listItems.size(); i++) {
            MicrochipGroupListItem item = this.listItems.get(i);
            if (item.getItem().getId().equals(groupId)) {
                this.toggleSelected(i);
                screen.setSelectedGroup(i);
                return;
            }
        }
    }

    public void setTexture(Identifier texture) {
        this.texture = texture;
    }

    public void setTitle(MutableText title) {
        this.title = title;
    }

    public void setCanCreate(boolean canCreate) {
        this.canCreate = canCreate;
    }

    public void setTextureDims(int width, int height) {
        this.textureWidth = width;
        this.textureHeight = height;
    }

    public MicrochipGroupListItem getSelectedItem() {
        return this.getSelectedItems().size() > 0 ? this.getSelectedItems().get(0) : null;
    }

    public int getSelectedIndex() {
        return this.getSelectedIndices().size() > 0 ? (int) this.getSelectedIndices().get(0) : 0;
    }

    private void onCreateGroup() {
        this.screen.setActiveWindow(MicrochipModifyGroupWindow.createCreateWindow(this.screen));
    }

    private void toggleReordering() {
        this.setReorderGroups(!this.isReordering);
    }

    private void setReorderGroups(boolean isReordering) {
        this.isReordering = isReordering;

        this.reorderGroupButton.setActive(this.isReordering);

        for (MicrochipGroupListItem item : this.listItems) {
            item.setReordering(this.isReordering);
        }
    }

    private void drawButtons(DrawContext context, int mouseX, int mouseY) {
        if (this.canCreate) {
            this.reorderGroupButton.render(context, mouseX, mouseY, 0);

            this.createGroupButton.render(context, mouseX, mouseY, 0);

            if (!screen.isWindowOpen()) {
                this.reorderGroupButton.renderTooltip(context, mouseX, mouseY, 0);
                this.createGroupButton.renderTooltip(context, mouseX, mouseY, 0);
            }
        }

    }

    private static List<MicrochipGroupListItem> createItems(MicrochipsMenuScreen screen, Microchips microchips) {
        List<MicrochipGroupListItem> listItems = new ArrayList<>();

        List<MicrochipGroup> microchipGroups = microchips.getAllGroups();

        for (int i = 0; i < microchipGroups.size(); i++) {
            MicrochipGroup group = microchipGroups.get(i);
            MicrochipGroupListItem item = new MicrochipGroupListItem(screen, group, i);
            item.setMoveAction(MicrochipGroupListView::onReorder);

            if (group.isDefault()) item.setReorderable(false);

            listItems.add(item);
        }

        return listItems;
    }

    private static void onReorder(int from, int to) {
        ClientNetworkSender.MicrochipsActions.reorderGroup(from, to);
    }
}
