package eu.phiwa.dt.flights;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
		Flight flight = editors.remove(player);
		if (flight == null) return false;

		World world = Bukkit.getWorld(flight.worldName);
		if (world == null) return true;

		Location loc = new Location(Bukkit.getWorld(flight.worldName), 0, 0, 0);
		Block block;
		for (Waypoint wp : flight.waypoints) {
			wp.toLocation(loc);
			block = world.getBlockAt(loc);
			player.sendBlockChange(loc, block.getType(), block.getData());
		}
		return true;
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
			flight.removelastWaypoint(player);

			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Successful.WaypointRemoved"));
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
			player.sendBlockChange(player.getLocation(), Material.GLOWSTONE, (byte) 0);
			Block block = player.getLocation().getBlock();
			DragonTravelMain.globalwaypointmarkers.put(block, block);

			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Successful.WaypointAdded"));
		}
	}
}
