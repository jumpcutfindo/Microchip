package com.jumpcutfindo.microchip.screen.window;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.client.network.ClientNetworkSender;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.jumpcutfindo.microchip.data.Microchips;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.component.MicrochipButton;
import com.jumpcutfindo.microchip.screen.list.MicrochipGroupListView;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MicrochipMoveChipsWindow extends Window {
    protected static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_move_chips.png");
    public static final int WIDTH = 160, HEIGHT = 200;
    private final MicrochipGroupListView listView;
    private final MicrochipGroup fromGroup;
    private final List<UUID> selectedMicrochips;

    private final MicrochipButton submitButton;

    public MicrochipMoveChipsWindow(MicrochipsMenuScreen screen, int x, int y, Microchips microchips, MicrochipGroup fromGroup, List<UUID> selectedMicrochips) {
        super(screen, Text.translatable("microchip.menu.moveChips.windowTitle"), WIDTH, HEIGHT, x, y);
        this.fromGroup = fromGroup;
        this.selectedMicrochips = selectedMicrochips;

        // Create list and set parameters
        this.listView = new MicrochipGroupListView(screen, microchips, this.x, this.y);
        this.listView.setTexture(TEXTURE);
        this.listView.setTextureDims(160, 200);
        this.listView.setTitle(Text.translatable("microchip.menu.moveChips.windowTitle", this.selectedMicrochips.size()));
        this.listView.setCanCreate(false);

        // Create button
        this.submitButton = new MicrochipButton(x, y, 64, 20, Text.translatable("microchip.menu.moveChips.submitButton"), (widget) -> this.moveMicrochips());

        this.listView.x = x;
        this.listView.y = y;

        this.submitButton.setX(x + 88);
        this.submitButton.setY(y + 174);
    }

    @Override
    public void renderBackground(DrawContext context) {
        this.listView.renderBackground(context, 0, 0);
    }

    @Override
    public void renderContent(DrawContext context, int mouseX, int mouseY) {
        this.listView.renderItems(context, mouseX, mouseY);

        this.submitButton.active = this.listView.getSelectedItems().size() > 0;
        this.submitButton.render(context, mouseX, mouseY, 0);
    }

    @Override
    public void tick() {

    }

    private void moveMicrochips() {
        MicrochipGroup toGroup = getToGroup();
        if (toGroup == null) return;

        ClientNetworkSender.MicrochipsActions.moveEntitiesBetweenGroups(fromGroup.getId(), toGroup.getId(), this.selectedMicrochips);
        this.screen.setActiveWindow(null);
    }

    private MicrochipGroup getToGroup() {
        return this.listView.getSelectedItem() != null ? this.listView.getSelectedItem().getGroup() : null;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return this.listView.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
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
