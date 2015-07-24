package eu.phiwa.dragontravel.core.filehandlers;

import eu.phiwa.dragontravel.core.DragonTravelMain;
import eu.phiwa.dragontravel.core.objects.Station;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

public class StationsDB {

	private FileConfiguration dbStationsConfig;
	private File dbStationsFile;
	
	public StationsDB () {
		init();
	}
		
	public boolean checkForStation(Player player) {		
		String pathToStation;
		int x,
			y,
			z;
		World world;
		Location tempLoc;
		Location playerLoc = player.getLocation();
		
		for(String string: dbStationsConfig.getConfigurationSection("Stations").getKeys(true)) {
            if (string.contains(".displayname")) {
                pathToStation = "Stations." + string;
                pathToStation = pathToStation.replace(".displayname", "");

                String worldname = dbStationsConfig.getString(pathToStation + ".world");

                if (worldname == null) {
                    Bukkit.getLogger().log(Level.SEVERE, "The world of the station "
                            + dbStationsConfig.getString(pathToStation + ".displayname")
                            + " could not be read from the database, please check it for errors!");
                    player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.General.Error.DatabaseCorrupted"));
                    return false;
                }

                world = Bukkit.getWorld(worldname);

                if (world == null) {
                    Bukkit.getLogger().log(Level.SEVERE, "Skipping station '" + dbStationsConfig.getString(pathToStation + ".displayname") + "' while checking for a station. There is no world '" + dbStationsConfig.getString(pathToStation + ".world") + "' on the server!");
                    continue;
                }


                if (!world.getName().equalsIgnoreCase(player.getWorld().getName()))
                    continue;

                x = dbStationsConfig.getInt(pathToStation + ".x");
                y = dbStationsConfig.getInt(pathToStation + ".y");
                z = dbStationsConfig.getInt(pathToStation + ".z");

                tempLoc = new Location(world, x, y, z);

                if (tempLoc.distance(playerLoc) <= DragonTravelMain.getInstance().getConfigHandler().getMountingLimitRadius())
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

		for(String string: dbStationsConfig.getConfigurationSection("Stations").getKeys(true)) {
			if(string.contains(".displayname")) {
				pathToStation = "Stations." + string;
				pathToStation = pathToStation.replace(".displayname", "");

				String worldname = dbStationsConfig.getString(pathToStation + ".world");
                String name = dbStationsConfig.getString("Stations."+string+".name");
                String displayname = dbStationsConfig.getString("Stations." + string + ".displayname", name);
                String pid = dbStationsConfig.getString("Stations." + string + ".owner", "admin");

				if(worldname == null) {
                    Bukkit.getLogger().log(Level.SEVERE, "The world of the station "
                            + dbStationsConfig.getString(pathToStation + ".displayname")
                            + " could not be read from the database, please check it for errors!");
					player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.General.Error.DatabaseCorrupted"));
					return null;
				}

				world = Bukkit.getWorld(worldname);

				if(world == null) {
                    Bukkit.getLogger().log(Level.SEVERE, "Skipping station '" + dbStationsConfig.getString(pathToStation + ".displayname") + "' while checking for a station. There is no world '" + dbStationsConfig.getString(pathToStation + ".world") + "' on the server!");
					continue;
				}


				if(!world.getName().equalsIgnoreCase(player.getWorld().getName()))
					continue;

				x = dbStationsConfig.getInt(pathToStation + ".x");
				y = dbStationsConfig.getInt(pathToStation + ".y");
				z = dbStationsConfig.getInt(pathToStation + ".z");



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
		
		if (dbStationsFile.exists())
			return;

		try {
			dbStationsFile.createNewFile();
			copy(DragonTravelMain.getInstance().getResource("databases/stations.yml"), dbStationsFile);
            Bukkit.getLogger().log(Level.INFO, "Created stations-database.");
		}
		catch(Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not create the stations-database!");
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
		
		ConfigurationSection sec = dbStationsConfig.createSection(path);
		dbStationsConfig.createPath(sec, "displayname");
		dbStationsConfig.createPath(sec, "owner");
		dbStationsConfig.createPath(sec, "x");
		dbStationsConfig.createPath(sec, "y");
		dbStationsConfig.createPath(sec, "z");
		dbStationsConfig.createPath(sec, "world");
		dbStationsConfig.set(path + ".displayname", station.displayname);
		dbStationsConfig.set(path+".owner", station.owner);
		dbStationsConfig.set(path+".x", station.x);
		dbStationsConfig.set(path+".y", station.y);
		dbStationsConfig.set(path+".z", station.z);
		dbStationsConfig.set(path+".world", station.world.getName());
		
		try{
			dbStationsConfig.save(dbStationsFile);
			return true;
		}
		catch(Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not write new station to config.");
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
		dbStationsConfig.set(stationpath, null);
		
		try{
			dbStationsConfig.save(dbStationsFile);
			return true;
		}
		catch(Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not delete station from config.");
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
		
		if(dbStationsConfig.getString(stationpath+".world") == null)
			return null;
		
		Location stationLoc = new Location(
									Bukkit.getWorld(dbStationsConfig.getString(stationpath + ".world")),
											(double)dbStationsConfig.getInt(stationpath+".x"),
											(double)dbStationsConfig.getInt(stationpath+".y"),
											(double)dbStationsConfig.getInt(stationpath+".z")
									);
		String displayname = dbStationsConfig.getString(stationpath + ".displayname", "Dragon Station");
		String owner = dbStationsConfig.getString(stationpath + ".owner", "admin");
		return new Station(stationname, displayname, stationLoc, owner);
	}
	
	public void init() {
	
		dbStationsFile = new File("plugins/DragonTravel/databases", "stations.yml");
	
		try {
			create();
		}
		catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not initialize the stations-database.");
			e.printStackTrace();
		}
	
		dbStationsConfig = new YamlConfiguration();
		load();
	
	}

	private void load() {
		try {
			dbStationsConfig.load(dbStationsFile);
            Bukkit.getLogger().log(Level.INFO, "Loaded stations-database.");
		}
		catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "No stations-database found");
			e.printStackTrace();
		}
	}
	
	public void showStations() {
		System.out.println("Available stations: ");
        dbStationsConfig.getConfigurationSection("Stations").getKeys(true).stream().filter(string -> string.contains(".displayname")).forEach(string -> {
            System.out.println("- " + dbStationsConfig.getString("Stations." + string));
        });
	}

	public void showStations(Player player) {
		player.sendMessage("Available stations: ");
        dbStationsConfig.getConfigurationSection("Stations").getKeys(true).stream().filter(string -> string.contains(".displayname")).forEach(string -> {
            String stationname = string.replace(".displayname", "");
            if (player.hasPermission("dt.travel.*") || player.hasPermission("dt.travel." + stationname))
                player.sendMessage("- " + dbStationsConfig.getString("Stations." + string));
        });
	}
	
}
