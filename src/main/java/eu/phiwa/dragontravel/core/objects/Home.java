package eu.phiwa.dragontravel.core.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("DT-Home")
public class Home implements ConfigurationSerializable {

    public String playerName;
    public String worldName;
    public int x;
    public int y;
    public int z;


    public Home(Map<String, Object> data) {
        this.x = (Integer) data.get("x");
        this.y = (Integer) data.get("y");
        this.z = (Integer) data.get("z");
        this.worldName = (String) data.get("world");
    }

    public Home(String playerName, int x, int y, int z, World world) {
        this.playerName = playerName.toLowerCase();
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
    }

    public Home(Location loc) {
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
        sb.append("--- Home ---").append('\n');
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
        return ret;
    }
}
