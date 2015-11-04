package eu.phiwa.dragontravel.core.movement.flight;

import eu.phiwa.dragontravel.core.DragonTravel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Collection;

public class Waypoint {

    private boolean finalWP = false;
    private Block marker;
    private int x;
    private int y;
    private int z;
    private String worldName;

    public Waypoint() {
    }

    public Waypoint(String worldName, int x, int y, int z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Removes all WaypointMarkers in the server
     */
    @SuppressWarnings("unused")
    public static void removeWayPointMarkersGlobally() {
        Collection<Block> globalMarkers = DragonTravel.getInstance().getFlightEditor().getWayPointMarkers().values();

        for (final Block marker : globalMarkers) {
            marker.getWorld().getBlockAt(marker.getX(), marker.getY(), marker.getZ()).getChunk().load(true);
            Bukkit.getScheduler().runTaskLater(DragonTravel.getInstance(), new Runnable() {
                @Override
                public void run() {
                    marker.setType(Material.AIR);
                }
            }, 1L);
        }
    }

    /**
     * Removes all WaypointMarkers of the specified flight
     *
     * @param flight Flight whose waypoint-markers you want to remove
     */
    public static void removeWayPointMarkersOfFlight(Flight flight) {
        for (Waypoint wp : flight.getWaypoints()) {
            if (DragonTravel.getInstance().getFlightEditor().getWayPointMarkers().containsKey(wp.marker))
                DragonTravel.getInstance().getFlightEditor().getWayPointMarkers().remove(wp.marker);

            if (wp.getMarker() == null)
                continue;
            Bukkit.getWorld(wp.getWorldName()).getBlockAt(wp.x, wp.y, wp.z).getChunk().load(true);
            wp.getMarker().setType(Material.AIR);
        }
    }

    private Block getMarker() {
        return marker;
    }

    private void setMarker(Block marker) {
        this.marker = marker;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public static Waypoint loadFromString(String wpData) {
        String[] wpDataParts = wpData.split("%");

        try {
            int x = Integer.parseInt(wpDataParts[0]);
            int y = Integer.parseInt(wpDataParts[1]);
            int z = Integer.parseInt(wpDataParts[2]);
            String worldName = wpDataParts[3];
            return new Waypoint(worldName, x, y, z);
        } catch (NumberFormatException | IndexOutOfBoundsException ex) {
            Bukkit.getLogger().warning("Unable to read waypoint: " + wpData);
        }
        return null;
    }

    public Location getAsLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    public void removeMarker() {
        DragonTravel.getInstance().getFlightEditor().getWayPointMarkers().remove(this.marker);
        this.getMarker().setType(Material.AIR);
    }

    public void setMarker(Player player) {
        setMarker(player.getLocation().getBlock());
        try {
        	getMarker().setType(Material.SEA_LANTERN);
        }
        catch(NoSuchFieldError ex) {
        	getMarker().setType(Material.GLOWSTONE); // Fallback option for servers older than MC 1.8
        }
        DragonTravel.getInstance().getFlightEditor().getWayPointMarkers().put(this.marker, this.marker);
    }

    public String saveToString() {
        return x + "%" + y + "%" + z + "%" + worldName;
    }

    public String toString() {
        return "WP: (" + x + ", " + y + ", " + z + ", " + worldName + ", " + finalWP;
    }

    public boolean isFinalWP() {
        return finalWP;
    }

    public void setFinalWP(boolean finalWP) {
        this.finalWP = finalWP;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
