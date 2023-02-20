package com.jumpcutfindo.microchip.screen.window;

import java.util.*;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.client.network.ClientNetworkSender;
import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.data.MicrochipEntityData;
import com.jumpcutfindo.microchip.helper.StringUtils;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.ScreenUtils;
import com.jumpcutfindo.microchip.screen.window.infotab.InfoTab;
import com.jumpcutfindo.microchip.screen.window.infotab.StatusInfoTab;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class MicrochipInfoWindow extends Window {
    public static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_info_window.png");
    public static final int WIDTH = 168, HEIGHT = 200;

    private Microchip microchip;
    private final GroupColor color;
    private Tab selectedTab;
    private LivingEntity entity;

    private InfoTab statusTab, actionsTab;

    private final List<String> buttonTranslatableKeys = List.of(
            "microchip.menu.microchipInfo.actionTab.locate",
            "microchip.menu.microchipInfo.actionTab.teleportTo",
            "microchip.menu.microchipInfo.actionTab.heal",
            "microchip.menu.microchipInfo.actionTab.kill"
    );

    private final List<ButtonWidget.PressAction> buttonActions = List.of(
            (locateButton) -> {
                ClientNetworkSender.EntityActions.locateEntity(microchip);
            },
            (teleportToButton) -> {
                ClientNetworkSender.EntityActions.teleportToEntity(microchip);
            },
            (healButton) -> {
                ClientNetworkSender.EntityActions.healEntity(microchip);
            },
            (killButton) -> {
                ClientNetworkSender.EntityActions.killEntity(microchip);
            }
    );

    private List<ButtonWidget> entityActionButtons;
    private int timeSinceStatusRetrieved = 0;

    private final float entityModelSize;
    public MicrochipInfoWindow(MicrochipsMenuScreen screen, int x, int y, Microchip microchip, LivingEntity entity, GroupColor color) {
        super(screen, new TranslatableText("microchip.menu.microchipInfo.windowTitle"), 168, 200, x, y);

        this.microchip = microchip;
        this.entity = entity;
        this.color = color;

        if (this.entity != null) {
            this.entityModelSize = 1 / Math.max(this.entity.getHeight(), this.entity.getWidth()) * 48.0f * (float) Math.max(Math.cos(this.entity.getWidth() / this.entity.getHeight()), Math.cos(this.entity.getHeight() / this.entity.getWidth()));
        } else {
            this.entityModelSize = 0;
        }

        this.selectedTab = Tab.STATUS;

        this.entityActionButtons = new ArrayList<>();

        // Create buttons only after position is set
        for (int i = 0; i < buttonTranslatableKeys.size(); i++) {
            int xOffset = 77;
            int yOffset = 24;
            ButtonWidget buttonWidget = new ButtonWidget(x + 7 + (i % 2) * xOffset, y + 118 + (i / 2) * yOffset , 75, 20, new TranslatableText(buttonTranslatableKeys.get(i)), buttonActions.get(i));
            entityActionButtons.add(buttonWidget);
        }

        // Create tabs only after position is set
        this.statusTab = new StatusInfoTab(screen, microchip, color, entity, x, y, 5);

        ClientNetworkSender.RequestActions.requestEntityStatuses(this.microchip.getEntityId());
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
    }

    @Override
    public void renderContent(MatrixStack matrices, int mouseX, int mouseY) {
        this.drawIdentityCard(matrices, mouseX, mouseY);

        // Draw tabs according to selection
        switch (selectedTab) {
            case STATUS -> drawStatusTab(matrices, mouseX, mouseY);
            case ACTIONS -> drawActionTab(matrices, mouseX, mouseY);
        }

        this.drawTabs(matrices, mouseX, mouseY);
        this.drawTooltips(matrices, mouseX, mouseY);
    }

    private void drawIdentityCard(MatrixStack matrices, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        ScreenUtils.setShaderColor(this.color, false);
        screen.drawTexture(matrices, x + 8, y + 23, 168, 0, 46, 62);

        // Draw entity background, then entity, then the main UI
        if (this.entity != null) drawLookingEntity(entity, x + 31, y + 80, (float) (x + 38) - mouseX, (float) (y + 80) - mouseY, entityModelSize);
        else {
            screen.drawTexture(matrices, x + 18, y + 40, 0, 215, 28, 28);
        }

        RenderSystem.setShaderTexture(0, TEXTURE);
        ScreenUtils.setShaderColor(this.color, false);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        screen.drawTexture(matrices, x, y, 0, 0, this.width, this.height);

        // Draw the title and the entity information
        screen.getTextRenderer().draw(matrices, this.title, (float) (x + this.titleX), (float) (y + this.titleY), this.color.getShadowColor());
        screen.getTextRenderer().drawWithShadow(matrices, StringUtils.truncatedName(microchip.getEntityData().getDisplayName(), 15), x + 59, y + 30, 0xFFFFFF);
        screen.getTextRenderer().drawWithShadow(matrices, microchip.getEntityData().getTypeName(), x + 59, y + 50, 0xFFFFFF);

        screen.getTextRenderer().drawWithShadow(matrices, StringUtils.truncatedName(getCoordinates(), 18), x + 59, y + 70, 0xFFFFFF);
    }

    private void drawTabs(MatrixStack matrices, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);

        int tabVerticalOffset = 0;
        int tabIconVerticalOffset = 0;
        for (int i = 0; i < Tab.values().length; i++) {
            Tab tab = Tab.values()[i];

            ScreenUtils.setShaderColor(color, false);
            if (tab == selectedTab) {
                screen.drawTexture(matrices, x + 164, y + 96 + tabVerticalOffset, 168, 62 + (i == 0 ? 0 : 27), 32, 29);
            } else {
                screen.drawTexture(matrices, x + 164, y + 96 + tabVerticalOffset, 200, 62 + (i == 0 ? 0 : 27), 32, 29);
            }

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            screen.drawTexture(matrices, x + 171, y + 100 + tabIconVerticalOffset, 214 + i * 18, 0, 18, 18);

            tabVerticalOffset += 29;
            tabIconVerticalOffset += 31;
        }
    }

    private void drawStatusTab(MatrixStack matrices, int mouseX, int mouseY) {
        statusTab.renderContent(matrices, mouseX, mouseY);
    }

    private void drawActionTab(MatrixStack matrices, int mouseX, int mouseY) {
        screen.getTextRenderer().draw(matrices, new TranslatableText("microchip.menu.microchipInfo.actionTab"), (float) (x + 7), (float) (y + 105), this.color.getShadowColor());

        for (ButtonWidget entityActionButton : entityActionButtons) entityActionButton.render(matrices, mouseX, mouseY, 0);
    }

    private void drawTooltips(MatrixStack matrices, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        // Display name
        if (ScreenUtils.isWithin(mouseX, mouseY, x + 59, y + 29, 102, 12)) {
            screen.renderTooltip(matrices, new LiteralText(microchip.getEntityData().getDisplayName()), mouseX, mouseY);
        }

        // Coordinates
        if (ScreenUtils.isWithin(mouseX, mouseY, x + 59, y + 69, 102, 12)) {
            screen.renderTooltip(matrices, new LiteralText(getCoordinates()), mouseX, mouseY);
        }

        // Tabs
        if (ScreenUtils.isWithin(mouseX, mouseY, x + 164, y + 96, 32, 29)) {
            screen.renderTooltip(matrices, new TranslatableText("microchip.menu.microchipInfo.statusTab"), mouseX, mouseY);
        } else if (ScreenUtils.isWithin(mouseX, mouseY, x + 164, y + 127, 32, 29)) {
            screen.renderTooltip(matrices, new TranslatableText("microchip.menu.microchipInfo.actionTab"), mouseX, mouseY);
        }

        switch (selectedTab) {
        case STATUS -> {
            statusTab.renderTooltips(matrices, mouseX, mouseY);
        }
        case ACTIONS -> {
            int xOffset = 77;
            int yOffset = 24;

            for (int i = 0; i < buttonTranslatableKeys.size(); i++) {
                if (ScreenUtils.isWithin(mouseX, mouseY, x + 7 + (i % 2) * xOffset, y + 118 + (i / 2) * yOffset, 75, 20)) {
                    screen.renderTooltip(matrices, new TranslatableText(buttonTranslatableKeys.get(i) + ".tooltip"), mouseX, mouseY);
                }
            }
        }
        }


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
            screen.getPlayer().sendMessage(new TranslatableText("microchip.menu.microchipInfo.statusTab.clickLocation.message", data.getDisplayName(), StringUtils.coordinatesAsFancyText(data.getX(), data.getY(), data.getZ())), false);
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }

        int tabVerticalOffset = 0;
        for (Tab tab : Tab.values()) {
            if (ScreenUtils.isWithin(mouseX, mouseY, x + 164, y + 96 + tabVerticalOffset, 32, 29)) {
                selectedTab = tab;
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
            tabVerticalOffset += 29;
        }

        switch (selectedTab) {
            case STATUS -> {

            }
            case ACTIONS -> {
                for (ButtonWidget entityActionButton : entityActionButtons) {
                    if (entityActionButton.mouseClicked(mouseX, mouseY, button)) return true;
                }
            }
        }

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

    public void setEntityStatuses(Collection<StatusEffectInstance> entityStatuses) {
        if (this.statusTab != null) ((StatusInfoTab) this.statusTab).setEntityStatuses(entityStatuses);
    }

    private enum Tab {
        STATUS, ACTIONS
    }
}
