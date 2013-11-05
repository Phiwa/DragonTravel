package eu.phiwa.dt;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;

import eu.phiwa.dt.flights.Waypoint;

public class Flight {

	public String name;
	public String displayname;
	public String worldName;
	public List<Waypoint> waypoints = new ArrayList<Waypoint>();

	public Flight() {

	}

	public Flight(World world, String flightname) {
		this.displayname = flightname;
		this.name = flightname.toLowerCase();
		this.worldName = world.getName();
	}

	public String toString() {

		String flightString = displayname + ":\n";

		for (Waypoint wp : waypoints) {
			flightString += "- " + wp.x + ", " + wp.y + ", " + wp.z + "\n";
		}

		return flightString;
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
}
