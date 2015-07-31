package eu.phiwa.dragontravel.api.events;

import org.bukkit.event.Cancellable;

public abstract class DragonCancellableEvent extends DragonEvent implements Cancellable {

    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
