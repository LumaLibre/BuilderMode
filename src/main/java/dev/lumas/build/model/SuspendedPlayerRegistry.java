package dev.lumas.build.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class SuspendedPlayerRegistry implements Iterable<SuspendedPlayer> {

    public static final SuspendedPlayerRegistry INSTANCE = new SuspendedPlayerRegistry();


    private final Map<UUID, SuspendedPlayer> suspendedPlayers;

    private SuspendedPlayerRegistry() {
        this.suspendedPlayers = new HashMap<>();
    }

    @Nullable
    public SuspendedPlayer getSuspendedPlayer(UUID uuid) {
        return suspendedPlayers.get(uuid);
    }

    public void registerSuspendedPlayer(SuspendedPlayer suspendedPlayer) {
        suspendedPlayers.put(suspendedPlayer.getUuid(), suspendedPlayer);
    }

    public void unregisterSuspendedPlayer(UUID uuid) {
        suspendedPlayers.remove(uuid);
    }

    public boolean isSuspended(UUID uuid) {
        return suspendedPlayers.containsKey(uuid);
    }

    public void clear() {
        suspendedPlayers.clear();
    }

    @Override
    public @NotNull Iterator<SuspendedPlayer> iterator() {
        return suspendedPlayers.values().iterator();
    }
}
