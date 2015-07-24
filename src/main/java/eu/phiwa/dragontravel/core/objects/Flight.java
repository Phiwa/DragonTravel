package eu.phiwa.dragontravel.core.objects;

import eu.phiwa.dragontravel.core.flights.Waypoint;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

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
		
		waypoints.remove(waypoints.size() - 1);
		wpcount--;
	}

	public long getDistance(){
		long dist = 0;
		Waypoint lwp = null;
		for(Waypoint wp : waypoints){
			if(wp==null){
				lwp = wp;
				continue;
			}
			dist += Math.hypot(wp.getAsLocation().getBlockX() - lwp.getAsLocation().getBlockX(),wp.getAsLocation().getBlockZ()-lwp.getAsLocation().getBlockZ());
		}
		return dist;
	}
	
	public String toString() {

		String flightString = displayname + ":\n";
		
		for(Waypoint wp: waypoints) {
			flightString += "- " + wp.x + ", " + wp.y + ", " + wp.z + ", " + wp.world.getName() + "\n";
		}
		
		return flightString;		
	}

}
