package com.jumpcutfindo.microchip.screen.window;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.client.network.ClientNetworkSender;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.jumpcutfindo.microchip.data.Microchips;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.list.MicrochipGroupListView;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class MicrochipMoveChipsWindow extends Window {
    protected static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_move_chips.png");
    private MicrochipGroupListView listView;
    private final MicrochipGroup fromGroup;
    private List<UUID> selectedMicrochips;

    private ButtonWidget submitButton;

    public MicrochipMoveChipsWindow(MicrochipsMenuScreen screen, Microchips microchips, MicrochipGroup fromGroup, List<UUID> selectedMicrochips) {
        super(screen, new TranslatableText("microchip.menu.moveChips.windowTitle"));
        this.fromGroup = fromGroup;
        this.selectedMicrochips = selectedMicrochips;

        this.width = 160;
        this.height = 200;

        // Create list and set parameters
        this.listView = new MicrochipGroupListView(screen, microchips, this.x, this.y);
        this.listView.setTexture(TEXTURE);
        this.listView.setTextureDims(160, 200);
        this.listView.setTitle(new TranslatableText("microchip.menu.moveChips.windowTitle", this.selectedMicrochips.size()));
        this.listView.setCanCreate(false);

        // Create button
        this.submitButton = new ButtonWidget(x, y, 64, 20, new TranslatableText("microchip.menu.moveChips.submitButton"), (widget) -> this.moveMicrochips());
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        this.listView.renderBackground(matrices, 0, 0);
    }

    @Override
    public void renderContent(MatrixStack matrices, int mouseX, int mouseY) {
        this.listView.renderItems(matrices, mouseX, mouseY);

        this.submitButton.active = this.listView.getSelectedItems().size() > 0;
        this.submitButton.renderButton(matrices, mouseX, mouseY, 0);
    }

    @Override
    public void setPos(int x, int y) {
        super.setPos(x, y);
        this.listView.x = x;
        this.listView.y = y;

        this.submitButton.x = x + 88;
        this.submitButton.y = y + 174;
    }

    @Override
    public void tick() {

    }

    private void moveMicrochips() {
        MicrochipGroup toGroup = getToGroup();
        if (toGroup == null) return;

        ClientNetworkSender.GroupActions.moveEntitiesBetweenGroups(fromGroup.getId(), toGroup.getId(), this.selectedMicrochips);
        this.screen.setActiveWindow(null);
    }

    private MicrochipGroup getToGroup() {
        return this.listView.getSelectedItem() != null ? this.listView.getSelectedItem().getGroup() : null;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return this.listView.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.listView.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button) || this.submitButton.mouseClicked(mouseX, mouseY, button) || this.listView.mouseClicked(mouseX, mouseY, button);
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
    public List<ClickableWidget> getWidgets() {
        return new ArrayList<>();
    }
}
