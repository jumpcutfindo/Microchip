package com.jumpcutfindo.microchip.screen;

import java.util.List;
import java.util.stream.Collectors;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.Microchips;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class MicrochipGroupListView extends ListView {
    protected static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_group_list.png");
    private final TranslatableText title;
    private final int titleX, titleY;

    private final int addButtonX, addButtonY;
    private final int buttonWidth, buttonHeight;

    public MicrochipGroupListView(MicrochipsMenuScreen screen, Microchips microchips) {
        super(screen,
                TEXTURE, 0, 0, 160, 178,
                8, 26,
                160, 0, 139, 26,
                createItems(screen, microchips), 8);

        // Set various variables
        this.title = new TranslatableText("microchip.menu.groupTitle");
        this.titleX = 7;
        this.titleY = 9;

        this.addButtonX = 126;
        this.addButtonY = 6;

        this.buttonWidth = 26;
        this.buttonHeight = 16;
    }
    @Override
    public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        super.render(matrices, x, y, mouseX, mouseY);

        this.screen.getTextRenderer().draw(matrices, this.title, (float) (x + this.titleX), (float) (y + this.titleY), 0x404040);
        this.drawButtons(matrices, x, y, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(int x, int y, double mouseX, double mouseY, int button) {
        if (MicrochipsMenuScreen.isWithin(mouseX, mouseY, x + addButtonX, y + addButtonY, buttonWidth, buttonHeight)) {
            // Add clicked
            this.screen.setActiveWindow(new MicrochipCreateGroupWindow(this.screen));
            return true;
        }

        return super.mouseClicked(x, y, mouseX, mouseY, button);
    }

    private void drawButtons(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        this.drawAddButton(matrices, x + addButtonX, y + addButtonY, mouseX, mouseY);
    }

    private void drawAddButton(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        if (MicrochipsMenuScreen.isWithin(mouseX, mouseY, x, y, buttonWidth, buttonHeight)) {
            // Hovered
            this.screen.drawTexture(matrices, x, y , 186, 15, buttonWidth, buttonHeight);
        } else {
            // Default
            this.screen.drawTexture(matrices, x, y, 160, 15, buttonWidth, buttonHeight);
        }
    }

    private void drawDeleteButton(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        if (MicrochipsMenuScreen.isWithin(mouseX, mouseY, x, y, buttonWidth, buttonHeight)) {
            // Hovered
            this.screen.drawTexture(matrices, x, y, 186, 47, buttonWidth, buttonHeight);
        } else {
            // Default
            this.screen.drawTexture(matrices, x, y, 160, 47, buttonWidth, buttonHeight);
        }
    }

    private static List<ListItem> createItems(MicrochipsMenuScreen screen, Microchips microchips) {
        return microchips.getGroups().stream().map(group -> new MicrochipGroupListItem(screen, group, microchips.getGroups().indexOf(group))).collect(Collectors.toList());
    }
}
