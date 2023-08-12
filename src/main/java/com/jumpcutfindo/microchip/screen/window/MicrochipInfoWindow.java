package com.jumpcutfindo.microchip.screen.window;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.*;
import com.jumpcutfindo.microchip.helper.Looker;
import com.jumpcutfindo.microchip.helper.SoundUtils;
import com.jumpcutfindo.microchip.helper.StringUtils;
import com.jumpcutfindo.microchip.helper.Tagger;
import com.jumpcutfindo.microchip.screen.EntityModelScaler;
import com.jumpcutfindo.microchip.screen.MicrochipScreen;
import com.jumpcutfindo.microchip.screen.ScreenUtils;
import com.jumpcutfindo.microchip.screen.window.info.ActionsInfoTab;
import com.jumpcutfindo.microchip.screen.window.info.InfoTab;
import com.jumpcutfindo.microchip.screen.window.info.InventoryInfoTab;
import com.jumpcutfindo.microchip.screen.window.info.StatusInfoTab;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class MicrochipInfoWindow extends Window {
    public static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_info_window.png");
    public static final int WIDTH = 168, HEIGHT = 200;

    private final Microchip microchip;
    private final GroupColor color;
    private final LivingEntity entity;

    private InfoTab activeTab;
    private final InfoTab statusTab, inventoryTab, actionsTab;
    private final int tabCount = 3;

    private final float entityModelSize;
    public MicrochipInfoWindow(MicrochipScreen screen, int x, int y, Microchip microchip, LivingEntity entity, GroupColor color) {
        super(screen, Text.translatable("microchip.menu.microchipInfo.windowTitle"), 168, 200, x, y);

        this.microchip = microchip;
        this.entity = entity;
        this.color = color;

        if (this.entity != null) {
           this.entityModelSize = ScreenUtils.calculateModelSize(entity, 48.0f);
        } else {
            this.entityModelSize = 0;
        }
        this.statusTab = new StatusInfoTab(screen, this, microchip, color, entity, 5);
        this.inventoryTab = new InventoryInfoTab(screen, this, microchip, color, entity);
        this.actionsTab = new ActionsInfoTab(screen, this, microchip, color, entity, x, y);

        this.activeTab = statusTab;
    }

    @Override
    public void setPos(int x, int y) {
        super.setPos(x, y);
    }

    @Override
    public void renderBackground(DrawContext context) {
    }

    @Override
    public void renderContent(DrawContext context, int mouseX, int mouseY) {
        this.drawIdentityCard(context, mouseX, mouseY);

        this.activeTab.renderContent(context, mouseX, mouseY);
        this.drawTabs(context, mouseX, mouseY);
        this.drawTooltips(context, mouseX, mouseY);
    }

    private void drawIdentityCard(DrawContext context, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        ScreenUtils.setShaderColor(this.color, false);
        context.drawTexture(TEXTURE, x + 8, y + 23, 168, 0, 46, 62);

        // Draw entity background, then entity, then the main UI
        if (this.entity != null) {
            int xOffset = 32 + EntityModelScaler.getInterfaceOffset(entity).getWindowX();
            int yOffset = 80 + EntityModelScaler.getInterfaceOffset(entity).getWindowY();

            drawLookingEntity(entity, x + xOffset, y + yOffset, (float) (x + 38) - mouseX, (float) (y + 80) - mouseY, entityModelSize);
        }
        else {
            context.drawTexture(TEXTURE, x + 18, y + 40, 214, 0, 28, 28);
        }

        RenderSystem.setShaderTexture(0, TEXTURE);
        ScreenUtils.setShaderColor(this.color, false);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        context.drawTexture(TEXTURE, x, y, 0, 0, this.width, this.height);

        // Draw the title and the entity information
        context.drawText(this.screen.getTextRenderer(), this.title, (x + this.titleX), (y + this.titleY), this.color.getShadowColor(), false);
        context.drawText(this.screen.getTextRenderer(), StringUtils.truncatedName(microchip.getEntityData().getDisplayName(), 15), x + 59, y + 30, 0xFFFFFF, true);
        context.drawText(this.screen.getTextRenderer(), microchip.getEntityData().getTypeName(), x + 59, y + 50, 0xFFFFFF, true);

        context.drawText(this.screen.getTextRenderer(), StringUtils.truncatedName(getCoordinates(), 18), x + 59, y + 70, 0xFFFFFF, true);
    }

    private void drawTabs(DrawContext context, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);

        int tabVerticalOffset = 0;
        for (int i = 0; i < tabCount; i++) {
            if (!screen.getPlayer().isCreative() && getTabs().get(i) instanceof ActionsInfoTab) continue;

            ScreenUtils.setShaderColor(color, false);
            if (activeTab.equals(getTabs().get(i))) {
                context.drawTexture(TEXTURE, x + 164, y + 96 + tabVerticalOffset, 168, 62 + (i == 0 ? 0 : 28), 32, 28);
            } else {
                context.drawTexture(TEXTURE, x + 164, y + 96 + tabVerticalOffset, 200, 62 + (i == 0 ? 0 : 28), 32, 28);
            }

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            context.drawTexture(TEXTURE, x + 171, y + 100 + tabVerticalOffset, i * 18, 215, 18, 18);

            tabVerticalOffset += 30;
        }
    }

    private void drawTooltips(DrawContext context, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        // Display name
        if (ScreenUtils.isWithin(mouseX, mouseY, x + 59, y + 29, 102, 12)) {
            context.drawTooltip(this.screen.getTextRenderer(), Text.literal(microchip.getEntityData().getDisplayName()), mouseX, mouseY);
        }

        // Coordinates
        if (ScreenUtils.isWithin(mouseX, mouseY, x + 59, y + 69, 102, 12)) {
            context.drawTooltip(this.screen.getTextRenderer(), Text.literal(getCoordinates()), mouseX, mouseY);
        }

        // Tabs
        if (ScreenUtils.isWithin(mouseX, mouseY, x + 164, y + 96, 32, 29)) {
            context.drawTooltip(this.screen.getTextRenderer(), Text.translatable("microchip.menu.microchipInfo.statusTab"), mouseX, mouseY);
        } else if (ScreenUtils.isWithin(mouseX, mouseY, x + 164, y + 127, 32, 29)) {
            context.drawTooltip(this.screen.getTextRenderer(),  Text.translatable("microchip.menu.microchipInfo.inventoryTab"), mouseX, mouseY);
        } else if (screen.getPlayer().isCreative() && ScreenUtils.isWithin(mouseX, mouseY, x + 164, y + 158, 32, 29)) {
            context.drawTooltip(this.screen.getTextRenderer(),  Text.translatable("microchip.menu.microchipInfo.actionTab"), mouseX, mouseY);
        }

        // Active tab
        activeTab.renderTooltips(context, mouseX, mouseY);
    }

    private String getCoordinates() {
        if (this.entity != null) return String.format("XYZ: %d / %d / %d", this.entity.getBlockPos().getX(), this.entity.getBlockPos().getY(), this.entity.getBlockPos().getZ());
        else return String.format("XYZ: %d / %d / %d", (int) microchip.getEntityData().getX(), (int) microchip.getEntityData().getY(), (int) microchip.getEntityData().getZ());
    }

    @Override
    public void tick() {
        if (this.statusTab != null) this.statusTab.tick();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        // Coordinates to chat
        if (ScreenUtils.isWithin(mouseX, mouseY, x + 59, y + 69, 102, 12)) {
            MicrochipEntityData data = microchip.getEntityData();
            screen.getPlayer().sendMessage(Text.translatable("microchip.menu.microchipInfo.statusTab.clickLocation.message", data.getDisplayName(), StringUtils.coordinatesAsFancyText(data.getX(), data.getY(), data.getZ())), false);
            SoundUtils.playClickSound(MinecraftClient.getInstance().getSoundManager());
            return true;
        }

        int tabVerticalOffset = 0;
        for (InfoTab tab : getTabs()) {
            if (!screen.getPlayer().isCreative() && tab instanceof ActionsInfoTab) continue;
            if (ScreenUtils.isWithin(mouseX, mouseY, x + 164, y + 96 + tabVerticalOffset, 32, 29)) {
                activeTab = tab;
                SoundUtils.playClickSound(MinecraftClient.getInstance().getSoundManager());
                return true;
            }
            tabVerticalOffset += 29;
        }

        activeTab.mouseClicked(mouseX, mouseY, button);

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }

    @Override
    public List<ClickableWidget> getWidgets() {
        return new ArrayList<>();
    }

    private List<InfoTab> getTabs() {
        return List.of(statusTab, inventoryTab, actionsTab);
    }

    public static void drawLookingEntity(LivingEntity entity, int x, int y, double mouseX, double mouseY, float size) {
        EntityPose pose = entity.getPose();
        entity.setPose(EntityPose.STANDING);

        float f = (float)Math.atan(mouseX / 40.0F);
        float g = (float)Math.atan(mouseY / 40.0F);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 1050.0);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.translate(0.0, 0.0, 1000.0);
        matrixStack2.scale(size, size, size);
        Quaternionf quaternionf = new Quaternionf().rotateZ((float)Math.PI);
        Quaternionf quaternionf2 = new Quaternionf().rotateX(g * 20.0f * ((float)Math.PI / 180));
        quaternionf.mul(quaternionf2);
        matrixStack2.multiply(quaternionf);
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
        quaternionf2.conjugate();
        entityRenderDispatcher.setRotation(quaternionf2);
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

        entity.setPose(pose);
    }

    public void setEntityStatuses(Collection<StatusEffectInstance> entityStatuses) {
        if (this.statusTab != null) ((StatusInfoTab) this.statusTab).setEntityStatuses(entityStatuses);
    }

    public void setBreedingAge(int breedingAge) {
        if (this.statusTab != null) ((StatusInfoTab) this.statusTab).setBreedingAge(breedingAge);
    }

    public void setInventoryList(List<ItemStack> inventoryList, int inventorySize) {
        if (this.inventoryTab != null) ((InventoryInfoTab) this.inventoryTab).setInventoryList(inventoryList, inventorySize);
    }

    public static void openStandaloneWindow(MinecraftClient client, ClientPlayerEntity player) {
        List<Entity> entities = Looker.getLookingAt(player);

        if (entities.size() == 0 || !(entities.get(0) instanceof LivingEntity entity)) return;

        UUID entityId = entity.getUuid();
        Microchips microchips = Tagger.getMicrochips(player);

        MicrochipGroup group = microchips.getGroupOfEntity(entityId);
        if (group == null) return;

        Microchip microchip = microchips.getMicrochipOf(entityId);
        if (microchip == null) return;

        MicrochipScreen screen = new MicrochipScreen(Text.of(""));
        screen.setStandalone(true);
        MicrochipInfoWindow infoWindow = new MicrochipInfoWindow(screen, screen.getWindowX(MicrochipInfoWindow.WIDTH), screen.getWindowY(MicrochipInfoWindow.HEIGHT), microchip, entity, group.getColor());

        screen.setActiveWindow(infoWindow);
        client.setScreen(screen);
    }
}
