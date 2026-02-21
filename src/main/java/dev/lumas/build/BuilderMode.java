package dev.lumas.build;

import com.google.common.base.Preconditions;
import dev.lumas.build.configuration.Config;
import dev.lumas.build.contexts.SuspendedContextCalculator;
import dev.lumas.build.events.CanvasListeners;
import dev.lumas.build.model.SuspendedPlayer;
import dev.lumas.build.model.SuspendedPlayerRegistry;
import dev.lumas.lumacore.manager.modules.ModuleManager;
import dev.lumas.lumacore.utility.Logging;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.serdes.standard.StandardSerdes;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;


public final class BuilderMode extends JavaPlugin {

    private static final boolean IS_CANVAS;

    static {
        boolean b = false;
        try {
            Class.forName("io.canvasmc.canvas.event.EntityTeleportAsyncEvent");
            b = true;
        } catch (ClassNotFoundException ignored) {}
        IS_CANVAS = b;
    }


    @Getter
    private static BuilderMode instance;
    @Getter
    private static Config okaeriConfig;

    private static ModuleManager moduleManager;
    private static LuckPerms luckPerms;
    private static SuspendedContextCalculator suspendedContextCalculator;

    @Override
    public void onLoad() {
        instance = this;
        okaeriConfig = loadConfig(Config.class, "config.yml");
        moduleManager = new ModuleManager(this);
    }

    @Override
    public void onEnable() {
        moduleManager.reflectivelyRegisterModules();
        suspendedContextCalculator = new SuspendedContextCalculator();
        luckPerms = Preconditions.checkNotNull(Bukkit.getServicesManager().getRegistration(LuckPerms.class)).getProvider();
        luckPerms.getContextManager().registerCalculator(suspendedContextCalculator);

        if (IS_CANVAS) {
            getServer().getPluginManager().registerEvents(new CanvasListeners(), this);
        }
    }

    @Override
    public void onDisable() {
        moduleManager.unregisterModules();

        for (SuspendedPlayer suspendedPlayer : SuspendedPlayerRegistry.INSTANCE) {
            Player player = suspendedPlayer.getPlayer();
            if (player != null && player.isOnline()) {
                suspendedPlayer.resume(player);
            } else {
                Logging.errorLog("Could not resume suspended player with UUID " + suspendedPlayer.getUuid() + " as they are offline.");
            }
        }
        SuspendedPlayerRegistry.INSTANCE.clear();

        luckPerms.getContextManager().unregisterCalculator(suspendedContextCalculator);
    }

    public <T extends OkaeriConfig> T loadConfig(Class<T> configClass, String fileName) {
        Path bindFile = this.getDataPath().resolve(fileName);
        return ConfigManager.create(configClass, it -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new StandardSerdes());
            it.withRemoveOrphans(false);
            it.withBindFile(bindFile);

            it.saveDefaults();
            it.load(true);
        });
    }
}