package eu.phiwa.dt.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;

import eu.phiwa.dt.flights.Waypoint;

public class Flight {

	public String displayname;
	public String name;
	public List<Waypoint> waypoints = new ArrayList<Waypoint>();
	public int wpcount = 0;
	
	public Flight() {
		
	}
	
	public Flight(World world, String flightname) {		
		this.displayname = flightname;
		this.name = flightname.toLowerCase();
	}

	public void addWaypoint(Waypoint wp) {
		waypoints.add(wp);
		wpcount++;
	}
	
	public void removelastWaypoint() {
		
		// Remove marker from waypoint
		waypoints.get(waypoints.size()-1).removeMarker();
		
		waypoints.remove(waypoints.size()-1);
		wpcount--;
	}
	
	public String toString() {

		String flightString = displayname + ":\n";
		
		for(Waypoint wp: waypoints) {
			flightString += "- " + wp.x + ", " + wp.y + ", " + wp.z + ", " + wp.world.getName() + "\n";
		}
		
		return flightString;		
	}

}
