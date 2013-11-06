package eu.phiwa.dt.payment;

import org.bukkit.entity.Player;

/**
 * A method for DragonTravel to exact payment from players.
 * <p>
 * A PaymentHandler must define a constructor that takes no arguments.
 */
public interface PaymentHandler {

	/**
	 * Do any setup required to determine if this payment method is
	 * available.
	 * <p>
	 * If the payment method is permanently unavailable on this server (e.g.
	 * a missing required plugin), the method should return false.
	 *
	 * @return if setup was successful
	 */
	public boolean setup();

	/**
	 * A descriptive name for the PaymentHandler.
	 * <p>
	 *
	 * Grammatically, it should fit in:
	 *
	 * <pre>
	 * Payment set up using {toString()}.
	 * </pre>
	 *
	 * @return a descriptive name
	 */
	public String toString();

	/**
	 * Charge the player the correct amount for the given ChargeType.
	 *
	 * @param type Type of charge this is for
	 * @param player player to charge
	 * @return if the player had enough to pay for the charge
	 */
	public boolean chargePlayer(ChargeType type, Player player);

	/**
	 * Charge the player the given amount for the given ChargeType.
	 *
	 * @param type Type of charge this is for
	 * @param player Player to charge
	 * @param customCost Amount to charge
	 * @return if the player had enough to pay for the charge
	 */
	public boolean chargePlayerExact(ChargeType type, Player player, double customCost);
}
