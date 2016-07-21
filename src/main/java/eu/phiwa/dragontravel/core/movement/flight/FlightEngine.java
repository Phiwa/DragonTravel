package eu.phiwa.dragontravel.core.movement.flight;

import eu.phiwa.dragontravel.api.DragonException;
import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.hooks.server.IRyeDragon;
import eu.phiwa.dragontravel.core.movement.DragonType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlightEngine {

    /**
     * @param checkForStation Whether or not DragonTravel should check
     *                        if the player is at a station and return if not.
     *                        If the admin disabled the station-check globally,
     *                        this has no function.
     * @param sentByAdmin     Whether or not the player was sent on the flight by admin.
     *                        If true, checks like the one for the required item and stations
     *                        are not run and messages about errors are printed to the sending player.
     * @param sendingPlayer   Player who sent the player on a flight.
     *                        Gets all messages about problems until the flight is started
     */
    public void startFlight(Player player, String flightName, Boolean checkForStation, CommandSender sendingPlayer) throws DragonException {

        CommandSender sender;
        
        // Player sent by an admin?
        boolean sentByAdmin = (sendingPlayer != null);

        if (sentByAdmin)
            sender = sendingPlayer;
        else
            sender = player;

        Flight flight = DragonTravel.getInstance().getDbFlightsHandler().getFlight(flightName);

        if (DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player))
            return;

        if (flight == null) {
            // Sent by console
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Error.FlightDoesNotExist"));
            throw new DragonException("Flight not recognised.");
        }

        // Checks do not need to be performed,
        // if the player is sent on the flight by an admin
        if (!sentByAdmin) {
            if (checkForStation && DragonTravel.getInstance().getConfig().getBoolean("MountingLimit.EnableForFlights") && !player.hasPermission("dt.ignoreusestations.flights")) {
                if (!DragonTravel.getInstance().getDbStationsHandler().checkForStation(player)) {
                    sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.NotAtAStation"));
                    throw new DragonException("Player is not near a station.");
                }
            }

            if (DragonTravel.getInstance().getConfigHandler().isRequireItemFlight()) {
                if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.flight")) {
                    sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                    throw new DragonException("Player does not have the required item.");
                }
            }
        }

        Location tempLoc = player.getLocation();
        Location firstWP = flight.getWaypoints().get(0).getAsLocation();
        tempLoc.setYaw(getCorrectYawForPlayer(player, firstWP));
        player.teleport(tempLoc);

        if (!DragonTravel.getInstance().getDragonManager().mount(player, true, DragonType.MANNED_FLIGHT))
            return;

        if (sentByAdmin) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Successful.SentPlayer").replace("{flightname}", flight.getDisplayName()));
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Successful.SendingPlayer").replace("{playername}", player.getName()).replace("{flightname}", flight.getDisplayName()));
        } else
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Successful.StartingFlight").replace("{flightname}", flight.getDisplayName()));
        IRyeDragon dragon = DragonTravel.getInstance().getDragonManager().getRiderDragons().get(player);
        dragon.setCustomName(ChatColor.translateAlternateColorCodes('&', DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Successful.StartingFlight").replace("{flightname}", flight.getDisplayName())));
        dragon.startFlight(flight, DragonType.MANNED_FLIGHT);
    }

    private float getCorrectYawForPlayer(Player player, Location destination) {

        if (player.getLocation().getBlockZ() > destination.getBlockZ())
            return (float) (-Math.toDegrees(Math.atan((player.getLocation().getBlockX() - destination.getBlockX()) / (player.getLocation().getBlockZ() - destination.getBlockZ())))) + 180.0F;
        else if (player.getLocation().getBlockZ() < destination.getBlockZ())
            return (float) (-Math.toDegrees(Math.atan((player.getLocation().getBlockX() - destination.getBlockX()) / (player.getLocation().getBlockZ() - destination.getBlockZ()))));
        else
            return player.getLocation().getYaw();
    }
}
