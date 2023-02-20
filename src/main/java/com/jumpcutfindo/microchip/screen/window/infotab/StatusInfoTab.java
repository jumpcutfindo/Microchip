package com.jumpcutfindo.microchip.screen.window.infotab;

import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.helper.StatUtils;
import com.jumpcutfindo.microchip.screen.MicrochipsMenuScreen;
import com.jumpcutfindo.microchip.screen.ScreenUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.StringHelper;

import java.util.*;

import static com.jumpcutfindo.microchip.screen.window.MicrochipInfoWindow.TEXTURE;

public class StatusInfoTab extends InfoTab {

    private Map<StatusEffect, StatusEffectWrapper> entityStatuses;
    private final int statusDisplayCount;
    private int timeSinceStatusRetrieved = 0;

    public StatusInfoTab(MicrochipsMenuScreen screen, Microchip microchip, GroupColor color, LivingEntity entity, int x, int y, int statusDisplayCount) {
        super(screen, microchip, color, entity, x, y);
        this.entityStatuses = new HashMap<>();

        this.statusDisplayCount = statusDisplayCount;
    }

    @Override
    public void renderContent(MatrixStack matrices, int mouseX, int mouseY) {
        screen.getTextRenderer().draw(matrices, new TranslatableText("microchip.menu.microchipInfo.statusTab"), (float) (x + 7), (float) (y + 105), this.color.getShadowColor());

        // Draw health and armor
        RenderSystem.setShaderTexture(0, TEXTURE);

        float health = this.entity == null ? 0.0f : this.entity.getHealth();
        screen.drawTexture(matrices, x + 7, y + 130, 0, 200, 154, 5);
        screen.drawTexture(matrices, x + 7, y + 130, 0, 205, (int) ((health / microchip.getEntityData().getMaxHealth()) * 154), 5);

        RenderSystem.setShaderTexture(0, TEXTURE);
        screen.drawTexture(matrices, x + 7, y + 117, 168, 128, 9, 9);
        String healthString = this.entity == null ? "?" : Integer.toString((int) health);
        String healthDisplayString = String.format("%s/%d", healthString, (int) microchip.getEntityData().getMaxHealth());
        screen.getTextRenderer().drawWithShadow(matrices, healthDisplayString, x + 19, y + 118, 0xFFFFFF);

        RenderSystem.setShaderTexture(0, TEXTURE);
        screen.drawTexture(matrices, x + 19 + healthDisplayString.length() * 7, y + 117, 177, 128, 9, 9);
        String armorString = this.entity == null ? "?" : Integer.toString(this.entity.getArmor());
        screen.getTextRenderer().drawWithShadow(matrices, armorString, x + 19 + healthDisplayString.length() * 7 + 12, y + 118, 0xFFFFFF);

        // Draw stats
        // Speed
        RenderSystem.setShaderTexture(0, TEXTURE);
        screen.drawTexture(matrices, x + 7 , y + 142, 186, 128, 9, 9);
        String speedString = this.entity == null ? "?" : String.format("%.2f m/s", StatUtils.calculateMaxSpeed((float) entity.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED), entityStatuses.containsKey(StatusEffects.SPEED) ? entityStatuses.get(StatusEffects.SPEED).getAmplifier() : 0));
        screen.getTextRenderer().drawWithShadow(matrices, speedString, x + 19, y + 143, 0xFFFFFF);

        // Draw status effects
        RenderSystem.setShaderTexture(0, TEXTURE);
        screen.getTextRenderer().drawWithShadow(matrices, new TranslatableText("microchip.menu.microchipInfo.statusTab.effects"), (float) (x + 7), (float) (y + 158), 0xFFFFFF);
        StatusEffectSpriteManager statusEffectSpriteManager = MinecraftClient.getInstance().getStatusEffectSpriteManager();

        int effectsOffset = 0;
        int statusEffectBgOffset = 0;
        Iterator<StatusEffectWrapper> iterator = this.entityStatuses.values().iterator();

