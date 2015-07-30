package eu.phiwa.dragontravel.core.hooks.permissions;

import org.bukkit.command.CommandSender;


public class PermissionsHandler {

    /**
     * Checks if the specified player has the permission to use the specified flight
     *
     * @param sender     Player to check the permission for
     * @param flightName Flight to check the permission for (if null, only checks the general permission)
     * @return "True" if the player has the permissions, "false" if he hasn't
     */
    public static boolean hasFlightPermission(CommandSender sender, String flightName) {
        return sender.hasPermission("dt.flight.*") || flightName == null || sender.hasPermission("dt.flight." + flightName);
    }

    /**
     * Checks if the specified player has the permission to use
     * the specified travel-type to travel to the specified destination.
     *
     * @param sender     Player to check the permission for
     * @param travelType Type of travel ("travel", "...", ...)
     * @param destName   Destination to check the permission for (if null, only checks the general permission)
     * @return "True" if the player has the permissions, "false" if he hasn't
     */
    public static boolean hasTravelPermission(CommandSender sender, String travelType, String destName) {
        if (travelType == null)
            return true;
        if (travelType.equals("travel")) {
            return sender.hasPermission("dt.travel.*") || sender.hasPermission("dt.travel." + destName);
        } else {
            return sender.hasPermission("dt." + travelType);
        }
    }
}
