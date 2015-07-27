package eu.phiwa.dragontravel.core.anticheatplugins;

import org.bukkit.entity.Player;

public interface AbstractHandler {
    public void startExempting(Player player);

    public void stopExempting(Player player);
}