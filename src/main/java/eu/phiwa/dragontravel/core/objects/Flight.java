package eu.phiwa.dragontravel.core.objects;

import eu.phiwa.dragontravel.core.flights.Waypoint;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("DT-Flight")
public class Flight implements ConfigurationSerializable {

    private String displayName;
    private String name;
    private List<Waypoint> waypoints;

    public Flight(Map<String, Object> data) {
        displayName = (String) data.get("displayname");
        this.waypoints = new LinkedList<>();
        @SuppressWarnings("unchecked")
        List<Object> wps = (List<Object>) data.get("waypoints");
        for (Object o : wps) {
            if (!(o instanceof String))
                continue;
            String wpData = (String) o;
            waypoints.add(Waypoint.loadFromString(wpData));
        }
    }

    public Flight(String flightName, String displayName) {
        this.displayName = displayName;
        this.name = flightName.toLowerCase();
        this.waypoints = new LinkedList<>();
    }

    public void addWaypoint(Waypoint wp) {
        waypoints.add(wp);
    }

    public void removelastWaypoint() {
        waypoints.get(waypoints.size() - 1).removeMarker();
        waypoints.remove(waypoints.size() - 1);
    }

    public long getDistance() {
        long dist = 0;
        Waypoint lwp = null;
        for (Waypoint wp : waypoints) {
            if (lwp == null) {
                lwp = wp;
                continue;
            }

            dist += Math.hypot(wp.getAsLocation().getBlockX() - lwp.getAsLocation().getBlockX(), wp.getAsLocation().getBlockZ() - lwp.getAsLocation().getBlockZ());
        }
        return dist;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("displayname", displayName);
        List<String> wpStrings = new ArrayList<>();
        for (Waypoint wp : waypoints) {
            wpStrings.add(wp.saveToString());
        }
        ret.put("waypoints", wpStrings);
        return ret;
    }

    public String toString() {

        StringBuilder sb = new StringBuilder(name);
        sb.append(":\n");

        for (Waypoint wp : waypoints) {
            sb.append("- " + wp.getX() + ", " + wp.getY() + ", " + wp.getZ() + ", " + wp.getWorldName() + "\n");
        }

        return sb.toString();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }
}
