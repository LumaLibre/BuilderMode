package dev.lumas.build.events;

import com.google.common.base.Preconditions;
import dev.lumas.build.BuilderMode;
import dev.lumas.build.model.SuspendedPlayer;
import dev.lumas.build.model.SuspendedPlayerRegistry;
import dev.lumas.build.util.CommandLookup;
import dev.lumas.lumacore.manager.modules.AutoRegister;
import dev.lumas.lumacore.manager.modules.RegisterType;
import dev.lumas.lumacore.utility.Text;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

@AutoRegister(RegisterType.LISTENER)
public class PlayerListeners implements Listener {

    private static final NamespacedKey TAG = new NamespacedKey(BuilderMode.getInstance(), "suspended");

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

    // Prevent item smuggling through entities:
    private boolean isTagged(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        Byte tagged = meta.getPersistentDataContainer().get(TAG, PersistentDataType.BYTE);
        return tagged != null && tagged == (byte) 1;
    }
    private void tagItem(ItemStack item, boolean isPlayerSuspended) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        if (!isPlayerSuspended) return;
        meta.getPersistentDataContainer().set(TAG, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {

        Player player = event.getPlayer();
        boolean suspended = SuspendedPlayerRegistry.INSTANCE.isSuspended(player.getUniqueId());

        ItemStack playerItem = event.getPlayerItem();
        ItemStack standItem = event.getArmorStandItem();

        boolean playerHasItem = playerItem.getType() != Material.AIR;
        boolean standHasItem = standItem.getType() != Material.AIR;

        boolean taking = !playerHasItem && standHasItem;
        boolean placing = playerHasItem && !standHasItem;
        boolean swapping = playerHasItem && standHasItem;

        if (taking || swapping) {
            if (isTagged(standItem) != suspended) {
                event.setCancelled(true);
                return;
            }
        }

        if (placing || swapping) {
            tagItem(playerItem, suspended);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof ArmorStand)) return;
        Player killer = event.getEntity().getKiller();

        if (killer == null || !SuspendedPlayerRegistry.INSTANCE.isSuspended(killer.getUniqueId())) {
            event.getDrops().removeIf(this::isTagged);
            return;
        }

        event.setCancelled(true);
        event.getEntity().remove();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteractWithItemFrame(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame frame)) return;

        Player player = event.getPlayer();
        boolean suspended = SuspendedPlayerRegistry.INSTANCE.isSuspended(player.getUniqueId());

        ItemStack inFrame = frame.getItem();
        boolean frameHasItem = inFrame.getType() != Material.AIR;

        ItemStack inHand = player.getInventory().getItem(event.getHand());
        boolean handHasItem = inHand.getType() != Material.AIR;

        if (frameHasItem && handHasItem) {
            if (isTagged(inFrame) != suspended) {
                event.setCancelled(true);
                return;
            }
        }

        if (handHasItem) {
            tagItem(inHand, suspended);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        if (!(event.getEntity() instanceof ItemFrame frame)) return;
        ItemStack inFrame = frame.getItem();

        if (event.getRemover() instanceof Player player && SuspendedPlayerRegistry.INSTANCE.isSuspended(player.getUniqueId())) {
            event.setCancelled(true);
            frame.setItem(new ItemStack(Material.AIR));
            frame.remove();
            return;
        }

        if (isTagged(inFrame)) {
            frame.setItem(new ItemStack(Material.AIR));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFrameDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof ItemFrame frame)) return;
        ItemStack inFrame = frame.getItem();

        if (event.getDamager() instanceof Player player && SuspendedPlayerRegistry.INSTANCE.isSuspended(player.getUniqueId())) {
            event.setCancelled(true);
            frame.setItem(new ItemStack(Material.AIR));
            frame.remove();
            return;
        }

        if (isTagged(inFrame)) {
            frame.setItem(new ItemStack(Material.AIR));
        }
    }

}
