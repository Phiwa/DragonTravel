package eu.phiwa.dt.filehandlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.objects.Home;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class HomesDB {

	DragonTravelMain plugin;
	
	public HomesDB (DragonTravelMain plugin) {
		this.plugin = plugin;
	}
		
	private void copy(InputStream in, File file) {
		
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void create() {
		
		if (DragonTravelMain.dbHomesFile.exists())
			return;

		try {
			DragonTravelMain.dbHomesFile.createNewFile();
			copy(this.plugin.getResource("databases/homes.yml"), DragonTravelMain.dbHomesFile);
			DragonTravelMain.logger.info("[DragonTravel] Created homes-database.");
		}
		catch(Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not create the homes-database!");
		}
		
		
	}
	
	/**
	 * Creates a new home.
	 * 
	 * @param home
	 * 			Home to create.
	 * @return
	 * 			Returns true if the home was created successfully, false if not.		
	 */
	@SuppressWarnings("static-access")
	public boolean createHome(Home home)  {
		
		String path = "Homes." + home.playername;
		
		ConfigurationSection sec = DragonTravelMain.dbHomesConfig.createSection(path);
		DragonTravelMain.dbHomesConfig.createPath(sec, "x");
		DragonTravelMain.dbHomesConfig.createPath(sec, "y");
		DragonTravelMain.dbHomesConfig.createPath(sec, "z");
		DragonTravelMain.dbHomesConfig.createPath(sec, "world");
		DragonTravelMain.dbHomesConfig.set(path+".x", home.x);
		DragonTravelMain.dbHomesConfig.set(path+".y", home.y);
		DragonTravelMain.dbHomesConfig.set(path+".z", home.z);
		DragonTravelMain.dbHomesConfig.set(path+".world", home.world.getName());
		
		try{
			DragonTravelMain.dbHomesConfig.save(DragonTravelMain.dbHomesFile);
			return true;
		}
		catch(Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not write new home to config.");
			return false;
		}
	}

	/**
	 * Deletes the given home.
	 * 
	 * @param homename
	 * 			Name of the home to delete
	 * @return
	 * 			True if successful, false if not.
	 */
	public boolean deleteHome(String playername) {	
		
		playername = "Homes." + playername;
		
		DragonTravelMain.dbHomesConfig.set(playername, null);
		
		try{
			DragonTravelMain.dbHomesConfig.save(DragonTravelMain.dbHomesFile);
			return true;
		}
		catch(Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not delete home from config.");
			return false;
		}
	}

	
	/**
	 * Returns the details of the home with the given name.
	 * 
	 * @param homename
	 * 			Name of the home which should be returned.
	 * @return
	 * 			The home as a home-object.
	 */
	public Home getHome(String playername) {
		
		playername = "Homes." + playername.toLowerCase();
		
		if(DragonTravelMain.dbHomesConfig.getString(playername+".world") == null)
			return null;

		Location homeLoc = new Location(
									Bukkit.getWorld(DragonTravelMain.dbHomesConfig.getString(playername+".world")),
											(double)DragonTravelMain.dbHomesConfig.getInt(playername+".x"),
											(double)DragonTravelMain.dbHomesConfig.getInt(playername+".y"),
											(double)DragonTravelMain.dbHomesConfig.getInt(playername+".z")						
									);
		
		Home home = new Home(playername, homeLoc);
		return home;

	}
	
	public void init() {
	
		DragonTravelMain.dbHomesFile = new File("plugins/DragonTravel/databases", "homes.yml");
	
		try {
			create();
		}
		catch (Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] Could not initialize the homes-database.");
			e.printStackTrace();
		}
	
		DragonTravelMain.dbHomesConfig = new YamlConfiguration();
		load();
	
	}
	
	private void load() {
		try {
			DragonTravelMain.dbHomesConfig.load(DragonTravelMain.dbHomesFile);
			DragonTravelMain.logger.info("[DragonTravel] Loaded homes-database.");
		}
		catch (Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] No homes-database found");
			e.printStackTrace();
		}
	}
	
	public void showHomes() {
		System.out.println("Player's who registered a home: ");
		for(String string: DragonTravelMain.dbHomesConfig.getConfigurationSection("Homes").getKeys(true)) {
			if(!string.contains("."))
				System.out.println("- " + string);
		}
	}
	
	public void showStations(Player player) {
		player.sendMessage("Player's who registered a home: ");
		for(String string: DragonTravelMain.dbHomesConfig.getConfigurationSection("Homes").getKeys(true)) {
			if(!string.contains("."))
				player.sendMessage("- " + string);
		}			
	}
	
}
