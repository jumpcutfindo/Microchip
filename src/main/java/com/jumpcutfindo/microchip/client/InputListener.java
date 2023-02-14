package com.jumpcutfindo.microchip.client;

import com.jumpcutfindo.microchip.client.network.ClientNetworkSender;
import org.lwjgl.glfw.GLFW;

import com.jumpcutfindo.microchip.helper.TagResult;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;


@Environment(EnvType.CLIENT)
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
                TagResult result = ClientTagger.tag(client.player);

                switch (result) {
                case ADDED -> client.player.sendMessage(new TranslatableText("microchip.action.tag.added"), false);
                case DUPLICATE -> client.player.sendMessage(new TranslatableText("microchip.action.tag.duplicate"), false);
                case NOTHING -> client.player.sendMessage(new TranslatableText("microchip.action.tag.nothing"), false);
                }
            }
            while (guiBinding.wasPressed() && client.player != null) {
                ClientNetworkSender.MicrochipsActions.updateMicrochips();
                client.setScreen(new MicrochipsMenuScreen(client.player));
            }
        });
    }
}
