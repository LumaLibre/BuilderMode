package dev.lumas.build.commands;

import dev.lumas.build.BuilderMode;
import dev.lumas.core.annotation.Autowire;
import dev.lumas.core.annotation.CommandMeta;
import dev.lumas.core.annotation.Register;
import dev.lumas.core.model.command.AbstractCommandManager;
import dev.lumas.core.util.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@CommandMeta(
        name = "buildermode",
        description = "Main command for Builder Mode",
        usage = "/buildermode <subcommand>",
        aliases = {"bm", "buildmode"},
        permission = "buildermode.use"
)
@Register(Autowire.COMMAND)
public class CommandManager extends AbstractCommandManager<BuilderMode, SubCommand> {

    public CommandManager() {
        super(BuilderMode.getInstance());
    }

    @Override
    public boolean handle(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return super.handle(sender, label, args);
        }

        List<String> allowedWorlds = BuilderMode.getOkaeriConfig().getEnabledWorlds();

        if (player.hasPermission("buildermode.bypassworldcheck") || allowedWorlds.contains(player.getWorld().getName())) {
            return super.handle(sender, label, args);
        }

        Text.msg(player, "You cannot use Builder Mode commands in this world.");
        return true;
    }
}
