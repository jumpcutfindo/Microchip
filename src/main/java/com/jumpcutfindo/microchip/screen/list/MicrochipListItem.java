package com.jumpcutfindo.microchip.screen.list;

import com.jumpcutfindo.microchip.client.ClientTagger;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.jumpcutfindo.microchip.helper.SoundUtils;
import com.jumpcutfindo.microchip.helper.StringUtils;
import com.jumpcutfindo.microchip.screen.EntityModelScaler;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.ScreenUtils;
import com.jumpcutfindo.microchip.screen.window.MicrochipInfoWindow;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.apache.logging.log4j.util.TriConsumer;

public class MicrochipListItem extends ListItem<Microchip> {
    private final MicrochipGroup group;
    private LivingEntity entity;

    private float entityModelSize;

    private boolean isReordering;
    private TriConsumer<MicrochipGroup, Integer, Integer> moveAction;

    public MicrochipListItem(MicrochipsMenuScreen screen, MicrochipGroup group, Microchip microchip, int index) {
        super(screen, microchip, index);

        this.setBackground(MicrochipsListView.TEXTURE, 0, 178, 180, 36);

        this.group = group;

        retrieveEntity();
    }

    public Microchip getMicrochip() {
        return item;
    }

    @Override
    public void renderContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        // Draw entity information
        int displayNameX = x + 38;
        int displayNameY = y + 8;

        if (this.item.getEntityData() == null) return;

        if (!this.screen.isBlockedByWindow(displayNameX, displayNameY)) {
            screen.getTextRenderer().drawWithShadow(matrices, StringUtils.truncatedName(this.item.getEntityData().getDisplayName(), 20), (float) displayNameX, (float) displayNameY, 0xFFFFFF);
        }

        int entityNameX = x + 38;
        int entityNameY = y + 21;
        if (!this.screen.isBlockedByWindow(entityNameX, entityNameY)) {
            screen.getTextRenderer().draw(matrices, this.item.getEntityData().getTypeName(), (float) entityNameX, (float) entityNameY, 0x404040);
        }

        String entityHealthString = this.entity == null ? "?" : Integer.toString((int) this.entity.getHealth());

        // Draw entity health
        String healthString = String.format("%s/%d", entityHealthString, (int) this.item.getEntityData().getMaxHealth());
        int offset = healthString.length() * 6 + 1;
        screen.getTextRenderer().drawWithShadow(matrices, healthString, x + 178 - offset, y + 21, 0xFFFFFF);
        int healthIconOffset = offset + 1;

        RenderSystem.setShaderTexture(0, MicrochipsListView.TEXTURE);
        if (this.entity != null) {
            if (this.entity.getHealth() > this.entity.getMaxHealth() / 2) {
                screen.drawTexture(matrices, x + 168 - healthIconOffset, y + 20, 180, 193, 9, 9);
            } else if (this.entity.getHealth() > this.entity.getMaxHealth() / 4) {
                screen.drawTexture(matrices, x + 168 - healthIconOffset, y + 20, 189, 193, 9, 9);
            } else {
                screen.drawTexture(matrices, x + 168 - healthIconOffset, y + 20, 198, 193, 9, 9);
            }
        } else {
            screen.drawTexture(matrices, x + 168 - healthIconOffset, y + 20, 180, 193, 9, 9);
        }


