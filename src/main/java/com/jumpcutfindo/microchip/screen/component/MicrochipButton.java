package com.jumpcutfindo.microchip.screen.component;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class MicrochipButton extends ButtonWidget {
    public MicrochipButton(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, MicrochipButton.DEFAULT_NARRATION_SUPPLIER);
    }
}
