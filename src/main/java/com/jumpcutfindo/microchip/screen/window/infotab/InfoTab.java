package com.jumpcutfindo.microchip.screen.window.infotab;

import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public abstract class InfoTab {
    final MicrochipsMenuScreen screen;
    final Microchip microchip;
    final GroupColor color;
    final int x, y;
    final LivingEntity entity;
    public InfoTab(MicrochipsMenuScreen screen, Microchip microchip, GroupColor color, LivingEntity entity, int x, int y) {
        this.screen = screen;
        this.microchip = microchip;
        this.color = color;
        this.x = x;
        this.y = y;
        this.entity = entity;
    }

    boolean hasEntity() {
        return entity != null;
    }

    public abstract void renderContent(MatrixStack matrices, int mouseX, int mouseY);

    public abstract void renderTooltips(MatrixStack matrices, int mouseX, int mouseY);

    public abstract void tick();
}
