package dev.lumas.build.events;

import com.google.common.base.Preconditions;
import dev.lumas.build.BuilderMode;
import dev.lumas.build.model.SuspendedPlayer;
import dev.lumas.build.model.SuspendedPlayerRegistry;
import dev.lumas.build.util.CommandLookup;
import dev.lumas.lumacore.manager.modules.AutoRegister;
import dev.lumas.lumacore.manager.modules.RegisterType;
import dev.lumas.lumacore.utility.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

@AutoRegister(RegisterType.LISTENER)
public class PlayerListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (!SuspendedPlayerRegistry.INSTANCE.isSuspended(player.getUniqueId())) {
            return;
        }

        SuspendedPlayer suspendedPlayer = SuspendedPlayerRegistry.INSTANCE.getSuspendedPlayer(player.getUniqueId());
        Preconditions.checkNotNull(suspendedPlayer, "SuspendedPlayer should not be null here.");
        if (suspendedPlayer.isSuspended() && event.getFrom().getWorld() != event.getTo().getWorld()) {
            Text.msg(player, "You cannot teleport to another world while suspended.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!SuspendedPlayerRegistry.INSTANCE.isSuspended(player.getUniqueId())) {
            return;
        }

        SuspendedPlayer suspendedPlayer = SuspendedPlayerRegistry.INSTANCE.getSuspendedPlayer(player.getUniqueId());
        Preconditions.checkNotNull(suspendedPlayer, "SuspendedPlayer should not be null here.");
        suspendedPlayer.resume(player);
        SuspendedPlayerRegistry.INSTANCE.unregisterSuspendedPlayer(player.getUniqueId());
    }


    @EventHandler
    public void onPlayerOpenInventory(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        if (!SuspendedPlayerRegistry.INSTANCE.isSuspended(player.getUniqueId())) {
            return;
        }

        SuspendedPlayer suspendedPlayer = SuspendedPlayerRegistry.INSTANCE.getSuspendedPlayer(player.getUniqueId());
        Preconditions.checkNotNull(suspendedPlayer, "SuspendedPlayer should not be null here.");
        if (!suspendedPlayer.isSuspended()) {
            return;
        }

        InventoryType type = event.getInventory().getType();

        if (type != InventoryType.PLAYER && type != InventoryType.CREATIVE && type != InventoryType.CRAFTING && type != InventoryType.WORKBENCH) {
            Text.msg(player, "You cannot open this inventory while suspended.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!SuspendedPlayerRegistry.INSTANCE.isSuspended(player.getUniqueId())) {
            return;
        }

        SuspendedPlayer suspendedPlayer = SuspendedPlayerRegistry.INSTANCE.getSuspendedPlayer(player.getUniqueId());
        Preconditions.checkNotNull(suspendedPlayer, "SuspendedPlayer should not be null here.");
        if (!suspendedPlayer.isSuspended()) {
            return;
        }

        Text.msg(player, "You cannot drop items while suspended.");
        event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPreProcessCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!SuspendedPlayerRegistry.INSTANCE.isSuspended(player.getUniqueId())) {
            return;
        }

        SuspendedPlayer suspendedPlayer = SuspendedPlayerRegistry.INSTANCE.getSuspendedPlayer(player.getUniqueId());
        Preconditions.checkNotNull(suspendedPlayer, "SuspendedPlayer should not be null here.");
        if (!suspendedPlayer.isSuspended()) {
            return;
        }

        List<String> whitelistedCommands = BuilderMode.getOkaeriConfig().getWhiteListedCommands();
        List<String> whitelistedPlugins = BuilderMode.getOkaeriConfig().getAllowCommandsFromPlugins();

        String command = event.getMessage().split(" ")[0].substring(1).toLowerCase();

        if (whitelistedCommands.contains(command)) {
            //Text.msg(player, "You have used a whitelisted command: /" + command);
            return;
        }


        List<String> owningPlugins = CommandLookup.findByCommand(command);

        for (String pluginName : owningPlugins) {
            if (whitelistedPlugins.contains(pluginName)) {
                //Text.msg(player, "You have used a command from a whitelisted plugin: /" + command + " (Plugin: " + pluginName + ")");
                return;
            }
        }

        Text.msg(player, "You cannot use this command while suspended.");
        event.setCancelled(true);
    }
}
