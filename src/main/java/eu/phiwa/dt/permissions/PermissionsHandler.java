package main.java.eu.phiwa.dt.permissions;

import org.bukkit.entity.Player;


public class PermissionsHandler {

	/**
	 * Checks if the specified player has the permission to use the specified flight
	 * 
	 * @param player
	 * 			Player to check the permission for
	 * @param flightname
	 * 			Flight to check the permission for (if null, only checks the general permission)
	 * @return
	 * 			"True" if the player has the permissions, "false" if he hasn't
	 */
	public static boolean hasFlightPermission(Player player, String flightname){
				
		if (player.hasPermission("dt.flight.*")) // wildcard
			return true;
		else {
			if(flightname == null) // If no flightname is specified, we got a problem, but simply allow it...^^
				return true;
			else {
				if (player.hasPermission("dt.flight." + flightname)) // flight-specific
					return true;
				else
					return false;	// No permission			
			}
		}		
	}
	
	/**
	 * Checks if the specified player has the permission to use
	 * the specified travel-type to travel to the specified destination.
	 * 
	 * @param player
	 * 			Player to check the permission for
	 * @param traveltype
	 * 			Type of travel ("travel", "...", ...)
	 * @param destination
	 * 			Destination to check the permission for (if null, only checks the general permission)
	 * @return
	 * 			"True" if the player has the permissions, "false" if he hasn't
	 */
	public static boolean hasTravelPermission(Player player, String traveltype, String destinationname){
		
		// Stops any NPEs ("traveltype" is 'travel'/'travel'/'ptravel'), 
		// if a problem occurs, simply allow it.^^
		if(traveltype == null)
			return true;
		else if(traveltype != "travel") {
			// Check for all Traveltypes other than "travel"
			if (player.hasPermission("dt." + traveltype))
				return true;
			else				
				return false;
		}
		else {
			// Check for "travel"-permissions
			if (player.hasPermission("dt.travel.*"))	// wildcard
				return true;
			else if (player.hasPermission("dt.travel." + destinationname))	// station-specific
				return true;
			else
				return false;	// No permission
		}
	}	
}
