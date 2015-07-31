package eu.phiwa.dragontravel.core.hooks.payment;

import org.bukkit.entity.Player;

public interface PaymentHandler {

    /**
     * Do any setup required to determine if this payment method is
     * available.
     * <p/>
     * If the payment method is permanently unavailable on this server (e.g.
     * a missing required plugin), the method should return false.
     *
     * @return if setup was successful
     */
    boolean setup();

    /**
     * A descriptive name for the PaymentHandler.
     * <p/>
     * <p/>
     * Grammatically, it should fit in:
     * <p/>
     * <pre>
     * Payment set up using {toString()}.
     * </pre>
     *
     * @return a descriptive name
     */
    String toString();

    /**
     * Charge the player the correct amount for the given ChargeType.
     *
     * @param type   Type of charge this is for
     * @param player player to charge
     * @return if the player had enough to pay for the charge
     */
    boolean chargePlayer(ChargeType type, Player player);

    /**
     * Charge the player the given amount for the given ChargeType.
     *
     * @param type       Type of charge this is for
     * @param player     Player to charge
     * @param customCost Amount to charge
     * @return if the player had enough to pay for the charge
     */
    boolean chargePlayerExact(ChargeType type, Player player, double customCost);
}