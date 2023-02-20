package com.jumpcutfindo.microchip.screen.window.infotab;

import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public class ActionsInfoTab extends InfoTab {
    public ActionsInfoTab(MicrochipsMenuScreen screen, Microchip microchip, GroupColor color, LivingEntity entity, int x, int y) {
        super(screen, microchip, color, entity, x, y);
    }

    @Override
    public void renderContent(MatrixStack matrices, int mouseX, int mouseY) {

    }

    @Override
    public void renderTooltips(MatrixStack matrices, int mouseX, int mouseY) {

    }

    @Override
    public void tick() {

    }
}
