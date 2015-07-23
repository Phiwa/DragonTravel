package eu.phiwa.dt.filehandlers;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.objects.Station;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

public class StationsDB {

	DragonTravelMain plugin;
	
	public StationsDB (DragonTravelMain plugin) {
		this.plugin = plugin;
	}
		
	public boolean checkForStation(Player player) {		
		String pathToStation;
		int x,
			y,
			z;
		World world;
		Location tempLoc;
		Location playerLoc = player.getLocation();
		
		for(String string: DragonTravelMain.dbStationsConfig.getConfigurationSection("Stations").getKeys(true)) {
			if(string.contains(".displayname")) {
				pathToStation = "Stations." + string;
				pathToStation = pathToStation.replace(".displayname", "");
				
				String worldname = DragonTravelMain.dbStationsConfig.getString(pathToStation + ".world");
				
				if(worldname == null) {
					DragonTravelMain.logger.log(Level.SEVERE, "[DragonTravel] [Error] The world of the station "
										+ DragonTravelMain.dbStationsConfig.getString(pathToStation + ".displayname")
										+ " could not be read from the database, please check it for errors!");
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.DatabaseCorrupted"));
					return false;
				}
				
				world = Bukkit.getWorld(worldname);
				
				if(world == null) {
					DragonTravelMain.logger.log(Level.SEVERE, "[DragonTravel] Skipping station '"+DragonTravelMain.dbStationsConfig.getString(pathToStation + ".displayname")+"' while checking for a station. There is no world '"+ DragonTravelMain.dbStationsConfig.getString(pathToStation + ".world")+"' on the server!");
					continue;
				}
					
				
				if(!world.getName().equalsIgnoreCase(player.getWorld().getName()))
					continue;
				
				x = DragonTravelMain.dbStationsConfig.getInt(pathToStation + ".x");
				y = DragonTravelMain.dbStationsConfig.getInt(pathToStation + ".y");
				z = DragonTravelMain.dbStationsConfig.getInt(pathToStation + ".z");
					
				tempLoc = new Location(world, x, y, z);
				
				if(tempLoc.distance(playerLoc) <= DragonTravelMain.config.getInt("MountingLimit.Radius"))
					return true;			
			}
		}
		
		return false;
	}

	public Station getNearestStation(Player player) {
		Station station = null;
		String pathToStation;
		int x,
				y,
				z;
		World world;
		Location tempLoc;
		Location playerLoc = player.getLocation();

		for(String string: DragonTravelMain.dbStationsConfig.getConfigurationSection("Stations").getKeys(true)) {
			if(string.contains(".displayname")) {
				pathToStation = "Stations." + string;
				pathToStation = pathToStation.replace(".displayname", "");

				String worldname = DragonTravelMain.dbStationsConfig.getString(pathToStation + ".world");
                String name = DragonTravelMain.dbStationsConfig.getString("Stations."+string+".name");
                String displayname = DragonTravelMain.dbStationsConfig.getString("Stations." + string + ".displayname", name);
                String pid = DragonTravelMain.dbStationsConfig.getString("Stations." + string + ".owner", "admin");

				if(worldname == null) {
					DragonTravelMain.logger.log(Level.SEVERE, "[DragonTravel] [Error] The world of the station "
							+ DragonTravelMain.dbStationsConfig.getString(pathToStation + ".displayname")
							+ " could not be read from the database, please check it for errors!");
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.DatabaseCorrupted"));
					return null;
				}

				world = Bukkit.getWorld(worldname);

				if(world == null) {
					DragonTravelMain.logger.log(Level.SEVERE, "[DragonTravel] Skipping station '"+DragonTravelMain.dbStationsConfig.getString(pathToStation + ".displayname")+"' while checking for a station. There is no world '"+ DragonTravelMain.dbStationsConfig.getString(pathToStation + ".world")+"' on the server!");
					continue;
				}


				if(!world.getName().equalsIgnoreCase(player.getWorld().getName()))
					continue;

				x = DragonTravelMain.dbStationsConfig.getInt(pathToStation + ".x");
				y = DragonTravelMain.dbStationsConfig.getInt(pathToStation + ".y");
				z = DragonTravelMain.dbStationsConfig.getInt(pathToStation + ".z");



				if(station==null){
					station = new Station(name, displayname, x, y, z, worldname, pid);
				}

                tempLoc = new Location(world, x, y, z);
				if(station.loc.distance(playerLoc) > tempLoc.distance(playerLoc))
					station = new Station(name, displayname, x, y, z, worldname, pid);
			}
		}

		return null;
	}

