package com.jumpcutfindo.microchip.screen.window.info;

import com.jumpcutfindo.microchip.client.network.ClientNetworkSender;
import com.jumpcutfindo.microchip.data.GroupColor;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.helper.StatUtils;
import com.jumpcutfindo.microchip.screen.MicrochipScreen;
import com.jumpcutfindo.microchip.screen.ScreenUtils;
import com.jumpcutfindo.microchip.screen.window.MicrochipInfoWindow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;

import java.util.*;

public class StatusInfoTab extends InfoTab {

    private final Map<StatusEffect, StatusEffectWrapper> entityStatuses;
    private final int statusDisplayCount;
    private int timeSinceStatusRetrieved = 0;
    private int breedingAge;

    private int speedStatX, jumpStatX, breedStatX;
    private int statY;
    private int speedStatWidth, jumpStatWidth, breedStatWidth;

    public StatusInfoTab(MicrochipScreen screen, MicrochipInfoWindow window, Microchip microchip, GroupColor color, LivingEntity entity, int statusDisplayCount) {
        super(screen, window, microchip, color, entity);
        this.entityStatuses = new HashMap<>();

        this.statusDisplayCount = statusDisplayCount;

        ClientNetworkSender.RequestActions.requestEntityData(this.microchip.getEntityId());
    }

    @Override
    public void renderContent(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.screen.getTextRenderer(), Text.translatable("microchip.menu.microchipInfo.statusTab"), (window.getX() + 7), (window.getY() + 105), 0xFFFFFF, true);

        // Draw health and armor
        float health = this.entity == null ? 0.0f : this.entity.getHealth();
        context.drawTexture(RenderLayer::getGuiTextured, MicrochipInfoWindow.TEXTURE, window.getX() + 7, window.getY() + 130, 0, 200, 154, 5, MicrochipInfoWindow.TEXTURE_WIDTH, MicrochipInfoWindow.TEXTURE_HEIGHT);
        context.drawTexture(RenderLayer::getGuiTextured, MicrochipInfoWindow.TEXTURE, window.getX() + 7, window.getY() + 130, 0, 205, (int) ((health / microchip.getEntityData().getMaxHealth()) * 154), 5, MicrochipInfoWindow.TEXTURE_WIDTH, MicrochipInfoWindow.TEXTURE_HEIGHT);

        context.drawTexture(RenderLayer::getGuiTextured, MicrochipInfoWindow.TEXTURE, window.getX() + 7, window.getY() + 117, 168, 128, 9, 9, MicrochipInfoWindow.TEXTURE_WIDTH, MicrochipInfoWindow.TEXTURE_HEIGHT);
        String healthString = this.entity == null ? "?" : Integer.toString((int) health);
        String healthDisplayString = String.format("%s/%d", healthString, (int) microchip.getEntityData().getMaxHealth());
        context.drawText(this.screen.getTextRenderer(), healthDisplayString, window.getX() + 19, window.getY() + 118, 0xFFFFFF, true);

        context.drawTexture(RenderLayer::getGuiTextured, MicrochipInfoWindow.TEXTURE, window.getX() + 19 + healthDisplayString.length() * 7, window.getY() + 117, 177, 128, 9, 9, MicrochipInfoWindow.TEXTURE_WIDTH, MicrochipInfoWindow.TEXTURE_HEIGHT);
        String armorString = this.entity == null ? "?" : Integer.toString(this.entity.getArmor());
        context.drawText(this.screen.getTextRenderer(), armorString, window.getX() + 19 + healthDisplayString.length() * 7 + 12, window.getY() + 118, 0xFFFFFF, true);

