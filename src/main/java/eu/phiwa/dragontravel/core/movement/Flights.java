package eu.phiwa.dragontravel.core.movement;

import eu.phiwa.dragontravel.core.DragonTravelMain;
import eu.phiwa.dragontravel.core.modules.DragonManagement;
import eu.phiwa.dragontravel.core.objects.Flight;
import eu.phiwa.dragontravel.nms.IRyeDragon;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Flights {

	/**
	 * @param player
	 * @param flightname
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

		Flight flight = DragonTravelMain.getInstance().getDbFlightsHandler().getFlight(flightname);

		if (flight == null) {
			// Sent by console
			sender.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Flights.Error.FlightDoesNotExist"));
			return;
		}

		// Checks do not need to be performed,
		// if the player is sent on the flight by an admin
		if (!sentbyadmin) {
			if (checkForStation && DragonTravelMain.getInstance().getConfig().getBoolean("MountingLimit.EnableForFlights") && !player.hasPermission("dt.ignoreusestations.flights")) {
				if (!DragonTravelMain.getInstance().getDbStationsHandler().checkForStation(player)) {
					sender.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.NotAtAStation"));
					return;
				}
			}

			if (DragonTravelMain.getInstance().getConfigHandler().isRequireItemFlight()) {
				if (!player.getInventory().contains(DragonTravelMain.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.flight")) {
					sender.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
					return;
				}
			}
		}

		Location temploc = player.getLocation();
		Location firstwp = flight.getWaypoints().get(0).getAsLocation();
		temploc.setYaw(getCorrectYawForPlayer(player, firstwp));
		player.teleport(temploc);

		if (!DragonManagement.mount(player, true))
			return;

		if (!DragonTravelMain.listofDragonriders.containsKey(player))
			return;

		if (sentbyadmin) {
			player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Flights.Successful.SentPlayer").replace("{flightname}", flight.getDisplayName()));
			player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Flights.Successful.SendingPlayer").replace("{playername}", player.getName()).replace("{flightname}", flight.getDisplayName()));
		} else
			player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Flights.Successful.StartingFlight").replace("{flightname}", flight.getDisplayName()));
		IRyeDragon dragon = DragonTravelMain.listofDragonriders.get(player);
		dragon.setCustomName(ChatColor.translateAlternateColorCodes('&', DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Flights.Successful.StartingFlight").replace("{flightname}", flight.getDisplayName())));
		dragon.setTotalDist(Math.round(flight.getDistance() + Math.hypot(firstwp.getBlockX() - temploc.getBlockX(), firstwp.getBlockZ() - temploc.getBlockZ())));
		dragon.setCoveredDist(1);
		((LivingEntity) dragon.getEntity()).setMaxHealth(dragon.getTotalDist() + 10);
		((LivingEntity) dragon.getEntity()).setHealth(dragon.getCoveredDist());
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
