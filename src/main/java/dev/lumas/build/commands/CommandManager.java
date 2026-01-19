package dev.lumas.build.commands;

import dev.lumas.build.BuilderMode;
import dev.lumas.lumacore.manager.commands.AbstractCommandManager;
import dev.lumas.lumacore.manager.commands.CommandInfo;
import dev.lumas.lumacore.manager.modules.AutoRegister;
import dev.lumas.lumacore.manager.modules.RegisterType;
import dev.lumas.lumacore.utility.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@CommandInfo(
        name = "buildermode",
        description = "Main command for Builder Mode",
        usage = "/buildermode <subcommand>",
        aliases = {"bm", "buildmode"},
        permission = "buildermode.use",
        playerOnly = true
)
@AutoRegister(RegisterType.COMMAND)
public class CommandManager extends AbstractCommandManager<BuilderMode, SubCommand> {

    public CommandManager() {
        super(BuilderMode.getInstance());
    }

    @Override
    public boolean handle(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        Player player = (Player) sender;
        List<String> allowedWorlds = BuilderMode.getOkaeriConfig().getEnabledWorlds();

        for (String world : allowedWorlds) {
            if (player.getWorld().getName().equalsIgnoreCase(world)) {
                return super.handle(sender, label, args);
            }
        }

        Text.msg(player, "You cannot use Builder Mode commands in this world.");
        return true;
    }
}
