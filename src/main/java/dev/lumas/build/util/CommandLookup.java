package dev.lumas.build.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

// Liberally borrowed from PlugManX
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommandLookup {

    private static final Class<?> pluginClassLoaderClass;
    private static final Function<ClassLoader, Plugin> getPluginFromClassLoader;

    static {
        try {
            pluginClassLoaderClass = Class.forName("org.bukkit.plugin.java.PluginClassLoader");

            getPluginFromClassLoader = instance -> {
                try {
                    Field pluginField = instance.getClass().getDeclaredField("plugin");
                    pluginField.setAccessible(true);
                    return (Plugin) pluginField.get(instance);
                } catch (IllegalAccessException | NoSuchFieldException exception) {
                    throw new RuntimeException(exception);
                }
            };
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public static List<String> findByCommand(String commandName) {
        var plugins = new ArrayList<String>();

        for (var entry : Bukkit.getCommandMap().getKnownCommands().entrySet()) {
            Command command = entry.getValue();


            var cl = command.getClass().getClassLoader();

            if (cl.getClass() != pluginClassLoaderClass) handleNonPluginClassLoaderCommand(entry, commandName, plugins);
            else handlePluginClassLoaderCommand(entry, commandName, plugins, cl);
        }

        return plugins;
    }

    private static void handleNonPluginClassLoaderCommand(Map.Entry<String, Command> entry, String command, List<String> plugins) {
        var parts = entry.getKey().split(":");

        if (parts.length != 2 || !parts[1].equalsIgnoreCase(command)) return;
        Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(pl -> pl.getName().equalsIgnoreCase(parts[0]))
                .findFirst().ifPresent(plugin -> plugins.add(plugin.getName()));
    }

    private static void handlePluginClassLoaderCommand(Map.Entry<String, Command> entry, String command, List<String> plugins, ClassLoader classLoader) {
        var parts = entry.getKey().split(":");
        var cmd = parts[parts.length - 1];

        if (!cmd.equalsIgnoreCase(command)) return;

        var plugin = (JavaPlugin) getPluginFromClassLoader.apply(classLoader);

        if (!plugins.contains(plugin.getName())) plugins.add(plugin.getName());
    }
}
