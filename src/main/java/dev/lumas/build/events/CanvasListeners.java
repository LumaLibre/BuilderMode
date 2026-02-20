package dev.lumas.build.events;

import com.google.common.base.Preconditions;
import dev.lumas.build.model.SuspendedPlayer;
import dev.lumas.build.model.SuspendedPlayerRegistry;
import dev.lumas.lumacore.utility.Text;
import io.canvasmc.canvas.event.EntityPortalAsyncEvent;
import io.canvasmc.canvas.event.EntityTeleportAsyncEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

// no auto register because we depend on server software
public class CanvasListeners implements Listener {

    // TODO: extract to a separate method
    @EventHandler
    public void onPlayerAsyncTeleport(EntityTeleportAsyncEvent event) {
        if (event.getEntity() instanceof Player player) {
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
    }

    @EventHandler
    public void onPortalAsyncTeleport(EntityPortalAsyncEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!SuspendedPlayerRegistry.INSTANCE.isSuspended(player.getUniqueId())) {
                return;
            }

            SuspendedPlayer suspendedPlayer = SuspendedPlayerRegistry.INSTANCE.getSuspendedPlayer(player.getUniqueId());
            Preconditions.checkNotNull(suspendedPlayer, "SuspendedPlayer should not be null here.");
            if (suspendedPlayer.isSuspended() && event.getFrom() != event.getTo()) {
                Text.msg(player, "You cannot teleport to another world while suspended.");
                event.setCancelled(true);
            }
        }
    }
}
