package com.jumpcutfindo.microchip.screen.list;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.client.ClientNetworker;
import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.component.IconButton;
import com.jumpcutfindo.microchip.screen.window.MicrochipModifyGroupWindow;
import com.jumpcutfindo.microchip.screen.window.MicrochipMoveChipsWindow;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class MicrochipsListView extends ListView {
    protected static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_list.png");
    private final MicrochipGroup group;

    private final int titleX, titleY;
    private final IconButton editGroupButton, deleteGroupButton, moveMicrochipsButton, deleteMicrochipsButton;
    private final LiteralText title;

    private final Color primaryColor;
    public MicrochipsListView(MicrochipsMenuScreen screen, MicrochipGroup microchipGroup, int x, int y) {
        super(screen,
                x, y,
                TEXTURE, 0, 0, 216, 178,
                8, 26,
                216, 0, 195, 26,
                createItems(screen, microchipGroup), 4,
                false);

        this.group = microchipGroup;

        // Set various variables
        if (microchipGroup != null)  {
            this.title = new LiteralText(microchipGroup.getDisplayName());
        } else {
            this.title = new LiteralText("");
        }

        this.titleX = 7;
        this.titleY = 9;

        this.editGroupButton = new IconButton(screen, x + 154, y + 6, 0, 32, this::onEditGroup, new TranslatableText("microchip.menu.editGroup.tooltip"));
        this.deleteGroupButton = new IconButton(screen, x + 182, y + 6, 0, 16, this::onDeleteGroup, new TranslatableText("microchip.menu.deleteGroup.tooltip"));
        if (group.isDefault()) this.deleteGroupButton.setDisabled(true);

        this.moveMicrochipsButton = new IconButton(screen, x + 154, y + 6, 104, 16, this::onMoveMicrochips, new TranslatableText("microchip.menu.moveMicrochips.tooltip"));
        this.deleteMicrochipsButton = new IconButton(screen, x + 182, y + 6, 104, 0, this::onDeleteMicrochips, new TranslatableText("microchip.menu.deleteMicrochips.tooltip"));

        this.primaryColor = new Color(this.group.getColor().getPrimaryColor());
    }

    @Override
    public void renderBackground(MatrixStack matrices, int mouseX, int mouseY) {
        if (this.group.getColor() != GroupColor.GRAY) {
            Color color = primaryColor;
            float r = (float) color.getRed() / 204.0f;
            float g = (float) color.getGreen() / 204.0f;
            float b = (float) color.getBlue() / 204.0f;
            float a = 0.1f;

            RenderSystem.setShaderColor(r, g, b, 0.0f);
        }

        super.renderBackground(matrices, mouseX, mouseY);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.screen.getTextRenderer().draw(matrices, this.title, (float) (x + this.titleX), (float) (y + this.titleY), this.group.getColor().getShadowColor());
    }

    @Override
    public void renderItems(MatrixStack matrices, int mouseX, int mouseY) {
        super.renderItems(matrices, mouseX, mouseY);
        this.drawButtons(matrices, mouseX, mouseY);
    }

    private void drawButtons(MatrixStack matrices, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, MicrochipsMenuScreen.BUTTONS_TEXTURE);

        // Swap the buttons depending on whether any items were selected
        if (this.isAnySelected()) {
            this.moveMicrochipsButton.render(matrices, mouseX, mouseY, 0);
            this.deleteMicrochipsButton.render(matrices, mouseX, mouseY, 0);

            this.moveMicrochipsButton.renderTooltip(matrices, mouseX, mouseY, 0);
            this.deleteMicrochipsButton.renderTooltip(matrices, mouseX, mouseY, 0);
        } else {
            this.editGroupButton.render(matrices, mouseX, mouseY, 0);
            this.deleteGroupButton.render(matrices, mouseX, mouseY, 0);

            this.editGroupButton.renderTooltip(matrices, mouseX, mouseY, 0);
            this.deleteGroupButton.renderTooltip(matrices, mouseX, mouseY, 0);
        }
    }

    private void onEditGroup() {
        this.screen.setActiveWindow(MicrochipModifyGroupWindow.createEditWindow(this.screen, this.group));
    }

    private void onDeleteGroup() {
        if (group.isDefault()) return;
        ClientNetworker.sendDeleteGroupPacket(this.group.getId());
    }

    private void onMoveMicrochips() {
        this.screen.setActiveWindow(new MicrochipMoveChipsWindow(screen, screen.getMicrochips(), this.group, getSelectedIds()));
    }

    private void onDeleteMicrochips() {
        if (!this.isAnySelected()) return;
        List<UUID> microchipIds = getSelectedIds();
        ClientNetworker.sendRemoveEntitiesFromGroupPacket(this.group.getId(), microchipIds);
    }

    private List<UUID> getSelectedIds() {
        return this.getSelectedItems().stream().map(item -> ((MicrochipListItem) item).getMicrochip().getEntityId()).toList();
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (group == null) return false;

        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        if (this.isAnySelected()) {
            return this.moveMicrochipsButton.mouseClicked(mouseX, mouseY, button)
                    || this.deleteMicrochipsButton.mouseClicked(mouseX, mouseY, button);
        } else {
            return this.editGroupButton.mouseClicked(mouseX, mouseY, button)
                    || this.deleteGroupButton.mouseClicked( mouseX, mouseY, button);
        }
    }

    private static List<ListItem> createItems(MicrochipsMenuScreen screen, MicrochipGroup microchipGroup) {
        if (microchipGroup == null) return new ArrayList<>();

        return microchipGroup.getMicrochips().stream().map(microchip -> new MicrochipListItem(screen, microchipGroup, microchip)).collect(Collectors.toList());
    }
}