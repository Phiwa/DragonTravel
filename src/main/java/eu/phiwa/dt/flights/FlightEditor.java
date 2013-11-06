package eu.phiwa.dt.flights;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.Flight;

public class FlightEditor implements Listener {

	public static HashMap<Player, Flight> editors = new HashMap<Player, Flight>();


	public FlightEditor() {
	}

	public static boolean isEditor(Player player) {
		return editors.containsKey(player);
	}

	public static void addEditor(Player player, String flightname) {
		if (!editors.containsKey(player))
			editors.put(player, new Flight(player.getWorld(), flightname));
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
			flight.removelastWaypoint();

			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Successful.WaypointRemoved"));
			// TODO: ---ADD MESSAGE Successfully removed the last waypoint
			return;
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
			Flight flight = editors.get(player);
			Waypoint wp = new Waypoint();
			wp.x = (int) loc.getX();
			wp.y = (int) loc.getY();
			wp.z = (int) loc.getZ();
			flight.addWaypoint(wp);

			// Create a marker at the waypoint
			wp.setMarker(player.getLocation());
			Block block = player.getLocation().getBlock();
			DragonTravelMain.globalwaypointmarkers.put(block, block);

			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Successful.WaypointAdded"));
			// TODO: ---ADD MESSAGE Successfully added a waypoint
		}
	}
}