	private void copy(InputStream in, File file) {
		
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) != -1)
				out.write(buf, 0, len);
			out.close();
			in.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void create() {
		
		if (DragonTravelMain.dbStationsFile.exists())
			return;

		try {
			DragonTravelMain.dbStationsFile.createNewFile();
			copy(this.plugin.getResource("databases/stations.yml"), DragonTravelMain.dbStationsFile);
			DragonTravelMain.logger.info("[DragonTravel] Created stations-database.");
		}
		catch(Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not create the stations-database!");
			e.printStackTrace();
		}
		
		
	}

	/**
	 * Creates a new station.
	 * 
	 * @param station
	 * 			Station to create.
	 * @return
	 * 			Returns true if the station was created successfully, false if not.		
	 */
	@SuppressWarnings("static-access")
	public boolean createStation(Station station)  {
		
		String path = "Stations." + station.name;
		
		ConfigurationSection sec = DragonTravelMain.dbStationsConfig.createSection(path);
		DragonTravelMain.dbStationsConfig.createPath(sec, "displayname");
		DragonTravelMain.dbStationsConfig.createPath(sec, "owner");
		DragonTravelMain.dbStationsConfig.createPath(sec, "x");
		DragonTravelMain.dbStationsConfig.createPath(sec, "y");
		DragonTravelMain.dbStationsConfig.createPath(sec, "z");
		DragonTravelMain.dbStationsConfig.createPath(sec, "world");
		DragonTravelMain.dbStationsConfig.set(path + ".displayname", station.displayname);
		DragonTravelMain.dbStationsConfig.set(path+".owner", station.owner);
		DragonTravelMain.dbStationsConfig.set(path+".x", station.x);
		DragonTravelMain.dbStationsConfig.set(path+".y", station.y);
		DragonTravelMain.dbStationsConfig.set(path+".z", station.z);
		DragonTravelMain.dbStationsConfig.set(path+".world", station.world.getName());
		
		try{
			DragonTravelMain.dbStationsConfig.save(DragonTravelMain.dbStationsFile);
			return true;
		}
		catch(Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not write new station to config.");
			return false;
		}
	}

	
	/**
	 * Deletes the given station.
	 * 
	 * @param stationname
	 * 			Name of the station to delete
	 * @return
	 * 			True if successful, false if not.
	 */
	public boolean deleteStation(String stationname) {	
		
		String stationpath = "Stations." + stationname.toLowerCase();	
		DragonTravelMain.dbStationsConfig.set(stationpath, null);
		
		try{
			DragonTravelMain.dbStationsConfig.save(DragonTravelMain.dbStationsFile);
			return true;
		}
		catch(Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not delete station from config.");
			return false;
		}
	}
	
	/**
	 * Returns the details of the station with the given name.
	 * 
	 * @param stationname
	 * 			Name of the station which should be returned.
	 * @return
	 * 			The station as a station-object.
	 */
	public Station getStation(String stationname) {
		
		String stationpath = "Stations." + stationname.toLowerCase();
		
		if(DragonTravelMain.dbStationsConfig.getString(stationpath+".world") == null)
			return null;
		
		Location stationLoc = new Location(
									Bukkit.getWorld(DragonTravelMain.dbStationsConfig.getString(stationpath+".world")),
											(double)DragonTravelMain.dbStationsConfig.getInt(stationpath+".x"),
											(double)DragonTravelMain.dbStationsConfig.getInt(stationpath+".y"),
											(double)DragonTravelMain.dbStationsConfig.getInt(stationpath+".z")						
									);
		String displayname = DragonTravelMain.dbStationsConfig.getString(stationpath + ".displayname", "Dragon Station");
		String owner = DragonTravelMain.dbStationsConfig.getString(stationpath + ".owner", "admin");
		Station station = new Station(stationname, displayname, stationLoc, owner);
		return station;
	}
	
	public void init() {
	
		DragonTravelMain.dbStationsFile = new File("plugins/DragonTravel/databases", "stations.yml");
	
		try {
			create();
		}
		catch (Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not initialize the stations-database.");
			e.printStackTrace();
		}
	
		DragonTravelMain.dbStationsConfig = new YamlConfiguration();
		load();
	
	}

	private void load() {
		try {
			DragonTravelMain.dbStationsConfig.load(DragonTravelMain.dbStationsFile);
			DragonTravelMain.logger.info("[DragonTravel] Loaded stations-database.");
		}
		catch (Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] No stations-database found");
			e.printStackTrace();
		}
	}
	
	public void showStations() {
		System.out.println("Available stations: ");
		for(String string: DragonTravelMain.dbStationsConfig.getConfigurationSection("Stations").getKeys(true)) {
			if(string.contains(".displayname")) {
				System.out.println("- " + DragonTravelMain.dbStationsConfig.getString("Stations." + string));
			}
		}
	}

	public void showStations(Player player) {
		player.sendMessage("Available stations: ");
		for(String string: DragonTravelMain.dbStationsConfig.getConfigurationSection("Stations").getKeys(true)) {
			if(string.contains(".displayname")) {			
				String stationname = string.replace(".displayname", "");
				if(player.hasPermission("dt.travel.*") || player.hasPermission("dt.travel."+stationname))				
					player.sendMessage("- " + DragonTravelMain.dbStationsConfig.getString("Stations." + string));
			}
		}			
	}
	
}
