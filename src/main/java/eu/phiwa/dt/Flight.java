package eu.phiwa.dt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

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
		List<Object> waypoints = (List<Object>) data.get("waypoints");
		for (Object o : waypoints) {
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

	public void removelastWaypoint() {
		// Remove marker from waypoint
		waypoints.get(waypoints.size() - 1).removeMarker();

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
