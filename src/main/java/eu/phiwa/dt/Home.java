package eu.phiwa.dt;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("DT-Home")
public class Home implements ConfigurationSerializable {
	public int x;
	public int y;
	public int z;
	public String worldName;

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
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("x", x);
		ret.put("y", y);
		ret.put("z", z);
		ret.put("world", worldName);
		return ret;
	}
}
