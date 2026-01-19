package dev.lumas.build.configuration;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;

import java.util.List;

@Getter
public class Config extends OkaeriConfig {

    private List<String> enabledWorlds = List.of("world", "spawn", "event_new");

    private List<String> allowCommandsFromPlugins = List.of(
            "BuilderMode", "FastAsyncWorldEdit", "WorldEdit", "WorldGuard",
            "TownyChat", "mcMMO"
    );

    // lazy list but whatever
    private List<String> whiteListedCommands = List.of(
            "msg", "tell", "w", "whisper", "reply", "r",
            "tp", "tpa", "tpahere"
    );

}
