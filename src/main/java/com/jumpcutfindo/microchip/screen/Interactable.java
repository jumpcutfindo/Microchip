package com.jumpcutfindo.microchip.screen;

public interface Interactable {
    boolean mouseScrolled(double mouseX, double mouseY, double amount);

    boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY);

    boolean mouseClicked(int mouseX, int mouseY, int button);

    boolean keyPressed(int keyCode, int scanCode, int modifiers);

    boolean charTyped(char chr, int modifiers);
}
