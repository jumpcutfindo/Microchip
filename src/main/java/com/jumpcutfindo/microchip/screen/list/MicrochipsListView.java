package com.jumpcutfindo.microchip.screen.list;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.client.ClientNetworker;
import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
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
    private final int buttonWidth, buttonHeight;
    private final int editGroupX, editGroupY;
    private final int deleteGroupX, deleteGroupY;
    private final int deleteMicrochipsX, deleteMicrochipsY;
    private final int moveMicrochipsX, moveMicrochipsY;
    private final LiteralText title;

    private final Color primaryColor;

    private Runnable renderTooltip;
    public MicrochipsListView(MicrochipsMenuScreen screen, MicrochipGroup microchipGroup) {
        super(screen,
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

        this.editGroupX = 154;
        this.editGroupY = 6;

        this.deleteGroupX = 182;
        this.deleteGroupY = 6;

        this.moveMicrochipsX = 154;
        this.moveMicrochipsY = 6;

        this.deleteMicrochipsX = 182;
        this.deleteMicrochipsY = 6;

        this.buttonWidth = 26;
        this.buttonHeight = 16;

        this.primaryColor = new Color(this.group.getColor().getPrimaryColor());
    }

    @Override
    public void renderBackground(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        if (this.group.getColor() != GroupColor.GRAY) {
            float r = (float) primaryColor.getRed() / 255.0f;
            float g = (float) primaryColor.getGreen() / 255.0f;
            float b = (float) primaryColor.getBlue() / 255.0f;

            RenderSystem.setShaderColor(r, g, b, 0.1f);
        }

        super.renderBackground(matrices, x, y, mouseX, mouseY);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.screen.getTextRenderer().draw(matrices, this.title, (float) (x + this.titleX), (float) (y + this.titleY), 0x404040);
    }

    @Override
    public void renderItems(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        super.renderItems(matrices, x, y, mouseX, mouseY);
        this.drawButtons(matrices, x, y, mouseX, mouseY);

        // Render tooltip on top of everything else
        if (this.renderTooltip != null) this.renderTooltip.run();
        this.renderTooltip = null;
    }

    private void drawButtons(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, MicrochipsMenuScreen.BUTTONS_TEXTURE);

        // Swap the buttons depending on whether any items were selected
        if (this.isAnySelected()) {
            this.drawMoveItemsButton(matrices, x + moveMicrochipsX, y + moveMicrochipsY, mouseX, mouseY);
            this.drawDeleteItemsButton(matrices, x + deleteMicrochipsX, y + deleteMicrochipsY, mouseX, mouseY);
        } else {
            this.drawEditGroupButton(matrices, x + editGroupX, y + editGroupY, mouseX, mouseY);
            this.drawDeleteGroupButton(matrices, x + deleteGroupX, y + deleteGroupY, mouseX, mouseY);
        }
    }

    private void drawEditGroupButton(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, MicrochipsMenuScreen.BUTTONS_TEXTURE);

        if (MicrochipsMenuScreen.isWithin(mouseX, mouseY, x, y, buttonWidth, buttonHeight)) {
            // Hovered
            this.screen.drawTexture(matrices, x, y, 26, 32, buttonWidth, buttonHeight);
            this.renderTooltip = () -> this.screen.renderTooltip(matrices, new TranslatableText("microchip.menu.editGroup.tooltip"), mouseX, mouseY);
        } else {
            // Default
            this.screen.drawTexture(matrices, x, y, 0, 32, buttonWidth, buttonHeight);
        }
    }

    private void drawDeleteGroupButton(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, MicrochipsMenuScreen.BUTTONS_TEXTURE);
        if (group.isDefault()) {
            // Disabled
            this.screen.drawTexture(matrices, x, y, 78, 16, buttonWidth, buttonHeight);
            return;
        };

        if (MicrochipsMenuScreen.isWithin(mouseX, mouseY, x, y, buttonWidth, buttonHeight)) {
            // Hovered
            this.screen.drawTexture(matrices, x, y, 26, 16, buttonWidth, buttonHeight);
            this.renderTooltip = () -> this.screen.renderTooltip(matrices, new TranslatableText("microchip.menu.deleteGroup.tooltip"), mouseX, mouseY);
        } else {
            // Default
            this.screen.drawTexture(matrices, x, y, 0, 16, buttonWidth, buttonHeight);
        }
    }

    private void drawDeleteItemsButton(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, MicrochipsMenuScreen.BUTTONS_TEXTURE);
        if (this.isAnySelected()) {
            if (MicrochipsMenuScreen.isWithin(mouseX, mouseY, x, y, buttonWidth, buttonHeight)) {
                // Hovered
                this.screen.drawTexture(matrices, x, y, 130, 0, buttonWidth, buttonHeight);
                this.renderTooltip = () -> this.screen.renderTooltip(matrices, new TranslatableText("microchip.menu.deleteMicrochips.tooltip"), mouseX, mouseY);
            } else {
                // Normal
                this.screen.drawTexture(matrices, x, y, 104, 0, buttonWidth, buttonHeight);
            }
        } else {
            // Inactive
            this.screen.drawTexture(matrices, x, y, 182, 0, buttonWidth, buttonHeight);
        }
    }

    private void drawMoveItemsButton(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, MicrochipsMenuScreen.BUTTONS_TEXTURE);
        if (this.isAnySelected()) {
            if (MicrochipsMenuScreen.isWithin(mouseX, mouseY, x, y, buttonWidth, buttonHeight)) {
                // Hovered
                this.screen.drawTexture(matrices, x, y, 130, 16, buttonWidth, buttonHeight);
                this.renderTooltip = () -> this.screen.renderTooltip(matrices, new TranslatableText("microchip.menu.moveMicrochips.tooltip"), mouseX, mouseY);
            } else {
                // Normal
                this.screen.drawTexture(matrices, x, y, 104, 16, buttonWidth, buttonHeight);
            }
        } else {
            // Inactive
            this.screen.drawTexture(matrices, x, y, 182, 16, buttonWidth, buttonHeight);
        }
    }

    @Override
    public boolean mouseClicked(int x, int y, double mouseX, double mouseY, int button) {
        if (group == null) return false;

        if (!this.isAnySelected()) {
            // Delete Group
            if (MicrochipsMenuScreen.isWithin(mouseX, mouseY, x + deleteGroupX, y + deleteGroupY, buttonWidth, buttonHeight)) {
                if (group.isDefault()) return false;
                ClientNetworker.sendDeleteGroupPacket(this.group.getId());
                return true;
            }

            // Edit Group
            if (MicrochipsMenuScreen.isWithin(mouseX, mouseY, x + editGroupX, y + editGroupY, buttonWidth, buttonHeight)) {
                // TODO: Implement opening of edit window
            }
        } else {
            // Move Microchips
            if (MicrochipsMenuScreen.isWithin(mouseX, mouseY, x + moveMicrochipsX, y + moveMicrochipsY, buttonWidth, buttonHeight)) {
                // TODO: Implement opening of moving window
            }

            // Delete Microchips
            if (MicrochipsMenuScreen.isWithin(mouseX, mouseY, x + deleteMicrochipsX, y + deleteMicrochipsY, buttonWidth, buttonHeight)) {
                // TODO: Send delete microchips packet
            }
        }


        return super.mouseClicked(x, y, mouseX, mouseY, button);
    }

    private static List<ListItem> createItems(MicrochipsMenuScreen screen, MicrochipGroup microchipGroup) {
        if (microchipGroup == null) return new ArrayList<>();

        return microchipGroup.getMicrochips().stream().map(microchip -> new MicrochipListItem(screen, microchipGroup, microchip)).collect(Collectors.toList());
    }
}