        // Draw stats
        // Speed
        int statOffset = 0, statGap = 12;
        statY = window.getY() + 142;
        if (hasSpeed()) {
            context.drawTexture(RenderLayer::getGuiTextured, MicrochipInfoWindow.TEXTURE, window.getX() + statOffset + 7 , statY, 186, 128, 9, 9, MicrochipInfoWindow.TEXTURE_WIDTH, MicrochipInfoWindow.TEXTURE_HEIGHT);
            String speedString = String.format("%.2fm/s", StatUtils.calculateMaxSpeed((float) entity.getAttributeBaseValue(EntityAttributes.MOVEMENT_SPEED), entityStatuses.containsKey(StatusEffects.SPEED) ? entityStatuses.get(StatusEffects.SPEED).getAmplifier() : 0));

            speedStatX = window.getX() + statOffset + 19;
            context.drawText(this.screen.getTextRenderer(), speedString, speedStatX, statY + 1, 0xFFFFFF, true);
            speedStatWidth = speedString.length() * 6;
            statOffset += speedStatWidth + statGap;

            speedStatX -= 16;
            speedStatWidth += 16;
        }

        if (hasJump()) {
            context.drawTexture(RenderLayer::getGuiTextured, MicrochipInfoWindow.TEXTURE, window.getX() + statOffset + 7 , statY, 195, 128, 9, 9, MicrochipInfoWindow.TEXTURE_WIDTH, MicrochipInfoWindow.TEXTURE_HEIGHT);
            String jumpString = String.format("%.2fm", StatUtils.calculateMaxJumpHeightWithJumpStrength((float) ((HorseEntity) entity).getAttributeValue(EntityAttributes.JUMP_STRENGTH)));

            jumpStatX = window.getX() + statOffset + 19;
            context.drawText(this.screen.getTextRenderer(), jumpString, window.getX() + statOffset + 19, statY + 1, 0xFFFFFF, true);
            jumpStatWidth = jumpString.length() * 6;
            statOffset += jumpStatWidth + statGap;

            jumpStatX -= 16;
            jumpStatWidth += 16;
        }

        if (hasBreeding()) {
            context.drawTexture(RenderLayer::getGuiTextured, MicrochipInfoWindow.TEXTURE, window.getX() + statOffset + 7 , statY, 204, 128, 9, 9, MicrochipInfoWindow.TEXTURE_WIDTH, MicrochipInfoWindow.TEXTURE_HEIGHT);
            String breedString = breedingAge < 0 ? "0:00" : StringHelper.formatTicks(breedingAge, this.screen.getPlayer().getWorld().getTickManager().getTickRate());

            breedStatX = window.getX() + statOffset + 19;
            context.drawText(this.screen.getTextRenderer(), breedString, window.getX() + statOffset + 19, statY + 1, 0xFFFFFF, true);
            breedStatWidth = breedString.length() * 6;
            statOffset += breedStatWidth + statGap;

            breedStatX -= 16;
            breedStatWidth += 16;
        }

        // Draw status effects
        context.drawText(this.screen.getTextRenderer(), Text.translatable("microchip.menu.microchipInfo.statusTab.effects"), (window.getX() + 7), (window.getY() + 158), 0xFFFFFF, true);
        StatusEffectSpriteManager statusEffectSpriteManager = MinecraftClient.getInstance().getStatusEffectSpriteManager();

        int effectsOffset = 0;
        int statusEffectBgOffset = 0;
        Iterator<StatusEffectWrapper> iterator = this.entityStatuses.values().iterator();

