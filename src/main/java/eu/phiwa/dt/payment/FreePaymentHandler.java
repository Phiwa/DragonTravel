package eu.phiwa.dt.payment;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * This PaymentHandler exacts no cost on the player and is always available.
 */
public class FreePaymentHandler implements PaymentHandler {

	public FreePaymentHandler() { }

	@Override
	public boolean setup() {
		return true;
	}

	@Override
	public String toString() {
		return ChatColor.GOLD + "free";
	}

	@Override
	public boolean chargePlayer(ChargeType type, Player player) {
		return true;
	}

	@Override
	public boolean chargePlayerExact(ChargeType type, Player player, double customCost) {
		return true;
	}
}
