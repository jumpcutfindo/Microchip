package com.jumpcutfindo.microchip.screen.list;

import java.util.List;
import java.util.stream.Collectors;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.Microchips;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.component.IconButton;
import com.jumpcutfindo.microchip.screen.window.MicrochipModifyGroupWindow;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class MicrochipGroupListView extends ListView {
    protected static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_group_list.png");
    private final TranslatableText title;
    private final int titleX, titleY;

    private final IconButton createGroupButton;

    public MicrochipGroupListView(MicrochipsMenuScreen screen, Microchips microchips, int x, int y) {
        super(screen,
                x, y,
                TEXTURE, 0, 0, 160, 178,
                8, 26,
                160, 0, 139, 26,
                createItems(screen, microchips), 8,
                true);

        // Set various variables
        this.title = new TranslatableText("microchip.menu.groupTitle");
        this.titleX = 7;
        this.titleY = 9;

        this.createGroupButton = new IconButton(screen, x + 126, y + 6, 0, 0, this::onCreateGroup, new TranslatableText("microchip.menu.createGroup.tooltip"));
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
        if (this.createGroupButton.mouseClicked((int) mouseX, (int) mouseY, button)) return true;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void onCreateGroup() {
        this.screen.setActiveWindow(MicrochipModifyGroupWindow.createCreateWindow(this.screen));
    }

    private void drawButtons(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, MicrochipsMenuScreen.BUTTONS_TEXTURE);

        this.createGroupButton.render(matrices, mouseX, mouseY, 0);
        this.createGroupButton.renderTooltip(matrices, mouseX, mouseY, 0);
    }

    private static List<ListItem> createItems(MicrochipsMenuScreen screen, Microchips microchips) {
        return microchips.getAllGroups().stream().map(group -> new MicrochipGroupListItem(screen, group, microchips.getAllGroups().indexOf(group))).collect(Collectors.toList());
    }
}
