package com.jumpcutfindo.microchip.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class MicrochipsMenuScreen extends Screen {
    public MicrochipsMenuScreen(PlayerEntity playerEntity) {
        super(new TranslatableText("microchip.menuTitle"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
