package com.jumpcutfindo.microchip.screen.list;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.Microchips;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.component.IconButton;
import com.jumpcutfindo.microchip.screen.window.MicrochipModifyGroupWindow;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.stream.Collectors;

public class MicrochipGroupListView extends ListView {
    protected static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_group_list.png");
    private MutableText title;
    private final int titleX, titleY;

    private final IconButton createGroupButton;
    private boolean canCreate;

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

        this.createGroupButton = new IconButton(screen, x + 126, y + 6, 0, 0, this::onCreateGroup, Text.translatable("microchip.menu.createGroup.tooltip"));
        this.canCreate = true;

        this.setSelected(0);
    }

    @Override
    public void renderBackground(MatrixStack matrices, int mouseX, int mouseY) {
        super.renderBackground(matrices, mouseX, mouseY);

        this.screen.getTextRenderer().draw(matrices, this.title, (float) (x + this.titleX), (float) (y + this.titleY), 0x404040);
    }

    @Override
    public void renderItems(MatrixStack matrices, int mouseX, int mouseY) {
        super.renderItems(matrices, mouseX, mouseY);
        this.drawButtons(matrices, x, y, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (this.canCreate && this.createGroupButton.mouseClicked(mouseX, mouseY, button)) return true;

        boolean flag = super.mouseClicked(mouseX, mouseY, button);
        if (flag && this.canCreate) screen.setSelectedGroup(this.getSelectedIndex());
        return flag;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return false;
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
        return this.getSelectedItems().size() > 0 ? (MicrochipGroupListItem) this.getSelectedItems().get(0) : null;
    }

    public int getSelectedIndex() {
        return this.getSelectedIndices().size() > 0 ? this.getSelectedIndices().get(0) : 0;
    }

    private void onCreateGroup() {
        this.screen.setActiveWindow(MicrochipModifyGroupWindow.createCreateWindow(this.screen));
    }

    private void drawButtons(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, MicrochipsMenuScreen.BUTTONS_TEXTURE);

        if (this.canCreate) {
            this.createGroupButton.render(matrices, mouseX, mouseY, 0);

            if (!screen.isWindowOpen()) this.createGroupButton.renderTooltip(matrices, mouseX, mouseY, 0);
        }
    }

    private static List<ListItem> createItems(MicrochipsMenuScreen screen, Microchips microchips) {
        return microchips.getAllGroups().stream().map(group -> new MicrochipGroupListItem(screen, group, microchips.getAllGroups().indexOf(group))).collect(Collectors.toList());
    }
}