        // Draw status effect backgrounds
        for (int i = 0; i < statusDisplayCount; i++) {
            RenderSystem.setShaderTexture(0, TEXTURE);
            screen.drawTexture(matrices, x + 7 + statusEffectBgOffset, y + 170, 168, 137, 22, 22);
            statusEffectBgOffset += 24;
        }

        int displayedStatuses = 0;
        int activeStatusCount = 0;
        while (iterator.hasNext()) {
            StatusEffectWrapper instance = iterator.next();
            if (!instance.hasExpired(timeSinceStatusRetrieved)) {
                activeStatusCount++;
                if (displayedStatuses < statusDisplayCount) {
                    // Draw the status
                    StatusEffect statusEffect = instance.getEffectType();
                    Sprite sprite = statusEffectSpriteManager.getSprite(statusEffect);
                    RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
                    DrawableHelper.drawSprite(matrices, x + 9 + effectsOffset, y + 172, 0, 18, 18, sprite);

                    effectsOffset += 24;
                    displayedStatuses++;
                }
            }
        }

        screen.getTextRenderer().drawWithShadow(matrices, new LiteralText(String.format("+%d", Math.max(activeStatusCount - displayedStatuses, 0))), (float) (x + 132), (float) (y + 177), 0xFFFFFF);

    }

    @Override
    public void renderTooltips(MatrixStack matrices, int mouseX, int mouseY) {
        // Draw tooltips for drawn statuses
        Iterator<StatusEffectWrapper> iterator = this.entityStatuses.values().iterator();
        int effectsOffset = 0;

        int displayedStatuses = 0;
        int activeStatusCount = 0;
        List<Text> undisplayedStatuses = new ArrayList<>();
        while (iterator.hasNext()) {
            StatusEffectWrapper instance = iterator.next();
            StatusEffect statusEffect = instance.getEffectType();

            if (!instance.hasExpired(timeSinceStatusRetrieved)) {
                activeStatusCount++;
                if (displayedStatuses < statusDisplayCount) {
                    // Draw the status tooltip
                    if (ScreenUtils.isWithin(mouseX, mouseY, x + 9 + effectsOffset, y + 172, 18, 18)) {
                        Text timeLeftText = new LiteralText(String.format(" (%s)", StringHelper.formatTicks(instance.getRemainingTime(timeSinceStatusRetrieved))));
                        Text text = new TranslatableText(statusEffect.getTranslationKey()).append(timeLeftText);
                        screen.renderTooltip(matrices, text, mouseX, mouseY);

                    }
                    effectsOffset += 24;
                    displayedStatuses++;
                } else {
                    TranslatableText statusName = new TranslatableText(instance.getTranslationKey());
                    statusName.append(new LiteralText(String.format(" (%s)", StringHelper.formatTicks(instance.getRemainingTime(timeSinceStatusRetrieved)))));
                    undisplayedStatuses.add(statusName);
                }
            }
        }
        if (ScreenUtils.isWithin(mouseX, mouseY, x + 129, y + 172, 18, 18)) {
            screen.renderTooltip(matrices, undisplayedStatuses, mouseX, mouseY);
        }
    }

    @Override
    public void tick() {
        timeSinceStatusRetrieved++;
    }

    public void setEntityStatuses(Collection<StatusEffectInstance> entityStatuses) {
        entityStatuses.forEach(statusEffectInstance -> {
            this.entityStatuses.put(statusEffectInstance.getEffectType(), new StatusEffectWrapper(statusEffectInstance));
        });

        timeSinceStatusRetrieved = 0;
    }

    private static class StatusEffectWrapper {
        private final StatusEffectInstance statusEffectInstance;
        public StatusEffectWrapper(StatusEffectInstance statusEffectInstance) {
            this.statusEffectInstance = statusEffectInstance;
        }

        public boolean hasExpired(int timeSince) {
            return timeSince > statusEffectInstance.getDuration();
        }

        public StatusEffect getEffectType() {
            return statusEffectInstance.getEffectType();
        }

        public int getRemainingTime(int timeSince) {
            return statusEffectInstance.getDuration() - timeSince;
        }

        public String getTranslationKey() {
            return statusEffectInstance.getTranslationKey();
        }

        public int getAmplifier() {
            return statusEffectInstance.getAmplifier();
        }
    }
}
