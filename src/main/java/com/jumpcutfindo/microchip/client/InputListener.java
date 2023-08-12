package com.jumpcutfindo.microchip.client;

import com.jumpcutfindo.microchip.client.network.ClientNetworkSender;
import com.jumpcutfindo.microchip.helper.TagResult;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.window.MicrochipInfoWindow;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;


@Environment(EnvType.CLIENT)
public class InputListener implements ClientModInitializer {
    private static final KeyBinding TAG_BINDING = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                    "key.microchip.tag",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_R,
                    "category.microchip.microchip"
                )
    );
    public static final KeyBinding GUI_BINDING = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "key.microchip.gui",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_M,
                    "category.microchip.microchip"
            )
    );

    private static final KeyBinding MOB_INFO_BINDING = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "key.microchip.mobInfo",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_Z,
                    "category.microchip.microchip"
            )
    );

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                while (TAG_BINDING.wasPressed()) tagEntity(client.player);
                while (GUI_BINDING.wasPressed()) openMicrochipsMenu(client, client.player);
                while (MOB_INFO_BINDING.wasPressed()) openMicrochipInfoWindow(client, client.player);
            }
        });
    }

    public static void tagEntity(ClientPlayerEntity player) {
        TagResult result = ClientTagger.tag(player);

        switch (result) {
            case ADDED -> player.sendMessage(Text.translatable("microchip.action.tag.added"), false);
            case DUPLICATE -> player.sendMessage(Text.translatable("microchip.action.tag.duplicate"), false);
            case NOTHING -> player.sendMessage(Text.translatable("microchip.action.tag.nothing"), false);
        }
    }

    public static void openMicrochipsMenu(MinecraftClient client, ClientPlayerEntity player) {
        ClientNetworkSender.MicrochipsActions.updateMicrochips();
        client.setScreen(new MicrochipsMenuScreen(player));
    }

    public static void openMicrochipInfoWindow(MinecraftClient client, ClientPlayerEntity player) {
        MicrochipInfoWindow.openStandaloneWindow(client, player);
    }
}
