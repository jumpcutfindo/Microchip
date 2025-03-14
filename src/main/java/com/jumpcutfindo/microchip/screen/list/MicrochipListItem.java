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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.apache.logging.log4j.util.TriConsumer;

public class MicrochipListItem extends ListItem<Microchip> {
    private final MicrochipGroup group;
    private LivingEntity entity;

    private float entityModelSize;

    private boolean isReordering;
    private TriConsumer<MicrochipGroup, Integer, Integer> moveAction;

    public MicrochipListItem(MicrochipsMenuScreen screen, MicrochipGroup group, Microchip microchip, int index) {
        super(screen, microchip, index);

        this.setBackground(MicrochipsListView.TEXTURE, 0, 178, 180, 36, MicrochipsListView.TEXTURE_WIDTH, MicrochipsListView.TEXTURE_HEIGHT);

        this.group = group;

        retrieveEntity();
    }

    public Microchip getMicrochip() {
        return item;
    }

    @Override
    public void renderContent(DrawContext context, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        drawTooltips(context, x, y, mouseX, mouseY);
        drawButtons(context, x, y, mouseX, mouseY);

        // Draw entity information
        int displayNameX = x + 38;
        int displayNameY = y + 8;

        if (this.item.getEntityData() == null) return;

        if (!this.screen.isBlockedByWindow(displayNameX, displayNameY)) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            context.drawText(this.screen.getTextRenderer(), StringUtils.truncatedName(this.item.getEntityData().getDisplayName(), 20), displayNameX, displayNameY, 0xFFFFFF, true);
        }

        int entityNameX = x + 38;
        int entityNameY = y + 21;
        if (!this.screen.isBlockedByWindow(entityNameX, entityNameY)) {
            ScreenUtils.setShaderColor(this.group.getColor(), false);
            context.drawText(this.screen.getTextRenderer(), this.item.getEntityData().getTypeName(), entityNameX, entityNameY, 0x404040, false);
        }

