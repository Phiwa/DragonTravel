package eu.phiwa.dragontravel.core.anticheatplugins;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CheatProtectionHandler {
	private static NoCheatPlusHandler ncpHandle = null;
	private static AntiCheatHandler acHandle = null;

	public static void setup() {
		try {
			acHandle = new AntiCheatHandler();
		} catch (Throwable t) {
			acHandle = null;
		}
		if (acHandle != null) {
			Bukkit.getLogger().info("[DragonTravel] AntiCheat support enabled");
		}

		try {
			ncpHandle = new NoCheatPlusHandler();
		} catch (Throwable t) {
			ncpHandle = null;
		}
		if (ncpHandle != null) {
			Bukkit.getLogger().info("[DragonTravel] NoCheatPlus support enabled");
		}
	}

	/**
	 * Exempts a player from the Cheat-check of AntiCheat-plugins
	 *
	 * @param player
	 * 				the player to exempt from the check
	 */
	public static void exemptPlayerFromCheatChecks(Player player) {

		// AntiCheat
		if (acHandle != null) {
			acHandle.startExempting(player);
		}

		// NoCheatPlus
		if (ncpHandle != null) {
			ncpHandle.startExempting(player);
		}
	}

	/**
	 * Unexempts a player from the Cheat-check of AntiCheat-plugins
	 *
	 * @param player
	 * 				the player to unexempt from the check
	 */
	public static void unexemptPlayerFromCheatChecks(Player player) {
		// AntiCheat
		if (acHandle != null) {
			acHandle.stopExempting(player);
		}

		// NoCheatPlus
		if (ncpHandle != null) {
			ncpHandle.stopExempting(player);
		}
	}

}
