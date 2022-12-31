package com.jumpcutfindo.microchip.screen.list;

import com.jumpcutfindo.microchip.client.ClientTagger;
import com.jumpcutfindo.microchip.client.MicrochipEntityHelper;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.data.MicrochipComponents;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.jumpcutfindo.microchip.data.PlayerMicrochips;
import com.jumpcutfindo.microchip.helper.StringUtils;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.window.MicrochipInfoWindow;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class MicrochipListItem extends ListItem {
    private final MicrochipGroup group;
    private final Microchip microchip;
    private LivingEntity entity;

    private float entityModelSize;

    public MicrochipListItem(MicrochipsMenuScreen screen, MicrochipGroup group, Microchip microchip) {
        super(screen, MicrochipsListView.TEXTURE, 0, 178, 0, 178, 0, 178, 180, 36);
        this.group = group;
        this.microchip = microchip;

        retrieveEntity();
    }

    public Microchip getMicrochip() {
        return microchip;
    }

    @Override
    public void renderContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        // Draw entity information
        int displayNameX = x + 38;
        int displayNameY = y + 8;

        if (this.microchip.getEntityData() == null) return;

        if (!this.screen.isBlockedByWindow(displayNameX, displayNameY)) {
            screen.getTextRenderer().drawWithShadow(matrices, StringUtils.truncatedName(microchip.getEntityData().getDisplayName(), 20), (float) displayNameX, (float) displayNameY, 0xFFFFFF);
        }

        int entityNameX = x + 38;
        int entityNameY = y + 21;
        if (!this.screen.isBlockedByWindow(entityNameX, entityNameY)) {
            screen.getTextRenderer().draw(matrices, microchip.getEntityData().getTypeName(), (float) entityNameX, (float) entityNameY, 0x404040);
        }

        String entityHealthString = this.entity == null ? "?" : Integer.toString((int) this.entity.getHealth());

        // Draw entity health
        RenderSystem.setShaderTexture(0, MicrochipsListView.TEXTURE);
        if (this.entity != null) {
            if (this.entity.getHealth() > this.entity.getMaxHealth() / 2) {
                screen.drawTexture(matrices, x + 168, y + 20, 180, 183, 9, 9);
            } else if (this.entity.getHealth() > this.entity.getMaxHealth() / 4) {
                screen.drawTexture(matrices, x + 168, y + 20, 189, 183, 9, 9);
            } else {
                screen.drawTexture(matrices, x + 168, y + 20, 198, 183, 9, 9);
            }
        } else {
            screen.drawTexture(matrices, x + 168, y + 20, 180, 183, 9, 9);
        }

        String healthString = String.format("%s/%d", entityHealthString, (int) microchip.getEntityData().getMaxHealth());
        int offset = healthString.length() * 5 + healthString.length() - 1;
        screen.getTextRenderer().drawWithShadow(matrices, healthString, x + 168 - offset - 3, y + 21, 0xFFFFFF);

        drawButton(matrices, x + 172, y + 3, mouseX, mouseY);
        drawTooltips(matrices, x, y, mouseX, mouseY);
    }

    @Override
    public void renderBackground(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        // Draw entity
        if (this.entity != null) this.drawEntity(x, y);
        else {
            RenderSystem.setShaderTexture(0, MicrochipsListView.TEXTURE);
            screen.drawTexture(matrices, x + 4, y + 4, 0, 214, 28, 28);
        }

        MicrochipsMenuScreen.setShaderColor(this.group.getColor(), false);
        super.renderBackground(matrices, x, y, mouseX, mouseY);
    }

    @Override
    public boolean onClick(int x, int y, double mouseX, double mouseY) {
        if (MicrochipsMenuScreen.isWithin(mouseX, mouseY, x, y, this.width, this.height)) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            screen.setActiveWindow(new MicrochipInfoWindow(screen, this.microchip, this.entity, this.group.getColor()));
            return true;
        }

        return false;
    }

    @Override
    public boolean onSelect(int x, int y, double mouseX, double mouseY) {
        return MicrochipsMenuScreen.isWithin(mouseX, mouseY, x + 172, y + 3, 5, 5);
    }

    private void drawTooltips(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        if (screen.isWindowOpen()) return;

        if (MicrochipsMenuScreen.isWithin(mouseX, mouseY, x, y, 180, 36)) {
            if (this.entity == null) {
                screen.renderTooltip(matrices, new TranslatableText("microchip.menu.listItem.outOfRange.tooltip"), mouseX, mouseY);
            }
        }
    }

    private void drawButton(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, MicrochipsListView.TEXTURE);

        if (this.isSelected()) screen.drawTexture(matrices, x, y, 185, 178, 5, 5);
        else screen.drawTexture(matrices, x, y, 180, 178, 5, 5);
    }

    private void drawEntity(int x, int y) {
        // Don't render if there is a window active and in front of it
        if (screen.isBlockedByWindow(x, y) || screen.isBlockedByWindow(x + 15, y + 15)) return;

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
    }
    private void setEntity(LivingEntity entity) {
        this.entity = entity;

        determineModelSize();
    }

    private void determineModelSize() {
        if (this.entity != null) {
            this.entityModelSize = 1 / Math.max(this.entity.getHeight(), this.entity.getWidth()) * 24.0f;
        } else {
            this.entityModelSize = 0;
        }
    }

    private void retrieveEntity() {
        MicrochipEntityHelper helper = screen.getMicrochipEntityHelper();
        PlayerEntity player = this.screen.getPlayer();

        helper.populateWithEntity(player, microchip, this::setEntity);
    }
}
