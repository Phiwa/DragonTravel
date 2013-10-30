package eu.phiwa.dt.anticheatplugins;

import org.bukkit.entity.Player;

import eu.phiwa.dt.DragonTravelMain;

public class CheatProtectionHandler {
	
	/** 
	 * Exempts a player from the Cheat-check of AntiCheat-plugins
	 *
	 * @param player
	 * 				the player to exempt from the check
	 */
	public static void exemptPlayerFromCheatChecks(Player player) {
		
		// AntiCheat
		if (DragonTravelMain.anticheat	&& !net.h31ix.anticheat.api.AnticheatAPI
							.isExempt(player, net.h31ix.anticheat.manage.CheckType.FLY)) {
			net.h31ix.anticheat.api.AnticheatAPI.exemptPlayer(player,net.h31ix.anticheat.manage.CheckType.FLY);
		}
				
		// NoCheatPlus
		if (DragonTravelMain.nocheatplus
				&& !fr.neatmonster.nocheatplus.hooks.NCPExemptionManager
						.isExempted(player, fr.neatmonster.nocheatplus.checks.CheckType.MOVING_SURVIVALFLY)
				&& !fr.neatmonster.nocheatplus.hooks.NCPExemptionManager
						.isExempted(player, fr.neatmonster.nocheatplus.checks.CheckType.MOVING_CREATIVEFLY)
			) {
			fr.neatmonster.nocheatplus.hooks.NCPExemptionManager
							.exemptPermanently(player, fr.neatmonster.nocheatplus.checks.CheckType.MOVING_SURVIVALFLY);
			fr.neatmonster.nocheatplus.hooks.NCPExemptionManager
							.exemptPermanently(player, fr.neatmonster.nocheatplus.checks.CheckType.MOVING_CREATIVEFLY);		
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
		if (DragonTravelMain.anticheat && net.h31ix.anticheat.api.AnticheatAPI
							.isExempt(player,net.h31ix.anticheat.manage.CheckType.FLY)) {
			net.h31ix.anticheat.api.AnticheatAPI
							.unexemptPlayer(player,net.h31ix.anticheat.manage.CheckType.FLY);
		}
		
		// NoCheatPlus
		if (DragonTravelMain.nocheatplus
				&& fr.neatmonster.nocheatplus.hooks.NCPExemptionManager
							.isExempted(player, fr.neatmonster.nocheatplus.checks.CheckType.MOVING_SURVIVALFLY)
				&& fr.neatmonster.nocheatplus.hooks.NCPExemptionManager
							.isExempted(player, fr.neatmonster.nocheatplus.checks.CheckType.MOVING_CREATIVEFLY)
							
			) {
			fr.neatmonster.nocheatplus.hooks.NCPExemptionManager.unexempt(player);
		}
	}

}