        // Draw status effect backgrounds
        for (int i = 0; i < statusDisplayCount; i++) {
            context.drawTexture(RenderLayer::getGuiTextured, MicrochipInfoWindow.TEXTURE, window.getX() + 7 + statusEffectBgOffset, window.getY() + 170, 168, 137, 22, 22, MicrochipInfoWindow.TEXTURE_WIDTH, MicrochipInfoWindow.TEXTURE_HEIGHT);
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
                    Sprite sprite = statusEffectSpriteManager.getSprite(instance.statusEffectInstance.getEffectType());
                    context.drawSpriteStretched(RenderLayer::getGuiTextured, sprite,window.getX() + 9 + effectsOffset, window.getY() + 172, 0, 18, 18);

                    effectsOffset += 24;
                    displayedStatuses++;
                }
            }
        }

        context.drawText(this.screen.getTextRenderer(), Text.literal(String.format("+%d", Math.max(activeStatusCount - displayedStatuses, 0))), (window.getX() + 132), (window.getY() + 177), 0xFFFFFF, true);
    }

    @Override
    public void renderTooltips(DrawContext context, int mouseX, int mouseY) {
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

                String remainingTimeFormatted = instance.isInfinite() ? "Infinite" :
                        StringHelper.formatTicks(instance.getRemainingTime(timeSinceStatusRetrieved), this.screen.getPlayer().getWorld().getTickManager().getTickRate());

                if (displayedStatuses < statusDisplayCount) {
                    // Draw the status tooltip
                    if (ScreenUtils.isWithin(mouseX, mouseY, window.getX() + 9 + effectsOffset, window.getY() + 172, 18, 18)) {
                        Text timeLeftText = Text.literal(String.format(" (%s)", remainingTimeFormatted));
                        Text text = Text.translatable(statusEffect.getTranslationKey()).append(timeLeftText);
                        context.drawTooltip(this.screen.getTextRenderer(), text, mouseX, mouseY);
                    }
                    effectsOffset += 24;
                    displayedStatuses++;
                } else {
                    MutableText statusName = Text.translatable(instance.getTranslationKey());
                    statusName.append(Text.literal(String.format(" (%s)", remainingTimeFormatted)));
                    undisplayedStatuses.add(statusName);
                }
            }
        }

        if (ScreenUtils.isWithin(mouseX, mouseY, window.getX() + 129, window.getY() + 172, 18, 18)) {
            context.drawTooltip(this.screen.getTextRenderer(), undisplayedStatuses, mouseX, mouseY);
        }

        if (hasSpeed() && ScreenUtils.isWithin(mouseX, mouseY, speedStatX, statY, speedStatWidth, 9)) {
            context.drawTooltip(this.screen.getTextRenderer(), Text.translatable("microchip.menu.microchipInfo.statusTab.stats.speed"), mouseX, mouseY);
        }

        if (hasJump() && ScreenUtils.isWithin(mouseX, mouseY, jumpStatX, statY, jumpStatWidth, 9)) {
            context.drawTooltip(this.screen.getTextRenderer(), Text.translatable("microchip.menu.microchipInfo.statusTab.stats.jump"), mouseX, mouseY);
        }

        if (hasBreeding() && ScreenUtils.isWithin(mouseX, mouseY, breedStatX, statY, breedStatWidth, 9)) {
            context.drawTooltip(this.screen.getTextRenderer(), Text.translatable("microchip.menu.microchipInfo.statusTab.stats.breed"), mouseX, mouseY);
        }
    }

    @Override
    public void tick() {
        timeSinceStatusRetrieved++;
        if (breedingAge > 0) breedingAge--;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }

    public void setEntityStatuses(Collection<StatusEffectInstance> entityStatuses) {
        entityStatuses.forEach(statusEffectInstance -> {
            this.entityStatuses.put(statusEffectInstance.getEffectType().value(), new StatusEffectWrapper(statusEffectInstance));
        });

        timeSinceStatusRetrieved = 0;
    }

    private boolean hasSpeed() {
        return entity instanceof HorseEntity;
    }

    private boolean hasJump() {
        return entity instanceof HorseEntity;
    }

    private boolean hasBreeding() {
        return entity instanceof AnimalEntity;
    }

    public void setBreedingAge(int breedingAge) {
        this.breedingAge = breedingAge;
    }

    private static class StatusEffectWrapper {
        private final StatusEffectInstance statusEffectInstance;
        public StatusEffectWrapper(StatusEffectInstance statusEffectInstance) {
            this.statusEffectInstance = statusEffectInstance;
        }

        public boolean isInfinite() {
            return statusEffectInstance.isInfinite();
        }

        public boolean hasExpired(int timeSince) {
            if (statusEffectInstance.isInfinite()) {
                return false;
            }

            return timeSince > statusEffectInstance.getDuration();
        }

        public StatusEffect getEffectType() {
            return statusEffectInstance.getEffectType().value();
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
