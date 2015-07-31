package eu.phiwa.dragontravel.core.hooks.anticheat;

import org.bukkit.entity.Player;

interface AbstractHandler {
    void startExempting(Player player);

    void stopExempting(Player player);
}