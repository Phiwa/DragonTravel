package eu.phiwa.dt;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Station {
	public String name;
	public String displayname;

	// Single values
	public int x;
	public int y;
	public int z;
	public World world;

	public Station(String name, int x, int y, int z, String worldname) {
		this.displayname = name;
		this.name = name.toLowerCase();
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = Bukkit.getWorld(worldname);
	}

	public Station(String name, int x, int y, int z, World world) {
		this.displayname = name;
		this.name = name.toLowerCase();
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}

	public Station(String name, Location loc) {
		this.displayname = name;
		this.name = name.toLowerCase();
		this.x = (int) loc.getX();
		this.y = (int) loc.getY();
		this.z = (int) loc.getZ();
		this.world = loc.getWorld();
	}

	/**
	 * Prints the station's details to the player's chat.
	 *
	 * @param player Player to send the details to.
	 */
	public void print(Player player) {
		player.sendMessage("--- Station ---");
		player.sendMessage("Name: " + displayname);
		player.sendMessage("X: " + x);
		player.sendMessage("Y: " + y);
		player.sendMessage("Z: " + z);
		player.sendMessage("World: " + world.getName());
		player.sendMessage("---------------");
	}

	/**
	 * Prints the station's details to the console.
	 */
	public void print() {
		System.out.println("--- Station ---");
		System.out.println("Name: " + displayname);
		System.out.println("X: " + x);
		System.out.println("Y: " + y);
		System.out.println("Z: " + z);
		System.out.println("World: " + world.getName());
		System.out.println("---------------");
	}
}
