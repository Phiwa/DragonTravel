package eu.phiwa.dragontravel.core.flights;

import eu.phiwa.dragontravel.core.DragonTravelMain;
import eu.phiwa.dragontravel.core.objects.Flight;
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
    public static void removeWaypointMarkersGlobally() {

        Collection<Block> globalMarkers = DragonTravelMain.globalwaypointmarkers.values();

        for (final Block marker : globalMarkers) {
            marker.getWorld().getBlockAt(marker.getX(), marker.getY(), marker.getZ()).getChunk().load(true);
            Bukkit.getScheduler().runTaskLater(DragonTravelMain.getInstance(), () -> {
                marker.setType(Material.AIR);
            }, 1L);
        }
    }

    /**
     * Removes all WaypointMarkers of the specified flight
     *
     * @param flight Flight whose waypoint-markers you want to remove
     */
    public static void removeWaypointMarkersOfFlight(Flight flight) {
        for (Waypoint wp : flight.getWaypoints()) {
            if (DragonTravelMain.globalwaypointmarkers.containsKey(wp.marker))
                DragonTravelMain.globalwaypointmarkers.remove(wp.marker);

            if (wp.getMarker() == null)
                continue;
            Bukkit.getWorld(wp.getWorldName()).getBlockAt(wp.x, wp.y, wp.z).getChunk().load(true);
            wp.getMarker().setType(Material.AIR);
        }
    }

    public static Waypoint loadFromString(String wpData) {
        String[] wpDataParts = wpData.split("%");

        try {
            int x = Integer.parseInt(wpDataParts[0]);
            int y = Integer.parseInt(wpDataParts[1]);
            int z = Integer.parseInt(wpDataParts[2]);
            String worldName = wpDataParts[3];
            return new Waypoint(worldName, x, y, z);
        } catch (NumberFormatException ex) {
            Bukkit.getLogger().warning("Unable to read waypoint: " + wpData);
        } catch (IndexOutOfBoundsException ex) {
            Bukkit.getLogger().warning("Unable to read waypoint: " + wpData);
        }
        return null;
    }

    public Location getAsLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    public void removeMarker() {
        DragonTravelMain.globalwaypointmarkers.remove(this.marker);
        this.getMarker().setType(Material.AIR);
    }

    public void setMarker(Player player) {
        setMarker(player.getLocation().getBlock());
        getMarker().setType(Material.SEA_LANTERN);
        DragonTravelMain.globalwaypointmarkers.put(this.marker, this.marker);
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

    public Block getMarker() {
        return marker;
    }

    public void setMarker(Block marker) {
        this.marker = marker;
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

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }
}
