package dev.lumas.build.contexts;

import dev.lumas.build.model.SuspendedPlayerRegistry;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SuspendedContextCalculator implements ContextCalculator<Player> {
    @Override
    public void calculate(@NonNull Player target, @NonNull ContextConsumer consumer) {
        if (SuspendedPlayerRegistry.INSTANCE.isSuspended(target.getUniqueId())) {
            consumer.accept("suspended", "true");
        } else {
            consumer.accept("suspended", "false");
        }
    }

    @Override
    public @NonNull ContextSet estimatePotentialContexts() {
        return ImmutableContextSet.builder()
                .add("suspended", "true")
                .add("suspended", "false")
                .build();
    }
}
