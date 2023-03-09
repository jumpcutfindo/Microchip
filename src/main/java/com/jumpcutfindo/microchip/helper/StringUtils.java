package com.jumpcutfindo.microchip.helper;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public class StringUtils {
    public static String truncatedName(String name, int maxLength) {
        if (name.length() <= maxLength) return name;
        else return name.substring(0, maxLength) + "...";
    }

    public static Text coordinatesAsFancyText(double x, double y, double z) {
        return Texts.bracketed(Text.translatable("chat.coordinates", (int) x, (int) y, (int) z)).styled((style) -> {
            Style textStyled = style.withColor(Formatting.GREEN);
            ClickEvent.Action command = ClickEvent.Action.SUGGEST_COMMAND;
            return textStyled.withClickEvent(new ClickEvent(command, String.format("/tp @s %d %d %d", (int) x, (int) y, (int) z))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.coordinates.tooltip")));
        });
    }
}
