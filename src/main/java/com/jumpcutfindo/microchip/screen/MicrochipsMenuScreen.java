package com.jumpcutfindo.microchip.screen;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.MicrochipGroup;
import com.jumpcutfindo.microchip.data.Microchips;
import com.jumpcutfindo.microchip.helper.Tagger;
import com.jumpcutfindo.microchip.screen.list.MicrochipGroupListView;
import com.jumpcutfindo.microchip.screen.list.MicrochipsListView;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.List;

public class MicrochipsMenuScreen extends MicrochipScreen {
    public static final Identifier BUTTONS_TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_screen_buttons.png");

    private final int titleX, titleY;
    private int x, y;
    private Microchips microchips;

    private List<MicrochipGroup> microchipGroups;
    private int selectedGroup;

    private MicrochipGroupListView microchipGroupList;
    private MicrochipsListView microchipsList;

    public MicrochipsMenuScreen(PlayerEntity player) {
        super(new TranslatableText("microchip.menu.title"));
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

    public Microchips getMicrochips() {
        return microchips;
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
        if (isWindowOpen()) return super.mouseClicked(mouseX, mouseY, button);

        if (isMouseInGroupList(mouseX, mouseY)) {
            return this.microchipGroupList.mouseClicked((int) mouseX, (int) mouseY, button);
        }

        if (isMouseInMicrochipList(mouseX, mouseY)) {
            return this.microchipsList.mouseClicked((int) mouseX, (int) mouseY, button);
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isWindowOpen()) return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

        if (isMouseInGroupList(mouseX, mouseY)) {
            return this.microchipGroupList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        if (isMouseInMicrochipList(mouseX, mouseY)) {
            return this.microchipsList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (isWindowOpen()) return super.mouseScrolled(mouseX, mouseY, amount);

        if (isMouseInGroupList(mouseX, mouseY)) {
            return this.microchipGroupList.mouseScrolled(mouseX, mouseY, amount);
        }

        if (isMouseInMicrochipList(mouseX, mouseY)) {
            return this.microchipsList.mouseScrolled(mouseX, mouseY, amount);
        }
        return false;
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
        NbtCompound settings = null;
        if (this.microchipGroupList != null) {
            settings = this.microchipGroupList.getSettings();
        }

        this.microchipGroupList = new MicrochipGroupListView(this, this.microchips, x, y);
        if (settings != null) this.microchipGroupList.applySettings(settings);
    }

    private void refreshMicrochips() {
        NbtCompound settings = null;
        List<MicrochipGroup> allGroups = this.microchips.getAllGroups();

        int index = Math.min(this.microchips.getGroupCount() - 1, this.selectedGroup);
        index = Math.max(0, index);
        this.selectedGroup = index;

        if (this.microchipsList != null) {
            settings = this.microchipsList.getSettings();
        }

        if (allGroups.size() == 0) {
            this.microchipsList = new MicrochipsListView(this, null, x + this.microchipGroupList.getTextureWidth(), y);
        } else {
            this.microchipsList = new MicrochipsListView(this, allGroups.get(index), x + this.microchipGroupList.getTextureWidth(), y);
            if (settings != null) this.microchipsList.applySettings(settings);
        }
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
