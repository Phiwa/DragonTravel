package eu.phiwa.dragontravel.core.hooks.anticheat;

import org.bukkit.entity.Player;

public interface AbstractHandler {
    void startExempting(Player player);

    void stopExempting(Player player);
}