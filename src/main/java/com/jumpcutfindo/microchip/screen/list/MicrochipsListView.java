package com.jumpcutfindo.microchip.screen.list;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.client.network.ClientNetworkSender;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.ScreenUtils;
import com.jumpcutfindo.microchip.screen.component.IconButton;
import com.jumpcutfindo.microchip.screen.window.MicrochipModifyGroupWindow;
import com.jumpcutfindo.microchip.screen.window.MicrochipMoveChipsWindow;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MicrochipsListView extends ListView<MicrochipListItem> {
    protected static final Identifier TEXTURE = Identifier.of(MicrochipMod.MOD_ID, "textures/gui/microchip_list.png");
    private final MicrochipGroup group;

    private final int titleX, titleY;
    private final IconButton editGroupButton, deleteGroupButton,
            reorderMicrochipsButton, moveMicrochipsButton, deleteMicrochipsButton,
            selectAllButton, unselectAllButton;
    private final Text title;
    private boolean isReordering;
    public MicrochipsListView(MicrochipsMenuScreen screen, MicrochipGroup microchipGroup, int x, int y) {
        super(screen);

        this.setPosition(x, y)
                .setTexture(TEXTURE, 0, 0, 216, 178)
                .setListPosition(8, 26)
                .setScrollbar(195, 26, 216, 0, 14, 144)
                .setList(createItems(screen, microchipGroup), 4)
                .setSingleSelect(false);

        this.group = microchipGroup;

        // Set various variables
        if (microchipGroup != null)  {
            this.title = Text.literal(microchipGroup.getDisplayName());
        } else {
            this.title = Text.literal("");
        }

        this.titleX = 22;
        this.titleY = 10;

        this.editGroupButton = new IconButton(screen, x + 137, y + 6, 0, 32, this::onEditGroup, Text.translatable("microchip.menu.editGroup.tooltip"));
        this.deleteGroupButton = new IconButton(screen, x + 155, y + 6, 0, 16, this::onDeleteGroup, Text.translatable("microchip.menu.deleteGroup.tooltip"));
        if (group.isDefault()) this.deleteGroupButton.setDisabled(true);
        this.reorderMicrochipsButton = new IconButton(screen, x + 173, y + 6, 64, 32, this::toggleReordering, Text.translatable("microchip.menu.reorderMicrochips.tooltip"));

        this.moveMicrochipsButton = new IconButton(screen, x + 155, y + 6, 64, 16, this::onMoveMicrochips, Text.translatable("microchip.menu.moveMicrochips.tooltip"));
        this.deleteMicrochipsButton = new IconButton(screen, x + 173, y + 6, 64, 0, this::onDeleteMicrochips, Text.translatable("microchip.menu.deleteMicrochips.tooltip"));

        this.selectAllButton = new IconButton(screen, x + 191, y + 6, 64, 48, this::onSelectAllMicrochips, Text.translatable("microchip.menu.selectAllMicrochips.tooltip"));
        this.unselectAllButton = new IconButton(screen, x + 191, y + 6, 64, 64, this::onUnselectAllMicrochips, Text.translatable("microchip.menu.unselectAllMicrochips.tooltip"));
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY) {
        ScreenUtils.setShaderColor(this.group.getColor(), true);

        super.renderBackground(context, mouseX, mouseY);

        context.drawText(this.screen.getTextRenderer(), this.title, (x + this.titleX), (y + this.titleY), this.group.getColor().getShadowColor(), false);

        ScreenUtils.setShaderColor(group.getColor(), false);
        context.drawTexture(MicrochipGroupListView.TEXTURE, x + 5, y + 6, group.getColor().ordinal() * 16, 214, 16, 16);
    }

    @Override
    public void renderItems(DrawContext context, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        super.renderItems(context, mouseX, mouseY);
        this.drawButtons(context, mouseX, mouseY);
    }

    private void drawButtons(DrawContext context, int mouseX, int mouseY) {
        // Swap selection buttons depending on number selected
        boolean isListEmpty = this.listItems.isEmpty();
        this.selectAllButton.setDisabled(isListEmpty || isReordering);
        this.unselectAllButton.setDisabled(isListEmpty || isReordering);
        this.reorderMicrochipsButton.setDisabled(isListEmpty);

        boolean isAllSelected = this.isAllSelected();
        boolean isAnySelected = this.isAnySelected();

        // Render buttons
        if (isAllSelected) {
            this.unselectAllButton.render(context, mouseX, mouseY, 0);
        } else {
            this.selectAllButton.render(context, mouseX, mouseY, 0);
        }

        // Swap the buttons depending on whether any items were selected
        if (this.isAnySelected()) {
            this.moveMicrochipsButton.render(context, mouseX, mouseY, 0);
            this.deleteMicrochipsButton.render(context, mouseX, mouseY, 0);
        } else {
            this.reorderMicrochipsButton.render(context, mouseX, mouseY, 0);
            this.editGroupButton.render(context, mouseX, mouseY, 0);
            this.deleteGroupButton.render(context, mouseX, mouseY, 0);
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        // Render tooltips
        if (!screen.isWindowOpen()) {
            if (isAllSelected) this.unselectAllButton.renderTooltip(context, mouseX, mouseY, 0);
            else this.selectAllButton.renderTooltip(context, mouseX, mouseY, 0);

            if (isAnySelected) {
                this.moveMicrochipsButton.renderTooltip(context, mouseX, mouseY, 0);
                this.deleteMicrochipsButton.renderTooltip(context, mouseX, mouseY, 0);
            } else {
                this.reorderMicrochipsButton.renderTooltip(context, mouseX, mouseY, 0);
                this.editGroupButton.renderTooltip(context, mouseX, mouseY, 0);
                this.deleteGroupButton.renderTooltip(context, mouseX, mouseY, 0);
            }
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (group == null) return false;

        int start = getLastToggledIndex();
        if (super.mouseClicked(mouseX, mouseY, button)) {
            if (Screen.hasShiftDown()) {
                this.resetSelection();

                int end = getLastToggledIndex();

                int smaller = Math.min(start, end);
                int larger = Math.max(start, end);

                for (int i = smaller; i <= larger; i++) {
                    this.setSelected(i, true);
                }

                this.setLastToggledIndex(start);
            }
            return true;
        }

        // If all selected, only consider unselect button
        boolean flag = false;
        if (this.isAllSelected()) {
            flag = this.unselectAllButton.mouseClicked(mouseX, mouseY, button);
        } else {
            flag = this.selectAllButton.mouseClicked(mouseX, mouseY, button);
        }
        if (flag) return true;

        // Consider the rest of the buttons
        if (this.isAnySelected()) {
            return this.moveMicrochipsButton.mouseClicked(mouseX, mouseY, button)
                    || this.deleteMicrochipsButton.mouseClicked(mouseX, mouseY, button);
        } else {
            return this.editGroupButton.mouseClicked(mouseX, mouseY, button)
                    || this.deleteGroupButton.mouseClicked(mouseX, mouseY, button)
                    || this.reorderMicrochipsButton.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public NbtCompound getSettings() {
        NbtCompound cpd = new NbtCompound();
        cpd.putBoolean("IsReordering", isReordering);
        cpd.putFloat("ScrollPosition", this.getScrollPosition());

        return cpd;
    }

    @Override
    public void applySettings(NbtCompound settings) {
        this.setReordering(settings.getBoolean("IsReordering"));
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

    private List<UUID> getSelectedIds() {
        return this.getSelectedItems().stream().map(item -> ((MicrochipListItem) item).getMicrochip().getEntityId()).toList();
    }

    private void onEditGroup() {
        this.screen.setActiveWindow(MicrochipModifyGroupWindow.createEditWindow(this.screen, this.group));
    }

    private void onDeleteGroup() {
        if (group.isDefault()) return;
        ClientNetworkSender.MicrochipsActions.deleteGroup(this.group.getId());
    }

    private void onMoveMicrochips() {
        this.screen.setActiveWindow(new MicrochipMoveChipsWindow(screen, screen.getWindowX(MicrochipMoveChipsWindow.WIDTH), screen.getWindowY(MicrochipMoveChipsWindow.HEIGHT), screen.getMicrochips(), this.group, getSelectedIds()));
    }

    private void onDeleteMicrochips() {
        if (!this.isAnySelected()) return;
        List<UUID> microchipIds = getSelectedIds();
        ClientNetworkSender.MicrochipsActions.removeEntitiesFromGroup(this.group.getId(), microchipIds);
    }

    private void onSelectAllMicrochips() {
        for (int i = 0; i < listItems.size(); i++) this.setSelected(i, true);
    }

    private void onUnselectAllMicrochips() {
        this.resetSelection();
    }

    private void toggleReordering() {
        this.setReordering(!this.isReordering);
    }

    public void setReordering(boolean reordering) {
        this.isReordering = reordering;

        this.reorderMicrochipsButton.setActive(this.isReordering);

        for (MicrochipListItem item : this.listItems) {
            item.setReordering(this.isReordering);
        }
    }

    private static List<MicrochipListItem> createItems(MicrochipsMenuScreen screen, MicrochipGroup microchipGroup) {
        if (microchipGroup == null) return new ArrayList<>();

        List<MicrochipListItem> items = new ArrayList<>();
        List<Microchip> microchips = microchipGroup.getMicrochips();

        for (int i = 0; i < microchips.size(); i++) {
            Microchip microchip = microchips.get(i);
            MicrochipListItem item = new MicrochipListItem(screen, microchipGroup, microchip, i);

            items.add(item);
            item.setMoveAction(MicrochipsListView::onReorder);
        }

        return items;
    }

    private static void onReorder(MicrochipGroup group, int from, int to) {
        ClientNetworkSender.MicrochipsActions.reorderMicrochips(group.getId(), from, to);
    }
}
