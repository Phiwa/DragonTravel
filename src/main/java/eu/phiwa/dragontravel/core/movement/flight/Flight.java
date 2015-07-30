package eu.phiwa.dragontravel.core.movement.flight;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("DT-Flight")
public class Flight implements ConfigurationSerializable {

    private String displayName;
    private String name;
    private List<WayPoint> wayPoints;

    public Flight(Map<String, Object> data) {
        displayName = (String) data.get("displayname");
        this.wayPoints = new LinkedList<>();
        @SuppressWarnings("unchecked")
        List<Object> wps = (List<Object>) data.get("waypoints");
        for (Object o : wps) {
            if (!(o instanceof String))
                continue;
            String wpData = (String) o;
            wayPoints.add(WayPoint.loadFromString(wpData));
        }
    }

    public Flight(String flightName, String displayName) {
        this.displayName = displayName;
        this.name = flightName.toLowerCase();
        this.wayPoints = new LinkedList<>();
    }

    public void addWayPoint(WayPoint wp) {
        wayPoints.add(wp);
    }

    public void removelastWayPoint() {
        wayPoints.get(wayPoints.size() - 1).removeMarker();
        wayPoints.remove(wayPoints.size() - 1);
    }

    public long getDistance() {
        long dist = 0;
        WayPoint lwp = null;
        for (WayPoint wp : wayPoints) {
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
        for (WayPoint wp : wayPoints) {
            wpStrings.add(wp.saveToString());
        }
        ret.put("waypoints", wpStrings);
        return ret;
    }

    public String toString() {

        StringBuilder sb = new StringBuilder(name);
        sb.append(":\n");

        for (WayPoint wp : wayPoints) {
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

    public List<WayPoint> getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(List<WayPoint> wayPoints) {
        this.wayPoints = wayPoints;
    }
}
