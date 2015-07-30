package eu.phiwa.dragontravel.core.hooks.anticheat;

import net.gravitydevelopment.anticheat.api.AntiCheatAPI;
import org.bukkit.entity.Player;

public class AntiCheatHandler implements AbstractHandler {

    static {        // This throws an exception if the class isn't loaded
        AntiCheatAPI.getManager();
    }

    @Override
    public void startExempting(Player player) {
        if (!AntiCheatAPI.isExempt(player, net.gravitydevelopment.anticheat.check.CheckType.FLY)) {
            AntiCheatAPI.exemptPlayer(player, net.gravitydevelopment.anticheat.check.CheckType.FLY);
        }

    }

    @Override
    public void stopExempting(Player player) {
        if (AntiCheatAPI.isExempt(player, net.gravitydevelopment.anticheat.check.CheckType.FLY)) {
            AntiCheatAPI.unexemptPlayer(player, net.gravitydevelopment.anticheat.check.CheckType.FLY);
        }
    }
}
