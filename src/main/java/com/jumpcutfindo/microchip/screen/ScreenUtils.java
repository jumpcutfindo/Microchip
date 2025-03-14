package com.jumpcutfindo.microchip.screen;

import com.jumpcutfindo.microchip.data.GroupColor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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

        RenderSystem.setShaderColor(r, g, b, 1.0f);
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

        float entityMultiplier = EntityModelScaler.getScaleModifier(entity);

        float height = dimensions.height();
        float width = dimensions.width();

        return 1 / Math.max(height, width) * baseMultiplier * entityMultiplier;
    }

    public static void drawStaticEntity(DrawContext context, int x1, int y1, int x2, int y2, int size, float f, float mouseX, float mouseY, LivingEntity entity) {
        float g = (x1 + x2) / 2.0F;
        float h = (y1 + y2) / 2.0F;
        context.enableScissor(x1, y1, x2, y2);
        float i = (float) Math.PI / 3;
        float j = 0.0F;
        Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf quaternionf2 = new Quaternionf().rotateX(j * 20.0F * (float) (Math.PI / 180.0));
        quaternionf.mul(quaternionf2);
        float k = entity.bodyYaw;
        float l = entity.getYaw();
        float m = entity.getPitch();
        float n = entity.prevHeadYaw;
        float o = entity.headYaw;
        entity.bodyYaw = 180.0F + i * 20.0F;
        entity.setYaw(180.0F + i * 40.0F);
        entity.setPitch(-j * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        float p = entity.getScale();
        Vector3f vector3f = new Vector3f(0.0F, entity.getHeight() / 2.0F + f * p, 0.0F);
        float q = size / p;

        // Use the in-built draw entity method
        InventoryScreen.drawEntity(context, g, h, q, vector3f, quaternionf, quaternionf2, entity);

        entity.bodyYaw = k;
        entity.setYaw(l);
        entity.setPitch(m);
        entity.prevHeadYaw = n;
        entity.headYaw = o;
        context.disableScissor();
    }

    public static void drawLookingEntity(DrawContext context, int x1, int y1, int x2, int y2, int size, float f, float mouseX, float mouseY, LivingEntity entity) {
        float g = (x1 + x2) / 2.0F;
        float h = (y1 + y2) / 2.0F;
        context.enableScissor(x1, y1, x2, y2);
        float i = (float)Math.atan((g - mouseX) / 40.0F);
        float j = (float)Math.atan((h - mouseY) / 40.0F);
        Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf quaternionf2 = new Quaternionf().rotateX(j * 20.0F * (float) (Math.PI / 180.0));
        quaternionf.mul(quaternionf2);
        float k = entity.bodyYaw;
        float l = entity.getYaw();
        float m = entity.getPitch();
        float n = entity.prevHeadYaw;
        float o = entity.headYaw;
        entity.bodyYaw = 180.0F + i * 20.0F;
        entity.setYaw(180.0F + i * 40.0F);
        entity.setPitch(-j * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        float p = entity.getScale();
        Vector3f vector3f = new Vector3f(0.0F, entity.getHeight() / 2.0F + f * p, 0.0F);
        float q = size / p;

        // Use the in-built draw entity method
        InventoryScreen.drawEntity(context, g, h, q, vector3f, quaternionf, quaternionf2, entity);

        entity.bodyYaw = k;
        entity.setYaw(l);
        entity.setPitch(m);
        entity.prevHeadYaw = n;
        entity.headYaw = o;
        context.disableScissor();
    }

}
