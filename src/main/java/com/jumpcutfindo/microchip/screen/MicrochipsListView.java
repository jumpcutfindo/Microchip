package com.jumpcutfindo.microchip.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jumpcutfindo.microchip.MicrochipMod;
import com.jumpcutfindo.microchip.data.MicrochipGroup;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class MicrochipsListView extends ListView {
    protected static final Identifier TEXTURE = new Identifier(MicrochipMod.MOD_ID, "textures/gui/microchip_list.png");
    private final MicrochipGroup group;

    private final int titleX, titleY;
    private final LiteralText title;
    public MicrochipsListView(MicrochipsMenuScreen screen, MicrochipGroup microchipGroup) {
        super(screen, TEXTURE, 0, 0, 216, 178,
                8, 26, 195, 26,
                createItems(screen, microchipGroup),
                4);

        this.group = microchipGroup;

        // Set various variables
        if (microchipGroup != null)  {
            this.title = new LiteralText(microchipGroup.getDisplayName());
        } else {
            this.title = new LiteralText("");
        }
        this.titleX = 7;
        this.titleY = 9;
    }

    @Override
    public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        super.render(matrices, x, y, mouseX, mouseY);

        this.screen.getTextRenderer().draw(matrices, this.title, (float) (x + this.titleX), (float) (y + this.titleY), 0x404040);
    }

    private static List<ListItem> createItems(MicrochipsMenuScreen screen, MicrochipGroup microchipGroup) {
        if (microchipGroup == null) return new ArrayList<>();

        return microchipGroup.getMicrochips().stream().map(microchip -> new MicrochipListItem(screen, microchip)).collect(Collectors.toList());
    }
}
