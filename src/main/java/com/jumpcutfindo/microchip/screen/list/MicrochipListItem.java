package com.jumpcutfindo.microchip.screen.list;

import com.jumpcutfindo.microchip.client.ClientNetworker;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.jumpcutfindo.microchip.helper.Tagger;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class MicrochipListItem extends ListItem {
    private final MicrochipGroup group;
    private final Microchip microchip;
    private final LivingEntity entity;
    public MicrochipListItem(MicrochipsMenuScreen screen, MicrochipGroup group, Microchip microchip) {
        super(screen, MicrochipsListView.TEXTURE, 0, 178, 0, 178, 0, 178, 180, 36);
        this.group = group;
        this.microchip = microchip;

        this.entity = this.getEntity();
    }

    @Override
    public void renderContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        if (this.entity != null) {
            int displayNameX = x + 38;
            int displayNameY = y + 8;
            if (!this.screen.isBlockedByWindow(displayNameX, displayNameY)) screen.getTextRenderer().drawWithShadow(matrices, this.entity.getDisplayName(), (float) displayNameX, (float) displayNameY, 0xFFFFFF);

            int entityNameX = x + 38;
            int entityNameY = y + 21;
            if (!this.screen.isBlockedByWindow(entityNameX, entityNameY)) screen.getTextRenderer().draw(matrices, this.entity.getType().getName(), (float) entityNameX, (float) entityNameY, 0x404040);

            this.drawEntity(x, y);
        }

        drawButton(matrices, x + 173, y + 3, mouseX, mouseY);
    }

    @Override
    public boolean onClick(int x, int y, double mouseX, double mouseY) {
        if (MicrochipsMenuScreen.isWithin(mouseX, mouseY, x + 173, y + 3, 4, 4)) {
            // Delete pressed
            ClientNetworker.sendRemoveEntityFromGroupPacket(this.group.getId(), this.microchip.getEntityId());
        }
        return false;
    }

    private void drawButton(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, MicrochipsListView.TEXTURE);

        if (MicrochipsMenuScreen.isWithin(mouseX, mouseY, x, y, 4, 4)) {
            // Hovered
            screen.drawTexture(matrices, x, y, 184, 178, 4, 4);
        } else {
            // Normal
            screen.drawTexture(matrices, x, y, 180, 178, 4, 4);
        }

    }

    private void drawEntity(int x, int y) {
        // Don't render if there is a window active and in front of it
        if (screen.isBlockedByWindow(x + 15, y + 15)) return;

        int size = (int) ((1 / (this.entity.getHeight() + this.entity.getWidth())) * 30.0f);

        float f = (float)Math.atan(1.0f);
        float g = (float)Math.atan(0.0f);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x + 18, y + 30, 1050.0);
        matrixStack.scale(1.0f, 1.0f, -1.0f);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.translate(0.0, 0.0, 1000.0);
        matrixStack2.scale(size, size, size);
        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0f);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(g * 20.0f);
        quaternion.hamiltonProduct(quaternion2);
        matrixStack2.multiply(quaternion);
        float h = entity.bodyYaw;
        float i = entity.getYaw();
        float j = entity.getPitch();
        float k = entity.prevHeadYaw;
        float l = entity.headYaw;
        entity.bodyYaw = 180.0f + f * 20.0f;
        entity.setYaw(180.0f + f * 40.0f);
        entity.setPitch(-g * 20.0f);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternion2.conjugate();
        entityRenderDispatcher.setRotation(quaternion2);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0f, 1.0f, matrixStack2, immediate, 0xF000F0));
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);
        entity.bodyYaw = h;
        entity.setYaw(i);
        entity.setPitch(j);
        entity.prevHeadYaw = k;
        entity.headYaw = l;
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }

    private LivingEntity getEntity() {
        PlayerEntity player = this.screen.getPlayer();
        return Tagger.getEntity(player.getWorld(), player.getPos(), microchip.getEntityId());
    }
}
