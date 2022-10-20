package com.jumpcutfindo.microchip;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;

public class InputListener implements ClientModInitializer {
    private static KeyBinding tagBinding = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                    "key.microchip.tag",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_R,
                    "category.microchip.microchip"
                )
    );
    private static KeyBinding guiBinding = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "key.microchip.gui",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_M,
                    "category.microchip.microchip"
            )
    );

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (tagBinding.wasPressed() && client.player != null) {
                client.player.sendMessage(new LiteralText("Key for tagging was pressed!"), false);
            }
            while (guiBinding.wasPressed() && client.player != null) {
                client.player.sendMessage(new LiteralText("Key for opening interface was pressed!"), false);
            }
        });
    }
}
