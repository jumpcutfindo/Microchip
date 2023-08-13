package com.jumpcutfindo.microchip.screen.window;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.client.network.ClientNetworkSender;
import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.component.ColorButton;
import com.jumpcutfindo.microchip.screen.component.MicrochipButton;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class MicrochipModifyGroupWindow extends Window {
    public static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_create_group.png");
    public static final int WIDTH = 138, HEIGHT = 121;

    private final MicrochipGroup group;
    private final TextFieldWidget groupNameField;
    private final MicrochipButton submitButton;

    private final List<ColorButton> colorButtons;
    private ColorButton selectedColor;

    public MicrochipModifyGroupWindow(MicrochipsMenuScreen screen, int x, int y, MicrochipGroup group) {
        super(screen, getTitle(group), WIDTH, HEIGHT, x, y);
        this.group = group;

        // Create text field
        this.groupNameField = new TextFieldWidget(screen.getTextRenderer(), 0, 0, 124, 18, Text.translatable("microchip.menu.createGroup.textWidget"));
        this.groupNameField.setMaxLength(20);
        this.groupNameField.setEditableColor(16777215);
        if (isEdit()) this.groupNameField.setText(group.getDisplayName());

        // Create buttons
        if (isEdit()) {
            this.submitButton = new MicrochipButton(0, 0, 64, 20, Text.translatable("microchip.menu.editGroup.submitButton"), (widget) -> {
                this.updateGroup(group);
                this.screen.setActiveWindow(null);
            });
        } else {
            this.submitButton = new MicrochipButton(0, 0, 64, 20, Text.translatable("microchip.menu.createGroup.submitButton"), (widget) -> {
                this.createGroup();
                this.screen.setActiveWindow(null);
            });
        }

        // Create color buttons
        this.colorButtons = new ArrayList<>();
        for (int i = 0; i < GroupColor.values().length; i++) {
            ColorButton button = new ColorButton(0, 0, screen, GroupColor.values()[i]);
            colorButtons.add(button);

            if (isEdit() && GroupColor.values()[i] == group.getColor()){
                this.selectedColor = button;
                button.setSelected(true);
            } else if (!isEdit() && GroupColor.values()[i] == GroupColor.getDefault()) {
                this.selectedColor = button;
                button.setSelected(true);
            }
        }

        this.groupNameField.setFocused(true);
    }

    private boolean isEdit() {
        return this.group != null;
    }

    public static MicrochipModifyGroupWindow createEditWindow(MicrochipsMenuScreen screen, MicrochipGroup group) {
        return new MicrochipModifyGroupWindow(screen, screen.getWindowX(MicrochipModifyGroupWindow.WIDTH), screen.getWindowY(MicrochipModifyGroupWindow.HEIGHT), group);
    }

    public static MicrochipModifyGroupWindow createCreateWindow(MicrochipsMenuScreen screen) {
        return new MicrochipModifyGroupWindow(screen, screen.getWindowX(MicrochipModifyGroupWindow.WIDTH), screen.getWindowY(MicrochipModifyGroupWindow.HEIGHT), null);
    }

    private static MutableText getTitle(MicrochipGroup group) {
        if (group == null) return Text.translatable("microchip.menu.createGroup.windowTitle");
        return Text.translatable("microchip.menu.editGroup.windowTitle");
    }

    @Override
    public void renderBackground(DrawContext context) {
        context.drawTexture(TEXTURE, x, y, 0, 0, this.width, this.height);
    }

    @Override
    public void renderContent(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.screen.getTextRenderer(), this.title, (x + this.titleX), (y + this.titleY), 0x404040, false);

        // Group title entry
        context.drawText(this.screen.getTextRenderer(), Text.translatable("microchip.menu.createGroup.title"), (x + this.titleX), (y + 25), 0x404040, false);

        this.groupNameField.setX(x + 7);
        this.groupNameField.setY(y + 36);
        this.groupNameField.render(context, mouseX, mouseY, 0);


        // Colour entry
        context.drawText(this.screen.getTextRenderer(), Text.translatable("microchip.menu.createGroup.colorTitle"), (x + this.titleX), (y + 60), 0x404040, false);

        for (int i = 0; i < colorButtons.size(); i++) {
            ColorButton colorButton = colorButtons.get(i);
            colorButton.x = x + 7 + i * 14;
            colorButton.y = y + 74;
            colorButton.render(context, mouseX, mouseY, 0);
        }

        // Button rendering
        this.submitButton.setX(x + 67);
        this.submitButton.setY(y + 95);
        this.submitButton.render(context, mouseX, mouseY, 0);
        this.submitButton.active = this.isValidInput();

        // Render tooltips
        for (ColorButton colorButton : colorButtons) {
            colorButton.renderTooltip(context, mouseX, mouseY, 0);
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        for (ColorButton colorButton : colorButtons) {
            if (colorButton.mouseClicked(mouseX, mouseY, button)) {
                for (ColorButton btn : colorButtons) {
                    btn.setSelected(false);
                }
                colorButton.setSelected(true);
                this.selectedColor = colorButton;

                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button)
                || this.groupNameField.mouseClicked(mouseX, mouseY, button)
                || this.submitButton.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.groupNameField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.groupNameField.charTyped(chr, modifiers);
    }

    @Override
    public List<ClickableWidget> getWidgets() {
        return List.of(this.groupNameField, this.submitButton);
    }

    @Override
    public void tick() {
        this.groupNameField.tick();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    private boolean isValidInput() {
        return !this.groupNameField.getText().equals("");
    }

    private void createGroup() {
        ClientNetworkSender.MicrochipsActions.createGroup(this.groupNameField.getText(), this.selectedColor.getColor());
    }

    private void updateGroup(MicrochipGroup group) {
        ClientNetworkSender.MicrochipsActions.updateGroup(group, this.groupNameField.getText(), this.selectedColor.getColor());
    }
}
