package eu.phiwa.dragontravel.core.hooks.payment;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FreePaymentHandler implements PaymentHandler {

    @Override
    public boolean setup() {
        return true;
    }

    @Override
    public boolean chargePlayer(ChargeType type, Player player) {
        return true;
    }

    @Override
    public boolean chargePlayerExact(ChargeType type, Player player, double customCost) {
        return true;
    }

    @Override
    public String toString() {
        return ChatColor.GOLD + "free";
    }
}