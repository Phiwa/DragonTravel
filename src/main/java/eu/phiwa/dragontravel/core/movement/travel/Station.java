package eu.phiwa.dragontravel.core.movement.travel;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;


@SerializableAs("DT-Station")
public class Station implements ConfigurationSerializable {

    private String displayName;
    private String owner;
    private String name;
    private String worldName;
    private int x;
    private int y;
    private int z;


    public Station(Map<String, Object> data) {
        this.x = (Integer) data.get("x");
        this.y = (Integer) data.get("y");
        this.z = (Integer) data.get("z");
        this.worldName = (String) data.get("world");
        this.displayName = (String) data.get("displayname");
        this.displayName = (String) data.get("displayname");
        if (data.containsKey("owner")) {
            this.owner = (String) data.get("owner");
        } else {
            this.owner = "admin";
        }
    }

    public Station(String name, String displayName, Location loc, String owner) {
        this.owner = owner;
        this.displayName = displayName;
        this.name = name.toLowerCase();
        this.x = (int) loc.getX();
        this.y = (int) loc.getY();
        this.z = (int) loc.getZ();
        this.worldName = loc.getWorld().getName();
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('\n');
        sb.append("--- Station ---").append('\n');
        sb.append("Name: " + displayName).append('\n');
        sb.append("Owner: " + owner).append('\n');
        sb.append("X: " + x).append('\n');
        sb.append("Y: " + y).append('\n');
        sb.append("Z: " + z).append('\n');
        sb.append("World: " + worldName).append('\n');
        sb.append("---------------").append('\n');
        return sb.toString();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("x", x);
        ret.put("y", y);
        ret.put("z", z);
        ret.put("world", worldName);
        ret.put("owner", owner);
        ret.put("displayname", displayName);
        return ret;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
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
