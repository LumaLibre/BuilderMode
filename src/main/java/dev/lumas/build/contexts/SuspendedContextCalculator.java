package dev.lumas.build.contexts;

import dev.lumas.build.model.SuspendedPlayerRegistry;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SuspendedContextCalculator implements ContextCalculator<Player> {
    @Override
    public void calculate(@NotNull Player target, @NotNull ContextConsumer consumer) {
        if (SuspendedPlayerRegistry.INSTANCE.isSuspended(target.getUniqueId())) {
            consumer.accept("suspended", "true");
        } else {
            consumer.accept("suspended", "false");
        }
    }

    @Override
    public @NotNull ContextSet estimatePotentialContexts() {
        return ImmutableContextSet.builder()
                .add("suspended", "true")
                .add("suspended", "false")
                .build();
    }
}
