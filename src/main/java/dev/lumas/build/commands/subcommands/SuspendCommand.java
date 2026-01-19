package dev.lumas.build.commands.subcommands;

import dev.lumas.build.BuilderMode;
import dev.lumas.build.commands.CommandManager;
import dev.lumas.build.commands.SubCommand;
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
        name = "suspend",
        usage = "/<command> suspend",
        permission = "buildermode.suspend",
        parent = CommandManager.class,
        playerOnly = true
)
@AutoRegister(RegisterType.SUBCOMMAND)
public class SuspendCommand implements SubCommand {
    @Override
    public boolean execute(BuilderMode builderMode, CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) return true;

        if (SuspendedPlayerRegistry.INSTANCE.isSuspended(player.getUniqueId())) {
            Text.msg(player, "Already suspended. Use /buildermode resume");
            return true;
        }

        SuspendedPlayer suspendedPlayer = new SuspendedPlayer(player.getUniqueId());
        suspendedPlayer.suspend(player);
        SuspendedPlayerRegistry.INSTANCE.registerSuspendedPlayer(suspendedPlayer);
        Text.msg(player, "Suspended");
        return true;
    }

    @Override
    public List<String> tabComplete(BuilderMode builderMode, CommandSender commandSender, String[] strings) {
        return List.of();
    }
}
