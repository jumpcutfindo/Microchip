package com.jumpcutfindo.microchip.screen.window;

import java.util.ArrayList;
import java.util.List;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.client.ClientNetworker;
import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.component.ColorButton;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class MicrochipModifyGroupWindow extends Window {
    public static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_create_group.png");
    private final MicrochipGroup group;
    private TextFieldWidget groupNameField;
    private ButtonWidget submitButton;

    private List<ColorButton> colorButtons;
    private ColorButton selectedColor;

    public MicrochipModifyGroupWindow(MicrochipsMenuScreen screen, MicrochipGroup group) {
        super(screen, getTitle(group));
        this.group = group;

        this.width = 138;
        this.height = 121;

        // Create text field
        this.groupNameField = new TextFieldWidget(screen.getTextRenderer(), 0, 0, 124, 18, new TranslatableText("microchip.menu.createGroup.textWidget"));
        this.groupNameField.setMaxLength(20);
        this.groupNameField.setEditableColor(16777215);
        if (isEdit()) this.groupNameField.setText(group.getDisplayName());

        // Create buttons
        if (isEdit()) {
            this.submitButton = new ButtonWidget(0, 0, 64, 20, new TranslatableText("microchip.menu.editGroup.submitButton"), (widget) -> {
                this.updateGroup(group);
                this.screen.setActiveWindow(null);
            });
        } else {
            this.submitButton = new ButtonWidget(0, 0, 64, 20, new TranslatableText("microchip.menu.createGroup.submitButton"), (widget) -> {
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

        this.groupNameField.setTextFieldFocused(true);
    }

    private boolean isEdit() {
        return this.group != null;
    }

    public static MicrochipModifyGroupWindow createEditWindow(MicrochipsMenuScreen screen, MicrochipGroup group) {
        return new MicrochipModifyGroupWindow(screen, group);
    }

    public static MicrochipModifyGroupWindow createCreateWindow(MicrochipsMenuScreen screen) {
        return new MicrochipModifyGroupWindow(screen, null);
    }

    private static TranslatableText getTitle(MicrochipGroup group) {
        if (group == null) return new TranslatableText("microchip.menu.createGroup.windowTitle");
        return new TranslatableText("microchip.menu.editGroup.windowTitle");
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        this.screen.drawTexture(matrices, x, y, 0, 0, this.width, this.height);
    }

    @Override
    public void renderContent(MatrixStack matrices, int mouseX, int mouseY) {
        this.screen.getTextRenderer().draw(matrices, this.title, (float) (x + this.titleX), (float) (y + this.titleY), 0x404040);

        // Group title entry
        this.screen.getTextRenderer().draw(matrices, new TranslatableText("microchip.menu.createGroup.title"), (float) (x + this.titleX), (float) (y + 25), 0x404040);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        this.groupNameField.x = x + 7;
        this.groupNameField.y = y + 36;
        this.groupNameField.render(matrices, mouseX, mouseY, 0);


        // Colour entry
        this.screen.getTextRenderer().draw(matrices, new TranslatableText("microchip.menu.createGroup.colorTitle"), (float) (x + this.titleX), (float) (y + 60), 0x404040);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        for (int i = 0; i < colorButtons.size(); i++) {
            ColorButton colorButton = colorButtons.get(i);
            colorButton.x = x + 7 + i * 14;
            colorButton.y = y + 74;
            colorButton.render(matrices, mouseX, mouseY, 0);
        }

        // Button rendering
        this.submitButton.x = x + 67;
        this.submitButton.y = y + 95;
        this.submitButton.renderButton(matrices, mouseX, mouseY, 0);
        this.submitButton.active = this.isValidInput();
    }

    @Override
    public boolean handleClick(int mouseX, int mouseY, int button) {
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

        return this.groupNameField.mouseClicked(mouseX, mouseY, button)
            || this.submitButton.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean handleKeyPress(int keyCode, int scanCode, int modifiers) {
        return this.groupNameField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean handleCharTyped(char chr, int modifiers) {
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
    public boolean handleMouseScroll(double mouseX, double mouseY, double amount) {
        return false;
    }

    @Override
    public boolean handleMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    private boolean isValidInput() {
        return !this.groupNameField.getText().equals("");
    }

    private void createGroup() {
        ClientNetworker.sendCreateGroupPacket(this.groupNameField.getText(), this.selectedColor.getColor());
    }

    private void updateGroup(MicrochipGroup group) {
        ClientNetworker.sendUpdateGroupPacket(group, this.groupNameField.getText(), this.selectedColor.getColor());
    }
}
