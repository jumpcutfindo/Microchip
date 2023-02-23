package com.jumpcutfindo.microchip.screen;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.client.ClientTagger;
import com.jumpcutfindo.microchip.client.MicrochipEntityHelper;
import com.jumpcutfindo.microchip.data.Microchip;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.jumpcutfindo.microchip.data.Microchips;
import com.jumpcutfindo.microchip.helper.Tagger;
import com.jumpcutfindo.microchip.screen.list.MicrochipGroupListView;
import com.jumpcutfindo.microchip.screen.list.MicrochipsListView;
import com.jumpcutfindo.microchip.screen.window.MicrochipInfoWindow;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.UUID;

public class MicrochipsMenuScreen extends MicrochipScreen {
    public static final Identifier BUTTONS_TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_screen_buttons.png");

    private final int titleX, titleY;
    private int x, y;
    private final MicrochipEntityHelper microchipEntityHelper;
    private Microchips microchips;

    private List<MicrochipGroup> microchipGroups;
    private int selectedGroup;

    private MicrochipGroupListView microchipGroupList;
    private MicrochipsListView microchipsList;

    public MicrochipsMenuScreen(PlayerEntity player) {
        super(new TranslatableText("microchip.menu.title"));
        this.microchipEntityHelper = new MicrochipEntityHelper();
        this.microchips = Tagger.getMicrochips(player);

        this.selectedGroup = 0;

        this.titleX = 7;
        this.titleY = 9;
    }

    @Override
    protected void init() {
        super.init();

        this.x = (this.width - (160 + 216)) / 2;
        this.y = (this.height - 178) / 2;

        this.refreshScreen(RefreshType.BOTH);
    }

    public MicrochipEntityHelper getMicrochipEntityHelper() {
        return microchipEntityHelper;
    }

    public Microchips getMicrochips() {
        return microchips;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (hasUpdates()) refreshScreen(RefreshType.BOTH);

        this.drawBackgroundGradient(matrices);
        this.microchipGroupList.renderBackground(matrices, mouseX, mouseY);
        this.microchipsList.renderBackground(matrices, mouseX, mouseY);

        this.microchipGroupList.renderItems(matrices, mouseX, mouseY);
        this.microchipsList.renderItems(matrices, mouseX, mouseY);

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseInGroupList(mouseX, mouseY)) {
            return this.microchipGroupList.mouseClicked((int) mouseX, (int) mouseY, button);
        }

        if (isMouseInMicrochipList(mouseX, mouseY)) {
            return this.microchipsList.mouseClicked((int) mouseX, (int) mouseY, button);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isMouseInGroupList(mouseX, mouseY)) {
            return this.microchipGroupList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        if (isMouseInMicrochipList(mouseX, mouseY)) {
            return this.microchipsList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (isMouseInGroupList(mouseX, mouseY)) {
            return this.microchipGroupList.mouseScrolled(mouseX, mouseY, amount);
        }

        if (isMouseInMicrochipList(mouseX, mouseY)) {
            return this.microchipsList.mouseScrolled(mouseX, mouseY, amount);
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public void setSelectedGroup(int index) {
        this.selectedGroup = index;
        // Only refresh microchips since the selection changed
        this.refreshScreen(RefreshType.MICROCHIPS);
    }

    private boolean hasUpdates() {
        Microchips clientMicrochips = Tagger.getMicrochips(client.player);
        return !this.microchipGroups.equals(clientMicrochips.getAllGroups());
    }

    public void refreshScreen(RefreshType refreshType) {
        this.microchips = Tagger.getMicrochips(client.player);
        this.microchipGroups = this.microchips.getAllGroups();

        switch (refreshType) {
        case GROUP -> refreshGroups();
        case MICROCHIPS -> refreshMicrochips();
        case BOTH -> {
            refreshGroups();
            refreshMicrochips();
        }
        }
    }

    private void refreshGroups() {
        int selectedIndex = 0;
        if (this.microchipGroupList != null) {
            selectedIndex = this.microchipGroupList.getSelectedIndex();
        }
        this.microchipGroupList = new MicrochipGroupListView(this, this.microchips, x, y);
        this.microchipGroupList.setSelected(selectedIndex);
    }

    private void refreshMicrochips() {
        int index = Math.min(this.microchips.getGroupCount() - 1, this.selectedGroup);
        index = Math.max(0, index);
        this.selectedGroup = index;

        if (this.microchips.getAllGroups().size() == 0) {
            this.microchipsList = new MicrochipsListView(this, null, x + this.microchipGroupList.getTextureWidth(), y);
        } else {
            this.microchipsList = new MicrochipsListView(this, this.microchips.getAllGroups().get(index), x + this.microchipGroupList.getTextureWidth(), y);
        }
    }

    public boolean setDisplayForEntity(PlayerEntity player, LivingEntity entity) {
        UUID entityId = entity.getUuid();
        Microchips microchips = Tagger.getMicrochips(player);
        MicrochipGroup group = microchips.getGroupOf(entityId);
        if (group == null) return false;
        Microchip microchip = group.getMicrochips().stream().filter(m -> m.getEntityId().equals(entityId)).toList().get(0);

        setActiveWindow(new MicrochipInfoWindow(this, this.getWindowX(MicrochipInfoWindow.WIDTH), this.getWindowY(MicrochipInfoWindow.HEIGHT), microchip, ClientTagger.getEntity(player.getWorld(), player.getPos(), entityId), group.getColor()));
        return true;
    }

    protected int getGroupListX() {
        return this.x;
    }

    protected int getMicrochipListX() {
        return this.x + this.microchipGroupList.getTextureWidth();
    }

    protected int getListY() {
        return this.y;
    }

    protected boolean isMouseInGroupList(double mouseX, double mouseY) {
        return ScreenUtils.isWithin(mouseX, mouseY, getGroupListX(), getListY(), this.microchipGroupList.getTextureWidth(), this.microchipGroupList.getTextureHeight());
    }

    protected boolean isMouseInMicrochipList(double mouseX, double mouseY) {
        return ScreenUtils.isWithin(mouseX, mouseY, getMicrochipListX(), getListY(), this.microchipsList.getTextureWidth(), this.microchipsList.getTextureHeight());
    }

    public enum RefreshType {
        GROUP, MICROCHIPS, BOTH;
    }
}