        drawButtons(matrices, x, y, mouseX, mouseY);
        drawTooltips(matrices, x, y, mouseX, mouseY);
    }

    @Override
    public void renderBackground(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        // Draw entity
        if (this.entity != null) {
            int xOffset = EntityModelScaler.getInterfaceOffset(entity).getListX();
            int yOffset = EntityModelScaler.getInterfaceOffset(entity).getListY();
            this.drawEntity(x + xOffset, y + yOffset);
        }
        else {
            RenderSystem.setShaderTexture(0, MicrochipsListView.TEXTURE);
            ScreenUtils.setShaderColor(this.group.getColor(), false);
            screen.drawTexture(matrices, x + 4, y + 4, 0, 214, 28, 28);
        }

        ScreenUtils.setShaderColor(this.group.getColor(), true);
        super.renderBackground(matrices, x, y, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(int x, int y, double mouseX, double mouseY) {

        if (isReordering) {
            int arrowWidth = 5, arrowHeight = 5;
            int upX = x + 164, upY = y + 3;
            int downX = x + 172, downY = y + 3;

            if (ScreenUtils.isWithin(mouseX, mouseY, upX, upY, arrowWidth, arrowHeight)) {
                // Up
                SoundUtils.playClickSound(MinecraftClient.getInstance().getSoundManager());
                this.moveAction.accept(this.group, this.index, this.index - 1);
                return true;
            } else if (ScreenUtils.isWithin(mouseX, mouseY, downX, downY, arrowWidth, arrowHeight)) {
                // Down
                SoundUtils.playClickSound(MinecraftClient.getInstance().getSoundManager());
                this.moveAction.accept(this.group, this.index, this.index + 1);
                return true;
            }
        }

        if (ScreenUtils.isWithin(mouseX, mouseY, x, y, this.width, this.height)) {
            SoundUtils.playClickSound(MinecraftClient.getInstance().getSoundManager());
            screen.setActiveWindow(new MicrochipInfoWindow(screen, screen.getWindowX(MicrochipInfoWindow.WIDTH), screen.getWindowY(MicrochipInfoWindow.HEIGHT), this.item, this.entity, this.group.getColor()));
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseSelected(int x, int y, double mouseX, double mouseY) {
        return !this.isReordering && ScreenUtils.isWithin(mouseX, mouseY, x + 172, y + 3, 5, 5);
    }

    private void drawTooltips(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        if (screen.isWindowOpen()) return;

        if (ScreenUtils.isWithin(mouseX, mouseY, x + 4, y + 4, 28, 28)) {
            if (this.entity == null) {
                screen.renderTooltip(matrices, new TranslatableText("microchip.menu.listItem.outOfRange.tooltip"), mouseX, mouseY);
            }
        }
    }

    private void drawButtons(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, MicrochipsListView.TEXTURE);

        if (this.isReordering) {
            int arrowWidth = 5, arrowHeight = 5;
            int upX = x + 164, upY = y + 3;
            int downX = x + 172, downY = y + 3;
            ScreenUtils.setShaderColor(this.group.getColor(), true);

            if (ScreenUtils.isWithin(mouseX, mouseY, upX, upY, arrowWidth, arrowHeight)) {
                screen.drawTexture(matrices, upX, upY, 185, 183, arrowWidth, arrowHeight);
            } else {
                screen.drawTexture(matrices, upX, upY, 180, 183, arrowWidth, arrowHeight);
            }

            if (ScreenUtils.isWithin(mouseX, mouseY, downX, downY, arrowWidth, arrowHeight)) {
                screen.drawTexture(matrices, downX, downY, 185, 188, arrowWidth, arrowHeight);
            } else {
                screen.drawTexture(matrices, downX, downY, 180, 188, arrowWidth, arrowHeight);
            }

        } else {
            if (this.isSelected()) screen.drawTexture(matrices, x + 172, y + 3, 185, 178, 5, 5);
            else screen.drawTexture(matrices, x + 172, y + 3, 180, 178, 5, 5);
        }
    }

    private void drawEntity(int x, int y) {
        // Don't render if there is a window active and in front of it
        if (screen.isBlockedByWindow(x, y) || screen.isBlockedByWindow(x + 15, y + 15)) return;

        EntityPose pose = this.entity.getPose();

        this.entity.setPose(EntityPose.STANDING);

        float f = (float)Math.atan(1.0f);
        float g = (float)Math.atan(0.0f);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x + 18, y + 30, 1050.0);
        matrixStack.scale(1.0f, 1.0f, -1.0f);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.translate(0.0, 0.0, 1000.0);
        matrixStack2.scale(entityModelSize, entityModelSize, entityModelSize);
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

        this.entity.setPose(pose);
    }
    private void setEntity(LivingEntity entity) {
        this.entity = entity;
        this.entityModelSize = ScreenUtils.calculateModelSize(entity, 24.0f);
    }

    private void retrieveEntity() {
        PlayerEntity player = this.screen.getPlayer();
        LivingEntity entity = ClientTagger.getEntity(player.getWorld(), player.getPos(), this.item.getEntityId());
        if (entity != null) setEntity(entity);
    }

    public void setReordering(boolean reordering) {
        isReordering = reordering;
    }

    public void setMoveAction(TriConsumer<MicrochipGroup, Integer, Integer> moveAction) {
        this.moveAction = moveAction;
    }
}
