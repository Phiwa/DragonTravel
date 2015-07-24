package eu.phiwa.dragontravel.core.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;


public class Station {

	public String displayname;
	// Complete Location
	public Location loc;
	public String owner;
	public String name;
	public World world;
	// Single values
	public int x;	
	public int y;
	
	public int z;
	
	
/*	
	public Station(String name) {
		this.name = name;
	}
*/	
	public Station(String name, String displayname, int x, int y, int z, String worldname, String owner) {
		this.displayname = displayname;
		this.name = name.toLowerCase();
		this.owner = owner;
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = Bukkit.getWorld(worldname);
	}
	
	public Station(String name, String displayname, int x, int y, int z, World world, String owner) {
		this.owner = owner;
		this.displayname = displayname;
		this.name = name.toLowerCase();
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}
	
	public Station(String name, String displayname, Location loc, String owner) {
		this.owner = owner;
		this.displayname = displayname;
		this.name = name.toLowerCase();
		this.loc = loc;
		this.x = (int) loc.getX();
		this.y = (int) loc.getY();
		this.z = (int) loc.getZ();
		this.world = loc.getWorld();
	}
	
	/**
	 * Prints the station's details to the console.
	 */
	public void print() {
		System.out.println("--- Station ---");
		System.out.println("Name: " + displayname);
		if(Bukkit.getOfflinePlayer(UUID.fromString(owner)).hasPlayedBefore() && !owner.equals("admin"))
			System.out.println("Owner: " + Bukkit.getOfflinePlayer(UUID.fromString(owner)).getName());
		System.out.println("X: " + x);
		System.out.println("Y: " + y);
		System.out.println("Z: " + z);
		System.out.println("World: " + world.getName());
		System.out.println("---------------");
	}

	/**
	 * Prints the station's details to the player's chat.
	 * 
	 * @param player
	 * 			Player to send the details to.
	 */
	public void print(Player player) {
		player.sendMessage("--- Station ---");
		player.sendMessage("Name: " + displayname);
		if(Bukkit.getOfflinePlayer(UUID.fromString(owner)).hasPlayedBefore() && !owner.equals("admin"))
			player.sendMessage("Owner: " + Bukkit.getOfflinePlayer(UUID.fromString(owner)).getName());
		player.sendMessage("X: " + x);
		player.sendMessage("Y: " + y);
		player.sendMessage("Z: " + z);
		player.sendMessage("World: " + world.getName());
		player.sendMessage("---------------");
	}
}
