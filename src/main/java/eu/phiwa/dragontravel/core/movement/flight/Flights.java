package eu.phiwa.dragontravel.core.movement.flight;

import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.hooks.server.IRyeDragon;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Flights {

    /**
     * @param checkForStation Whether or not DragonTravel should check
     *                        if the player is at a station and return if not.
     *                        If the admin disabled the station-check globally,
     *                        this has no function.
     * @param sentbyadmin     Whether or not the player was sent on the flight by admin.
     *                        If true, checks like the one for the required item and stations
     *                        are not run and messages about errors are printed to the sending player.
     * @param sendingPlayer   Player who sent the player on a flight.
     *                        Gets all messages about problems until the flight is started
     */
    public static void startFlight(Player player, String flightname, Boolean checkForStation, boolean sentbyadmin, CommandSender sendingPlayer) {

        CommandSender sender;

        if (sentbyadmin)
            sender = sendingPlayer;
        else
            sender = player;

        Flight flight = DragonTravel.getInstance().getDbFlightsHandler().getFlight(flightname);

        if (flight == null) {
            // Sent by console
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Error.FlightDoesNotExist"));
            return;
        }

        // Checks do not need to be performed,
        // if the player is sent on the flight by an admin
        if (!sentbyadmin) {
            if (checkForStation && DragonTravel.getInstance().getConfig().getBoolean("MountingLimit.EnableForFlights") && !player.hasPermission("dt.ignoreusestations.flights")) {
                if (!DragonTravel.getInstance().getDbStationsHandler().checkForStation(player)) {
                    sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.NotAtAStation"));
                    return;
                }
            }

            if (DragonTravel.getInstance().getConfigHandler().isRequireItemFlight()) {
                if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.flight")) {
                    sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                    return;
                }
            }
        }

        Location temploc = player.getLocation();
        Location firstwp = flight.getWaypoints().get(0).getAsLocation();
        temploc.setYaw(getCorrectYawForPlayer(player, firstwp));
        player.teleport(temploc);

        if (DragonTravel.getInstance().getDragonManager().mount(player, true))
            return;

        if (!DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player))
            return;

        if (sentbyadmin) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Successful.SentPlayer").replace("{flightname}", flight.getDisplayName()));
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Successful.SendingPlayer").replace("{playername}", player.getName()).replace("{flightname}", flight.getDisplayName()));
        } else
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Successful.StartingFlight").replace("{flightname}", flight.getDisplayName()));
        IRyeDragon dragon = DragonTravel.getInstance().getDragonManager().getRiderDragons().get(player);
        dragon.setCustomName(ChatColor.translateAlternateColorCodes('&', DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Successful.StartingFlight").replace("{flightname}", flight.getDisplayName())));
        dragon.startFlight(flight);
    }

    private static float getCorrectYawForPlayer(Player player, Location destination) {

        if (player.getLocation().getBlockZ() > destination.getBlockZ())
            return (float) (-Math.toDegrees(Math.atan((player.getLocation().getBlockX() - destination.getBlockX()) / (player.getLocation().getBlockZ() - destination.getBlockZ())))) + 180.0F;
        else if (player.getLocation().getBlockZ() < destination.getBlockZ())
            return (float) (-Math.toDegrees(Math.atan((player.getLocation().getBlockX() - destination.getBlockX()) / (player.getLocation().getBlockZ() - destination.getBlockZ()))));
        else
            return player.getLocation().getYaw();
    }
}
