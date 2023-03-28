package com.jumpcutfindo.microchip.screen;

import com.jumpcutfindo.microchip.data.GroupColor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;

import java.awt.*;

public class ScreenUtils {
    public static boolean isWithin(double mouseX, double mouseY, int boundX, int boundY, int boundWidth, int boundHeight) {
        return mouseX >= boundX && mouseX < boundX + boundWidth
                && mouseY >= boundY && mouseY < boundY + boundHeight;
    }

    public static void setShaderColor(GroupColor groupColor, boolean shouldIgnoreGray) {
        if (shouldIgnoreGray && groupColor == GroupColor.GRAY) return;

        Color color = new Color(groupColor.getPrimaryColor());
        float r = (float) color.getRed() / 204.0f;
        float g = (float) color.getGreen() / 204.0f;
        float b = (float) color.getBlue() / 204.0f;

        RenderSystem.setShaderColor(r, g, b, 0.0f);
    }

    public static void setShaderColor(int colorValue) {
        Color color = new Color(colorValue);
        float r = (float) color.getRed() / 204.0f;
        float g = (float) color.getGreen() / 204.0f;
        float b = (float) color.getBlue() / 204.0f;

        RenderSystem.setShaderColor(r, g, b, 0.0f);
    }

    public static float calculateModelSize(LivingEntity entity, float baseMultiplier) {
        EntityDimensions dimensions = entity.getDimensions(EntityPose.STANDING);

        float entityMultiplier = EntityModelScaler.ENTITY_SCALES.getOrDefault(entity.getClass(), 1.0f);

        float height = dimensions.height;
        float width = dimensions.width;

        return 1 / Math.max(height, width) * baseMultiplier * entityMultiplier;
    }
}
