package com.jumpcutfindo.microchip.data;

public enum GroupColor {
    RED(0xFF8B8B, 0xA55B5B, 0x703D3D, 0xFFE5E5),
    ORANGE(0xFFA970, 0xB5764F, 0x784C30, 0xFFE5D6),
    YELLOW(0xFFE175, 0x91834F, 0x685E39, 0xFFF2C6),
    GREEN(0x78C17E, 0x518255, 0x395F3C, 0xC2EABB),
    BLUE(0x7CC2FF, 0x598FB7, 0x395A75, 0xC9E6FF),
    INDIGO(0xB89EFF, 0x7D6BAD, 0x584B7A, 0xE8E0FF),
    PINK(0xFA94FF, 0xAD66BA, 0x6D4280, 0xFDDEFF),
    GRAY(0x8B8B8B, 0x565656, 0x373737, 0xFFFFFF);

    private final int primaryColor, secondaryColor, shadowColor, bezelColor;

    GroupColor(int primaryColor, int secondaryColor, int shadowColor, int bezelColor) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.shadowColor = shadowColor;
        this.bezelColor = bezelColor;
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public int getSecondaryColor() {
        return secondaryColor;
    }

    public int getShadowColor() {
        return shadowColor;
    }

    public int getBezelColor() {
        return bezelColor;
    }
}
