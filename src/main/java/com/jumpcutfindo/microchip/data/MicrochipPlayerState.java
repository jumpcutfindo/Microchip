package com.jumpcutfindo.microchip.data;

import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public class MicrochipPlayerState {
    private Microchips microchips;

    public MicrochipPlayerState(Microchips microchips) {
        this.microchips = microchips;
    }

    private MicrochipPlayerState() {

    }

    public Microchips getMicrochips() {
        return microchips;
    }

    public NbtCompound toNbt() {
        return Microchips.toNbt(microchips);
    }

    public static MicrochipPlayerState fromNbt(UUID playerUuid, NbtCompound nbt) {
        MicrochipPlayerState playerState = new MicrochipPlayerState();
        playerState.microchips = Microchips.fromNbt(nbt, new PlayerMicrochips(playerUuid));
        return playerState;
    }
}
