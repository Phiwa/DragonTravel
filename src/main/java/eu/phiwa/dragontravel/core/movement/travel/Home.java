package eu.phiwa.dragontravel.core.movement.travel;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("DT-Home")
public class Home implements ConfigurationSerializable {

    public String playerName;
    public final String worldName;
    public final int x;
    public final int y;
    public final int z;


    public Home(Map<String, Object> data) {
        this.x = (Integer) data.get("x");
        this.y = (Integer) data.get("y");
        this.z = (Integer) data.get("z");
        this.worldName = (String) data.get("world");
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
        return "\n" + "--- Home ---" + '\n' + "X: " + x + '\n' + "Y: " + y + '\n' + "Z: " + z + '\n' + "World: " + worldName + '\n' + "---------------" + '\n';
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