        // Draw baby status
        if (this.entity != null && this.entity.isBaby()) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            context.drawTexture(RenderLayer::getGuiTextured, MicrochipsListView.TEXTURE, x + 22, y + 22, 180, 202, 9, 9, MicrochipsListView.TEXTURE_WIDTH, MicrochipsListView.TEXTURE_HEIGHT);
        }

        // Draw entity health
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        String entityHealthString = this.entity == null ? "?" : Integer.toString((int) this.entity.getHealth());
        String healthString = String.format("%s/%d", entityHealthString, (int) this.item.getEntityData().getMaxHealth());
        int offset = healthString.length() * 6 + 1;
        context.drawText(this.screen.getTextRenderer(), healthString, x + 178 - offset, y + 21, 0xFFFFFF, !this.screen.isWindowOpen());
        int healthIconOffset = offset + 1;

        if (this.entity != null) {
            if (this.entity.getHealth() > this.entity.getMaxHealth() / 2) {
                context.drawTexture(RenderLayer::getGuiTextured, MicrochipsListView.TEXTURE, x + 168 - healthIconOffset, y + 20, 180, 193, 9, 9, MicrochipsListView.TEXTURE_WIDTH, MicrochipsListView.TEXTURE_HEIGHT);
            } else if (this.entity.getHealth() > this.entity.getMaxHealth() / 4) {
                context.drawTexture(RenderLayer::getGuiTextured, MicrochipsListView.TEXTURE, x + 168 - healthIconOffset, y + 20, 189, 193, 9, 9, MicrochipsListView.TEXTURE_WIDTH, MicrochipsListView.TEXTURE_HEIGHT);
            } else {
                context.drawTexture(RenderLayer::getGuiTextured, MicrochipsListView.TEXTURE, x + 168 - healthIconOffset, y + 20, 198, 193, 9, 9, MicrochipsListView.TEXTURE_WIDTH, MicrochipsListView.TEXTURE_HEIGHT);
            }
        } else {
            context.drawTexture(RenderLayer::getGuiTextured, MicrochipsListView.TEXTURE, x + 168 - healthIconOffset, y + 20, 180, 193, 9, 9, MicrochipsListView.TEXTURE_WIDTH, MicrochipsListView.TEXTURE_HEIGHT);
        }

        context.draw();
    }

    @Override
    public void renderBackground(DrawContext context, int x, int y, int mouseX, int mouseY) {
        // Draw entity
        if (this.entity != null) {
            int xOffset = EntityModelScaler.getInterfaceOffset(entity).getListX();
            int yOffset = EntityModelScaler.getInterfaceOffset(entity).getListY();
            this.drawEntity(context, x + xOffset, y + yOffset, entityModelSize);
        } else {
            context.drawTexture(RenderLayer::getGuiTextured, MicrochipsListView.TEXTURE, x + 4, y + 4, 0, 214, 28, 28, MicrochipsListView.TEXTURE_WIDTH, MicrochipsListView.TEXTURE_HEIGHT);
        }
        super.renderBackground(context, x, y, mouseX, mouseY);

        ScreenUtils.setShaderColor(this.group.getColor(), true);
        context.draw();
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

    private void drawTooltips(DrawContext context, int x, int y, int mouseX, int mouseY) {
        if (screen.isWindowOpen()) return;

        if (ScreenUtils.isWithin(mouseX, mouseY, x + 22, y + 22, 9, 9)) {
            if (this.entity != null && this.entity.isBaby()) {
                context.drawTooltip(this.screen.getTextRenderer(), Text.translatable("microchip.menu.listItem.baby.tooltip"), mouseX, mouseY);
            }
            return;
        }

        if (ScreenUtils.isWithin(mouseX, mouseY, x + 4, y + 4, 28, 28)) {
            if (this.entity == null) {
                context.drawTooltip(this.screen.getTextRenderer(), Text.translatable("microchip.menu.listItem.outOfRange.tooltip"), mouseX, mouseY);
            }
        }
    }

    private void drawButtons(DrawContext context, int x, int y, int mouseX, int mouseY) {
        if (this.isReordering) {
            int arrowWidth = 5, arrowHeight = 5;
            int upX = x + 164, upY = y + 3;
            int downX = x + 172, downY = y + 3;

            if (ScreenUtils.isWithin(mouseX, mouseY, upX, upY, arrowWidth, arrowHeight)) {
                context.drawTexture(RenderLayer::getGuiTextured, MicrochipsListView.TEXTURE, upX, upY, 185, 183, arrowWidth, arrowHeight, MicrochipsListView.TEXTURE_WIDTH, MicrochipsListView.TEXTURE_HEIGHT);
            } else {
                context.drawTexture(RenderLayer::getGuiTextured, MicrochipsListView.TEXTURE, upX, upY, 180, 183, arrowWidth, arrowHeight, MicrochipsListView.TEXTURE_WIDTH, MicrochipsListView.TEXTURE_HEIGHT);
            }

            if (ScreenUtils.isWithin(mouseX, mouseY, downX, downY, arrowWidth, arrowHeight)) {
                context.drawTexture(RenderLayer::getGuiTextured, MicrochipsListView.TEXTURE, downX, downY, 185, 188, arrowWidth, arrowHeight, MicrochipsListView.TEXTURE_WIDTH, MicrochipsListView.TEXTURE_HEIGHT);
            } else {
                context.drawTexture(RenderLayer::getGuiTextured, MicrochipsListView.TEXTURE, downX, downY, 180, 188, arrowWidth, arrowHeight, MicrochipsListView.TEXTURE_WIDTH, MicrochipsListView.TEXTURE_HEIGHT);
            }
        } else {
            if (this.isSelected()) context.drawTexture(RenderLayer::getGuiTextured, MicrochipsListView.TEXTURE, x + 172, y + 3, 185, 178, 5, 5, MicrochipsListView.TEXTURE_WIDTH, MicrochipsListView.TEXTURE_HEIGHT);
            else context.drawTexture(RenderLayer::getGuiTextured, MicrochipsListView.TEXTURE, x + 172, y + 3, 180, 178, 5, 5, MicrochipsListView.TEXTURE_WIDTH, MicrochipsListView.TEXTURE_HEIGHT);
        }
    }

    private void drawEntity(DrawContext context, int x, int y, float size) {
        // Don't render if there is a window active and in front of it
        if (screen.isBlockedByWindow(x, y) || screen.isBlockedByWindow(x + 15, y + 15)) return;

        ScreenUtils.drawStaticEntity(context, x + 3, y + 4, x + 32, y + 32, (int) (size), 0.0f, (float) screen.width / 2, (float) screen.height / 2 , entity);
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
