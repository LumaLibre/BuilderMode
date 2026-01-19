package dev.lumas.build.commands.subcommands;

import dev.lumas.build.BuilderMode;
import dev.lumas.build.commands.CommandManager;
import dev.lumas.build.commands.SubCommand;
import dev.lumas.lumacore.manager.commands.CommandInfo;
import dev.lumas.lumacore.manager.modules.AutoRegister;
import dev.lumas.lumacore.manager.modules.RegisterType;
import dev.lumas.lumacore.utility.Text;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandInfo(
        name = "reload",
        usage = "/<command> reload",
        permission = "buildermode.reload",
        parent = CommandManager.class
)
@AutoRegister(RegisterType.SUBCOMMAND)
public class ReloadCommand implements SubCommand {
    @Override
    public boolean execute(BuilderMode builderMode, CommandSender commandSender, String s, String[] strings) {
        BuilderMode.getOkaeriConfig().load(true);
        Text.msg(commandSender, "Configuration reloaded successfully.");
        return true;
    }

    @Override
    public List<String> tabComplete(BuilderMode builderMode, CommandSender commandSender, String[] strings) {
        return List.of();
    }
}
