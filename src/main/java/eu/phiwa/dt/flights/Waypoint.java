package eu.phiwa.dt.flights;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.Flight;

public class Waypoint {
	public boolean finalwp = false;
	public int x;
	public int y;
	public int z;
	public Block marker;

	public Waypoint() {
	}

	public Waypoint(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public String toString() {
		return "WP: (" + x + ", " + y + ", " + z + ", " + finalwp;
	}

	public void setMarker(Player player) {
		marker = player.getLocation().getBlock();
		marker.setType(Material.GLOWSTONE);
		DragonTravelMain.globalwaypointmarkers.put(this.marker, this.marker);
	}

	public void removeMarker() {
		DragonTravelMain.globalwaypointmarkers.remove(this.marker);
		this.marker.setType(Material.AIR);
	}

	/**
	 * Removes all WaypointMarkers in the server
	 *
	 */
	public static void removeWaypointMarkersGlobally() {

		Collection<Block> globalmarkers = DragonTravelMain.globalwaypointmarkers.values();

		for (Block marker : globalmarkers) {
			marker.getWorld().getBlockAt(marker.getX(), marker.getY(), marker.getZ()).getChunk().load(true);
			marker.setType(Material.AIR);
		}
	}

	/**
	 * Removes all WaypointMarkers of the specified flight
	 *
	 * @param flight Flight whose waypoint-markers you want to remove
	 */
	public static void removeWaypointMarkersOfFlight(Flight flight) {
		for (Waypoint wp : flight.waypoints) {
			if (DragonTravelMain.globalwaypointmarkers.containsKey(wp.marker))
				DragonTravelMain.globalwaypointmarkers.remove(wp.marker);

			if (wp.marker == null)
				continue;
			Bukkit.getWorld(flight.worldName).getBlockAt(wp.x, wp.y, wp.z).getChunk().load(false);
			wp.marker.setType(Material.AIR);
		}
	}

	public String saveToString() {
		return x + "%" + y + "%" + z;
	}

	public static Waypoint loadFromString(String wpData) {
		String[] wpDataParts = wpData.split("%");

		try {
			int x = Integer.parseInt(wpDataParts[0]);
			int y = Integer.parseInt(wpDataParts[1]);
			int z = Integer.parseInt(wpDataParts[2]);

			return new Waypoint(x, y, z);
		} catch (NumberFormatException ex) {
			DragonTravelMain.plugin.getLogger().warning("Unable to read waypoint: " + wpData);
		} catch (IndexOutOfBoundsException ex) {
			DragonTravelMain.plugin.getLogger().warning("Unable to read waypoint: " + wpData);
		}
		return null;
	}
}
