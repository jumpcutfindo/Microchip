package com.jumpcutfindo.microchip.screen.window.info;

import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.screen.Interactable;
import com.jumpcutfindo.microchip.screen.MicrochipScreen;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.window.MicrochipInfoWindow;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public abstract class InfoTab implements Interactable {
    final MicrochipScreen screen;
    final MicrochipInfoWindow window;
    final Microchip microchip;
    final GroupColor color;
    final LivingEntity entity;
    public InfoTab(MicrochipScreen screen, MicrochipInfoWindow window, Microchip microchip, GroupColor color, LivingEntity entity) {
        this.screen = screen;
        this.window = window;
        this.microchip = microchip;
        this.color = color;
        this.entity = entity;
    }

    boolean hasEntity() {
        return entity != null;
    }

    public abstract void renderContent(MatrixStack matrices, int mouseX, int mouseY);

    public abstract void renderTooltips(MatrixStack matrices, int mouseX, int mouseY);

    public abstract void tick();
}
