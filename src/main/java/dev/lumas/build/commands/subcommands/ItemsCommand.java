package dev.lumas.build.commands.subcommands;

import com.google.common.base.Preconditions;
import dev.lumas.build.BuilderMode;
import dev.lumas.build.commands.CommandManager;
import dev.lumas.build.commands.SubCommand;
import dev.lumas.build.gui.BuilderItemsGui;
import dev.lumas.build.model.SuspendedPlayer;
import dev.lumas.build.model.SuspendedPlayerRegistry;
import dev.lumas.lumacore.manager.commands.CommandInfo;
import dev.lumas.lumacore.manager.modules.AutoRegister;
import dev.lumas.lumacore.manager.modules.RegisterType;
import dev.lumas.lumacore.utility.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@CommandInfo(
        name = "items",
        usage = "/<command> items",
        permission = "buildermode.items",
        parent = CommandManager.class,
        playerOnly = true
)
@AutoRegister(RegisterType.SUBCOMMAND)
public class ItemsCommand implements SubCommand {
    @Override
    public boolean execute(BuilderMode builderMode, CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) return true;

        if (!SuspendedPlayerRegistry.INSTANCE.isSuspended(player.getUniqueId())) {
            Text.msg(player, "You must be suspended to use this. Use /buildermode suspend");
            return true;
        }

        SuspendedPlayer suspendedPlayer = SuspendedPlayerRegistry.INSTANCE.getSuspendedPlayer(player.getUniqueId());
        Preconditions.checkNotNull(suspendedPlayer, "SuspendedPlayer should not be null here.");
        if (!suspendedPlayer.isSuspended()) {
            Text.msg(player, "You must be suspended to use this. Use /buildermode suspend");
            return true;
        }

        new BuilderItemsGui().open(player);
        return true;
    }

    @Override
    public List<String> tabComplete(BuilderMode builderMode, CommandSender commandSender, String[] strings) {
        return List.of();
    }
}
