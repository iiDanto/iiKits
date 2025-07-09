package dev.iidanto.kitPreview.events;

import dev.iidanto.kitPreview.objects.Kit;
import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class KitLoadEvent extends Event implements Cancellable {

    @Getter
    private static final HandlerList HANDLERS = new HandlerList();
    @Getter
    private final Kit kit;
    private boolean cancelled = false;

    public KitLoadEvent(Kit kit) {
        this.kit = kit;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
