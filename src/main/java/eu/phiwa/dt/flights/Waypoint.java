package eu.phiwa.dt.flights;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.objects.Flight;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Collection;

public class Waypoint {

	/**
	 * Removes all WaypointMarkers in the server
	 * 
	 */
	public static void removeWaypointMarkersGlobally() {

		Collection<Block> globalmarkers =  DragonTravelMain.globalwaypointmarkers.values();

		for (final Block marker : globalmarkers) {
			marker.getWorld().getBlockAt(marker.getX(), marker.getY(), marker.getZ()).getChunk().load(true);
			Bukkit.getScheduler().runTaskLater(DragonTravelMain.plugin, new Runnable() {
				@Override
				public void run() {
					marker.setType(Material.AIR);
				}
			}, 1L);
		}
	}
	/** Removes all WaypointMarkers of the specified flight
	 * 
	 * @param flight
	 * 			Flight whose waypoint-markers you want to remove
	 */
	public static void removeWaypointMarkersOfFlight(Flight flight) {
		for (Waypoint wp: flight.waypoints) {
			if(DragonTravelMain.globalwaypointmarkers.containsKey(wp.marker))
				DragonTravelMain.globalwaypointmarkers.remove(wp.marker);

			if(wp.marker == null)
				continue;
			wp.world.getBlockAt(wp.x, wp.y, wp.z).getChunk().load(true);	
			wp.marker.setType(Material.AIR);
		}
	}
	public boolean finalwp = false;
	public Block marker;
	public int x;
	public int y;
	public int z;
	
	public World world;

	public Location getAsLocation(){
		return new Location(world, x, y, z);
	}

	public void removeMarker() {
		DragonTravelMain.globalwaypointmarkers.remove(this.marker);
		this.marker.setType(Material.AIR);
	}

	public void setMarker(Player player) {
		marker = player.getLocation().getBlock();
		marker.setType(Material.SEA_LANTERN);
		DragonTravelMain.globalwaypointmarkers.put(this.marker, this.marker);
	}

	public String toString() {
		return "WP: (" + x + ", " + y + ", " + z + ", " +world.getName() + ", " + finalwp;		
	}
}
