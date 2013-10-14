package eu.phiwa.dt;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Home {

	public String playername;
	
	// Single values
	public int x;
	public int y;
	public int z;	
	public World world;
	
	// Complete Location
	public Location loc;
	
	
	public Home(String playername, int x, int y, int z, String worldname) {
		this.playername = playername.toLowerCase();
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = Bukkit.getWorld(worldname);
	}
	
	public Home(String playername, int x, int y, int z, World world) {
		this.playername = playername.toLowerCase();
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}
	
	public Home(String playername, Location loc) {
		this.playername = playername.toLowerCase();
		this.loc = loc;
		this.x = (int) loc.getX();
		this.y = (int) loc.getY();
		this.z = (int) loc.getZ();
		this.world = loc.getWorld();
	}
	
	/**
	 * Prints the station's details to the player's chat.
	 * 
	 * @param player
	 * 			Player to send the details to.
	 */
	public void print(Player player) {
		player.sendMessage("--- Station ---");
		player.sendMessage("Owner: " + playername);
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
		System.out.println("--- Home ---");
		System.out.println("Owner: " + playername);
		System.out.println("X: " + x);
		System.out.println("Y: " + y);
		System.out.println("Z: " + z);
		System.out.println("World: " + world.getName());
		System.out.println("---------------");
	}
}
