package com.jumpcutfindo.microchip.screen.window;

import java.util.ArrayList;
import java.util.List;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.helper.Tagger;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class MicrochipInfoWindow extends Window {
    protected static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_info_window.png");

    private final Microchip microchip;
    private final GroupColor color;
    private LivingEntity entity;

    private final float entityModelSize;
    public MicrochipInfoWindow(MicrochipsMenuScreen screen, Microchip microchip, GroupColor color) {
        super(screen, new TranslatableText("microchip.menu.microchipInfo.windowTitle"));

        this.width = 168;
        this.height = 96;

        this.microchip = microchip;
        this.color = color;
        this.entity = Tagger.getEntity(screen.getPlayer().getWorld(), screen.getPlayer().getPos(), microchip.getEntityId());

        if (this.entity != null) {
            this.entityModelSize = 1 / Math.max(this.entity.getHeight(), this.entity.getWidth()) * 48.0f * (float) Math.max(Math.cos(this.entity.getWidth() / this.entity.getHeight()), Math.cos(this.entity.getHeight() / this.entity.getWidth()));
        } else {
            this.entityModelSize = 0;
        }
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
    }

    @Override
    public void renderContent(MatrixStack matrices, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        MicrochipsMenuScreen.setShaderColor(this.color, false);
        this.screen.drawTexture(matrices, x + 8, y + 23, 168, 0, 46, 62);
        drawLookingEntity(entity, x + 31, y + 80, (float) (x + 38) - mouseX, (float) (y + 80) - mouseY, entityModelSize);

        RenderSystem.setShaderTexture(0, TEXTURE);
        MicrochipsMenuScreen.setShaderColor(this.color, false);
        RenderSystem.setShader(GameRenderer::getBlockShader);
        this.screen.drawTexture(matrices, x, y, 0, 0, this.width, this.height);

        this.screen.getTextRenderer().draw(matrices, this.title, (float) (x + this.titleX), (float) (y + this.titleY), this.color.getShadowColor());

        this.screen.getTextRenderer().draw(matrices, this.entity.getDisplayName(), x + 59, y + 30, this.color.getShadowColor());
        this.screen.getTextRenderer().draw(matrices, Tagger.getEntityTypeText(entity), x + 59, y + 50, this.color.getShadowColor());
        this.screen.getTextRenderer().draw(matrices, new LiteralText(String.format("XYZ: %d / %d / %d", this.entity.getBlockPos().getX(), this.entity.getBlockPos().getY(), this.entity.getBlockPos().getZ())), x + 59, y + 70, this.color.getShadowColor());
    }

    @Override
    public void tick() {

    }

    @Override
    public boolean handleMouseScroll(double mouseX, double mouseY, double amount) {
        return false;
    }

    @Override
    public boolean handleMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    @Override
    public boolean handleClick(int mouseX, int mouseY, int button) {
        return false;
    }

    @Override
    public boolean handleKeyPress(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean handleCharTyped(char chr, int modifiers) {
        return false;
    }

    @Override
    public List<ClickableWidget> getWidgets() {
        return new ArrayList<>();
    }

    private static void drawLookingEntity(LivingEntity entity, int x, int y, double mouseX, double mouseY, float size) {
        float f = (float)Math.atan((double)(mouseX / 40.0F));
        float g = (float)Math.atan((double)(mouseY / 40.0F));
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate((double)x, (double)y, 1050.0);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.translate(0.0, 0.0, 1000.0);
        matrixStack2.scale((float)size, (float)size, (float)size);
        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(g * 20.0F);
        quaternion.hamiltonProduct(quaternion2);
        matrixStack2.multiply(quaternion);
        float h = entity.bodyYaw;
        float i = entity.getYaw();
        float j = entity.getPitch();
        float k = entity.prevHeadYaw;
        float l = entity.headYaw;
        entity.bodyYaw = 180.0F + f * 20.0F;
        entity.setYaw(180.0F + f * 40.0F);
        entity.setPitch(-g * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternion2.conjugate();
        entityRenderDispatcher.setRotation(quaternion2);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> {
            entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, matrixStack2, immediate, 15728880);
        });
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
}
