package com.jumpcutfindo.microchip.screen.window.info;

import com.jumpcutfindo.microchip.client.network.ClientNetworkSender;
import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.ScreenUtils;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.List;

public class ActionsInfoTab extends InfoTab {

    private final List<String> buttonTranslatableKeys = List.of(
            "microchip.menu.microchipInfo.actionTab.locate",
            "microchip.menu.microchipInfo.actionTab.teleportTo",
            "microchip.menu.microchipInfo.actionTab.heal",
            "microchip.menu.microchipInfo.actionTab.kill"
    );

    private final List<ButtonWidget.PressAction> buttonActions = List.of(
            (locateButton) -> {
                ClientNetworkSender.EntityActions.locateEntity(microchip);
            },
            (teleportToButton) -> {
                ClientNetworkSender.EntityActions.teleportToEntity(microchip);
            },
            (healButton) -> {
                ClientNetworkSender.EntityActions.healEntity(microchip);
            },
            (killButton) -> {
                ClientNetworkSender.EntityActions.killEntity(microchip);
            }
    );

    private final int buttonCount = 4;
    private final ArrayList<ButtonWidget> entityActionButtons;

    public ActionsInfoTab(MicrochipsMenuScreen screen, Microchip microchip, GroupColor color, LivingEntity entity, int x, int y) {
        super(screen, microchip, color, entity, x, y);

        this.entityActionButtons = new ArrayList<>();
        for (int i = 0; i < buttonTranslatableKeys.size(); i++) {
            int xOffset = 77;
            int yOffset = 24;
            ButtonWidget buttonWidget = new ButtonWidget(x + 7 + (i % 2) * xOffset, y + 118 + (i / 2) * yOffset , 75, 20, new TranslatableText(buttonTranslatableKeys.get(i)), buttonActions.get(i));
            entityActionButtons.add(buttonWidget);
        }
    }

    @Override
    public void renderContent(MatrixStack matrices, int mouseX, int mouseY) {
        screen.getTextRenderer().draw(matrices, new TranslatableText("microchip.menu.microchipInfo.actionTab"), (float) (x + 7), (float) (y + 105), this.color.getShadowColor());

        for (ButtonWidget entityActionButton : entityActionButtons) entityActionButton.render(matrices, mouseX, mouseY, 0);
    }

    @Override
    public void renderTooltips(MatrixStack matrices, int mouseX, int mouseY) {
        int xOffset = 77;
        int yOffset = 24;

        for (int i = 0; i < buttonTranslatableKeys.size(); i++) {
            if (ScreenUtils.isWithin(mouseX, mouseY, x + 7 + (i % 2) * xOffset, y + 118 + (i / 2) * yOffset, 75, 20)) {
                screen.renderTooltip(matrices, new TranslatableText(buttonTranslatableKeys.get(i) + ".tooltip"), mouseX, mouseY);
            }
        }
    }

    @Override
    public void tick() {

    }
}
