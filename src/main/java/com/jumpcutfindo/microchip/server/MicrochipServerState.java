package com.jumpcutfindo.microchip.server;

import com.jumpcutfindo.microchip.data.MicrochipPlayerState;
import com.jumpcutfindo.microchip.data.PlayerMicrochips;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public class MicrochipServerState extends PersistentState {
    public static final String ENTRY_PLAYER_MICROCHIPS = "playerMicrochips";
    private HashMap<UUID, MicrochipPlayerState> players = new HashMap<>();

    private MicrochipServerState() {
    }

    public void putPlayer(UUID playerId, MicrochipPlayerState playerState) {
        players.put(playerId, playerState);
    }

    public HashMap<UUID, MicrochipPlayerState> getPlayers() {
        return players;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound playersNbt = new NbtCompound();

        players.forEach((playerUuid, playerState) -> {
            NbtCompound playerMicrochipsNbt = new NbtCompound();
            playerMicrochipsNbt.put("microchips", playerState.toNbt());
            playersNbt.put(playerUuid.toString(), playerMicrochipsNbt);
        });

        nbt.put(ENTRY_PLAYER_MICROCHIPS, playersNbt);

        return nbt;
    }

    public MicrochipPlayerState getPlayerState(LivingEntity player) {
        MicrochipServerState serverState = getServerState(player.getWorld().getServer());

        MicrochipPlayerState playerState = serverState.getPlayers().computeIfAbsent(player.getUuid(), playerId -> new MicrochipPlayerState(new PlayerMicrochips(playerId)));

        return playerState;
    }

    public static MicrochipServerState createEmpty() {
        return new MicrochipServerState();
    }

    public static MicrochipServerState createFromNbt(NbtCompound nbt) {
        MicrochipServerState serverState = MicrochipServerState.createEmpty();

        NbtCompound playersNbt = nbt.getCompound(ENTRY_PLAYER_MICROCHIPS);

        playersNbt.getKeys().forEach(key -> {
            UUID playerId = UUID.fromString(key);
            NbtCompound playerStateNbt = playersNbt.getCompound(key);
            MicrochipPlayerState playerState = MicrochipPlayerState.fromNbt(playerId, playerStateNbt);
            serverState.putPlayer(playerId, playerState);
        });

        return serverState;
    }

    public static MicrochipServerState getServerState(MinecraftServer server) {
        PersistentStateManager stateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        MicrochipServerState serverState = stateManager.getOrCreate(
                MicrochipServerState::createFromNbt,
                MicrochipServerState::createEmpty,
                "microchip"
        );

        serverState.markDirty();

        return serverState;
    }
}
