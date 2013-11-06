package eu.phiwa.dt.flights;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.Flight;

public class Waypoint {
	public int x;
	public int y;
	public int z;

	public Waypoint() {
	}

	public Waypoint(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public String toString() {
		return "WP: (" + x + ", " + y + ", " + z + ")";
	}

	public Location toLocation(Location loc) {
		loc.setX(x);
		loc.setY(y);
		loc.setZ(z);
		return loc;
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
