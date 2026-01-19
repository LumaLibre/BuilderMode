package dev.lumas.build.model;

import lombok.Getter;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.inventory.SaveInventory;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
public class SuspendedPlayer {

    private final UUID uuid;

    private ItemStack[] suspendedInventory;
    private float suspendedExperience;
    private boolean isSuspended = false;

    public SuspendedPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }


    public void suspend(Player player) {
        SaveInventory saveInventory = new SaveInventory(player, LogType.FORCE, null, null);
        saveInventory.snapshotAndSave(player.getInventory(), player.getEnderChest(), true);

        this.suspendedInventory = player.getInventory().getContents();
        this.suspendedExperience = player.getExp();
        this.isSuspended = true;

        player.getInventory().clear();
        player.setExp(0);
        player.setGameMode(GameMode.CREATIVE);
    }


    public void resume(Player player) {
        if (!isSuspended) {
            throw new IllegalStateException("Player is not suspended.");
        }

        player.getInventory().clear();
        player.getInventory().setContents(suspendedInventory);
        player.setExp(suspendedExperience);
        player.setGameMode(GameMode.SURVIVAL);

        this.suspendedInventory = null;
        this.suspendedExperience = 0;
        this.isSuspended = false;
    }

}
