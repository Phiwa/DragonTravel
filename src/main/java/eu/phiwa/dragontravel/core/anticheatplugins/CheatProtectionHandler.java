package eu.phiwa.dragontravel.core.anticheatplugins;

import org.bukkit.entity.Player;

import eu.phiwa.dragontravel.core.DragonTravelMain;

public class CheatProtectionHandler {
	
	/** 
	 * Exempts a player from the Cheat-check of AntiCheat-plugins
	 *
	 * @param player
	 * 				the player to exempt from the check
	 */
	public static void exemptPlayerFromCheatChecks(Player player) {
		
		// AntiCheat
		if (DragonTravelMain.anticheat) {
			if(!net.gravitydevelopment.anticheat.api.AntiCheatAPI.isExempt(player, net.gravitydevelopment.anticheat.check.CheckType.FLY)) {
				net.gravitydevelopment.anticheat.api.AntiCheatAPI.exemptPlayer(player, net.gravitydevelopment.anticheat.check.CheckType.FLY);
			}
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
		if (DragonTravelMain.anticheat) {
			
			if(net.gravitydevelopment.anticheat.api.AntiCheatAPI.isExempt(player, net.gravitydevelopment.anticheat.check.CheckType.FLY)) {
				net.gravitydevelopment.anticheat.api.AntiCheatAPI.unexemptPlayer(player,net.gravitydevelopment.anticheat.check.CheckType.FLY);
			}
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
