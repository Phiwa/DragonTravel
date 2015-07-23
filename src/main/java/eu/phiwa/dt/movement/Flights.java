package eu.phiwa.dt.movement;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.RyeDragon;
import eu.phiwa.dt.modules.DragonManagement;
import eu.phiwa.dt.objects.Flight;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Flights {

	private static float getCorrectYawForPlayer(Player player, Location destination) {
	
		if (player.getLocation().getBlockZ() > destination.getBlockZ())
		    return (float) (-Math.toDegrees(Math.atan((player.getLocation().getBlockX() - destination.getBlockX())    / (player.getLocation().getBlockZ() - destination.getBlockZ())))) + 180.0F;
		else if (player.getLocation().getBlockZ() < destination.getBlockZ())
		    return (float) (-Math.toDegrees(Math.atan((player.getLocation().getBlockX() - destination.getBlockX())    / (player.getLocation().getBlockZ() - destination.getBlockZ()))));
		else
		    return player.getLocation().getYaw();
	}

	/**
	 * 
	 * @param player
	 * @param flightname
	 * @param checkForStation
	 * 			Whether or not DragonTravel should check
	 * 			if the player is at a station and return if not.
	 * 			If the admin disabled the station-check globally,
	 * 			this has no function.
	 * @param sentbyadmin
	 * 			Whether or not the player was sent on the flight by admin.
	 * 			If true, checks like the one for the required item and stations
	 * 			are not run and messages about errors are printed to the sending player.
	 * @param sendingPlayer
	 * 			Player who sent the player on a flight.
	 * 			Gets all messages about problems until the flight is started
	 */
	public static void startFlight(Player player, String flightname, Boolean checkForStation, boolean sentbyadmin, Player sendingPlayer) {
		
		Player playerToSendMessagesTo;
		
		if(sentbyadmin)
			playerToSendMessagesTo = sendingPlayer;
		else
			playerToSendMessagesTo = player;
		
		Flight flight = DragonTravelMain.dbFlightsHandler.getFlight(flightname);

		if(flight == null)  {
			// Sent by console
			if(sentbyadmin && playerToSendMessagesTo == null)
				System.out.println("[DragonTravel] Flight does not exist!");
			else	
				playerToSendMessagesTo.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.FlightDoesNotExist"));
			return;
		}
		
		if(flight.waypoints.get(0).world.getName() != player.getWorld().getName()) {
			// Sent by console
			if(sentbyadmin && playerToSendMessagesTo == null)
				System.out.println("[DragonTravel] The flight is in a different world than the player!");
			else	
				playerToSendMessagesTo.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.FlightIsInDifferentWorld"));
			return;
		}
		
		// Checks do not need to be performed,
		// if the player is sent on the flight by an admin
		if(!sentbyadmin) {
			if(checkForStation && DragonTravelMain.config.getBoolean("MountingLimit.EnableForFlights") && !player.hasPermission("dt.ignoreusestations.flights")) {
				if(!DragonTravelMain.dbStationsHandler.checkForStation(player)) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Stations.Error.NotAtAStation"));
					return;
				}
			}
		
			if(DragonTravelMain.requireItemFlight) {
				if(!player.getInventory().contains(DragonTravelMain.requiredItem) && !player.hasPermission("dt.notrequireitem.flight")) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.RequiredItemMissing"));
					return;
				}
			}
		}

		Location temploc = player.getLocation();
		Location firstwp = flight.waypoints.get(0).getAsLocation();
		temploc.setYaw(getCorrectYawForPlayer(player, firstwp));
		player.teleport(temploc);
		
		if (!DragonManagement.mount(player, true))
			return;
		
		if (!DragonTravelMain.listofDragonriders.containsKey(player))
			return;
		
		if(sentbyadmin) {
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Successful.SentPlayer").replace("{flightname}", flight.displayname));
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Successful.SendingPlayer").replace("{playername}", player.getName()).replace("{flightname}", flight.displayname));
		}
		else
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Successful.StartingFlight").replace("{flightname}", flight.displayname));		
		RyeDragon dragon = DragonTravelMain.listofDragonriders.get(player);
		dragon.setCustomName(ChatColor.translateAlternateColorCodes('&', DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Successful.StartingFlight").replace("{flightname}", flight.displayname)));
		dragon.setTotalDist(Math.round(flight.getDistance() + Math.hypot(firstwp.getBlockX() - temploc.getBlockX(), firstwp.getBlockZ()-temploc.getBlockZ())));
        dragon.setCoveredDist(0);
        ((LivingEntity) dragon.getEntity()).setMaxHealth(1 + dragon.getTotalDist());
		dragon.startFlight(flight);	
	}
}
