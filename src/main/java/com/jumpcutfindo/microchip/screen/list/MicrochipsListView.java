package com.jumpcutfindo.microchip.screen.list;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.client.ClientNetworker;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class MicrochipsListView extends ListView {
    protected static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_list.png");
    private final MicrochipGroup group;

    private final int titleX, titleY;
    private final int buttonWidth, buttonHeight;
    private final int deleteGroupButtonX, deleteGroupButtonY;
    private final LiteralText title;
    public MicrochipsListView(MicrochipsMenuScreen screen, MicrochipGroup microchipGroup) {
        super(screen,
                TEXTURE, 0, 0, 216, 178,
                8, 26,
                216, 0, 195, 26,
                createItems(screen, microchipGroup), 4);

        this.group = microchipGroup;

        // Set various variables
        if (microchipGroup != null)  {
            this.title = new LiteralText(microchipGroup.getDisplayName());
        } else {
            this.title = new LiteralText("");
        }

        this.titleX = 7;
        this.titleY = 9;

        this.deleteGroupButtonX = 182;
        this.deleteGroupButtonY = 6;

        this.buttonWidth = 26;
        this.buttonHeight = 16;
    }

    @Override
    public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        if (this.group == null) return;

        super.render(matrices, x, y, mouseX, mouseY);

        this.screen.getTextRenderer().draw(matrices, this.title, (float) (x + this.titleX), (float) (y + this.titleY), 0x404040);
        this.drawButtons(matrices, x, y, mouseX, mouseY);
    }
    private void drawButtons(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        this.drawDeleteButton(matrices, x + deleteGroupButtonX, y + deleteGroupButtonY, mouseX, mouseY);
    }

    private void drawDeleteButton(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        if (group.isDefault()) return;

        if (MicrochipsMenuScreen.isWithin(mouseX, mouseY, x, y, buttonWidth, buttonHeight)) {
            // Hovered
            this.screen.drawTexture(matrices, x, y , 216, 31, buttonWidth, buttonHeight);
        } else {
            // Default
            this.screen.drawTexture(matrices, x, y, 216, 15, buttonWidth, buttonHeight);
        }
    }

    @Override
    public boolean mouseClicked(int x, int y, double mouseX, double mouseY, int button) {
        if (group == null) return false;

        if (MicrochipsMenuScreen.isWithin(mouseX, mouseY, x + deleteGroupButtonX, y + deleteGroupButtonY, buttonWidth, buttonHeight)) {
            if (group.isDefault()) return false;
            // Delete clicked
            ClientNetworker.sendDeleteGroupPacket(this.group.getId());
            return true;
        }

        return super.mouseClicked(x, y, mouseX, mouseY, button);
    }

    private static List<ListItem> createItems(MicrochipsMenuScreen screen, MicrochipGroup microchipGroup) {
        if (microchipGroup == null) return new ArrayList<>();

        return microchipGroup.getMicrochips().stream().map(microchip -> new MicrochipListItem(screen, microchipGroup, microchip)).collect(Collectors.toList());
    }
}
