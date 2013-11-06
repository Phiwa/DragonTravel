package eu.phiwa.dt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import eu.phiwa.dt.flights.FlightEditor;
import eu.phiwa.dt.flights.Waypoint;

@SerializableAs("DT-Flight")
public class Flight implements ConfigurationSerializable {
	public String name;
	public String displayname;
	public String worldName;
	public List<Waypoint> waypoints = new ArrayList<Waypoint>();

	public Flight(Map<String, Object> data) {
		displayname = (String) data.get("displayname");
		worldName = (String) data.get("displayname");
		name = displayname.toLowerCase();

		@SuppressWarnings("unchecked")
		List<Object> wps = (List<Object>) data.get("waypoints");
		for (Object o : wps) {
			if (!(o instanceof String))
				continue;
			String wpData = (String) o;
			waypoints.add(Waypoint.loadFromString(wpData));
		}
	}

	public Flight(World world, String flightname) {
		this.displayname = flightname;
		this.name = flightname.toLowerCase();
		this.worldName = world.getName();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(displayname);
		sb.append(":\n");

		for (Waypoint wp : waypoints) {
			sb.append("- " + wp.x + ", " + wp.y + ", " + wp.z + "\n");
		}

		return sb.toString();
	}

	public void addWaypoint(Waypoint wp) {
		waypoints.add(wp);
	}

	public void removelastWaypoint(Player player) {
		// Remove marker from waypoint
		Waypoint tmp = waypoints.get(waypoints.size() - 1);
		Block block = Bukkit.getWorld(worldName).getBlockAt(tmp.x, tmp.y, tmp.z);
		player.sendBlockChange(block.getLocation(), block.getType(), block.getData());

		waypoints.remove(waypoints.size() - 1);
	}

	public int getWaypointCount() {
		return waypoints.size();
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("displayname", displayname);
		ret.put("world", worldName);

		List<String> wpStrings = new ArrayList<String>();
		for (Waypoint wp : waypoints) {
			wpStrings.add(wp.saveToString());
		}
		ret.put("waypoints", wpStrings);

		return ret;
	}
}
