package eu.phiwa.dragontravel.core.permissions;

import org.bukkit.command.CommandSender;


public class PermissionsHandler {

	/**
	 * Checks if the specified player has the permission to use the specified flight
	 *
	 * @param sender     Player to check the permission for
	 * @param flightname Flight to check the permission for (if null, only checks the general permission)
	 * @return "True" if the player has the permissions, "false" if he hasn't
	 */
	public static boolean hasFlightPermission(CommandSender sender, String flightname) {

		if (sender.hasPermission("dt.flight.*")) // wildcard
			return true;
		else {
			if (flightname == null) // If no flightname is specified, we got a problem, but simply allow it...^^
				return true;
			else {
				if (sender.hasPermission("dt.flight." + flightname)) // flight-specific
					return true;
				else
					return false;    // No permission
			}
		}
	}

	/**
	 * Checks if the specified player has the permission to use
	 * the specified travel-type to travel to the specified destination.
	 *
	 * @param sender          Player to check the permission for
	 * @param traveltype      Type of travel ("travel", "...", ...)
	 * @param destinationname Destination to check the permission for (if null, only checks the general permission)
	 * @return "True" if the player has the permissions, "false" if he hasn't
	 */
	public static boolean hasTravelPermission(CommandSender sender, String traveltype, String destinationname) {

		// Stops any NPEs ("traveltype" is 'travel'/'ctravel'/'ptravel'), if a problem occurs, simply allow it.^^
		if (traveltype == null)
			return true;

		if (traveltype == "travel") {
			// Check for "travel"-permissions
			if (sender.hasPermission("dt.travel.*"))    // wildcard
				return true;
			else if (sender.hasPermission("dt.travel." + destinationname))    // station-specific
				return true;
			else
				return false;    // No permission
		} else {
			// Check for all Traveltypes other than "travel" (dt.ctravel / dt.ptravel)
			if (sender.hasPermission("dt." + traveltype))
				return true;
			else
				return false;
		}
	}
}
