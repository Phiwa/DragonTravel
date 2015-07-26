package eu.phiwa.dragontravel.core.flights;

import eu.phiwa.dragontravel.core.DragonTravelMain;
import eu.phiwa.dragontravel.core.objects.Flight;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

public class FlightEditor implements Listener {

	public static HashMap<Player, Flight> editors = new HashMap<Player, Flight>();


	public FlightEditor() {
	}

	public static void addEditor(Player player, String flightName, String displayName) {
		if (!editors.containsKey(player))
			editors.put(player, new Flight(flightName, displayName));
	}

	public static boolean isEditor(Player player) {
		return editors.containsKey(player);
	}

	public static boolean removeEditor(Player player) {
		return editors.remove(player) != null;
	}

	@EventHandler
	public void onWP(PlayerInteractEvent event) {

		Player player = event.getPlayer();
		Location loc = player.getLocation();

		if (!editors.containsKey(player))
			return;

		if (player.getItemInHand().getType() != Material.BOWL)
			return;

		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			Flight flight = editors.get(player);
			if (!flight.getWaypoints().isEmpty()) {
				flight.removelastWaypoint();
			}

			player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Flights.Successful.WaypointRemoved"));
			return;
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
			Flight flight = editors.get(player);
			Waypoint wp = new Waypoint();
			wp.setX(loc.getBlockX());
			wp.setY(loc.getBlockY());
			wp.setZ(loc.getBlockZ());
			wp.setWorldName(loc.getWorld().getName());
			flight.addWaypoint(wp);

			// Create a marker at the waypoint
			wp.setMarker(player);
			Block block = player.getLocation().getBlock();
			DragonTravelMain.globalwaypointmarkers.put(block, block);

			player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Flights.Successful.WaypointAdded"));
		}
	}


}
