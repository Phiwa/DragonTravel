package eu.phiwa.dt.filehandlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.Station;

public class StationsDB {

	DragonTravelMain plugin;
	private ConfigurationSection stationSection;

	public StationsDB(DragonTravelMain plugin) {
		this.plugin = plugin;
	}

	public void init() {

		DragonTravelMain.dbStationsFile = new File("plugins/DragonTravel/databases", "stations.yml");

		try {
			create();
		} catch (Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not initialize the stations-database.");
			e.printStackTrace();
		}

		DragonTravelMain.dbStationsConfig = new YamlConfiguration();
		load();

		stationSection = DragonTravelMain.dbStationsConfig.getConfigurationSection("Stations");
		if (stationSection == null) {
			stationSection = DragonTravelMain.dbStationsConfig.createSection("Stations");
		}
	}

	private void create() {

		if (DragonTravelMain.dbStationsFile.exists())
			return;

		try {
			DragonTravelMain.dbStationsFile.createNewFile();
			copy(getClass().getResourceAsStream("stations.yml"), DragonTravelMain.dbStationsFile);
			DragonTravelMain.logger.info("[DragonTravel] Created stations-database.");
		} catch (Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not create the stations-database!");
			e.printStackTrace();
		}


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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void load() {
		try {
			DragonTravelMain.dbStationsConfig.load(DragonTravelMain.dbStationsFile);
			DragonTravelMain.logger.info("[DragonTravel] Loaded stations-database.");
		} catch (Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] No stations-database found");
			e.printStackTrace();
		}
	}


	/**
	 * Returns the details of the station with the given name.
	 *
	 * @param stationname Name of the station which should be returned.
	 * @return The station as a station-object.
	 */
	public Station getStation(String stationname) {
		return (Station) stationSection.get(stationname.toLowerCase(), null);
	}

	/**
	 * Creates a new station.
	 *
	 * @param station Station to create.
	 * @return Returns true if the station was created successfully, false if
	 *         not.
	 */
	public boolean saveStation(Station station) {
		stationSection.set(station.name, station);

		try {
			DragonTravelMain.dbStationsConfig.save(DragonTravelMain.dbStationsFile);
			return true;
		} catch (Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not write new station to config.");
			return false;
		}
	}

	/**
	 * Deletes the given station.
	 *
	 * @param stationname Name of the station to delete
	 * @return True if successful, false if not.
	 */
	public boolean deleteStation(String stationname) {
		stationSection.set(stationname.toLowerCase(), null);

		try {
			DragonTravelMain.dbStationsConfig.save(DragonTravelMain.dbStationsFile);
			return true;
		} catch (Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not delete station from config.");
			return false;
		}
	}

	public void showStations() {
		System.out.println("Available stations: ");
		for (String string : DragonTravelMain.dbStationsConfig.getConfigurationSection("Stations").getKeys(true)) {
			if (string.contains(".displayname")) {
				System.out.println("- " + DragonTravelMain.dbStationsConfig.getString("Stations." + string));
			}
		}
	}

	public void showStations(Player player) {
		player.sendMessage("Available stations: ");
		for (String string : DragonTravelMain.dbStationsConfig.getConfigurationSection("Stations").getKeys(true)) {
			if (string.contains(".displayname")) {
				player.sendMessage("- " + DragonTravelMain.dbStationsConfig.getString("Stations." + string));
			}
		}
	}

	public boolean checkForStation(Player player) {
		String pathToStation;
		int x, y, z;
		World world;
		Location tempLoc;
		Location playerLoc = player.getLocation();

		for (String string : DragonTravelMain.dbStationsConfig.getConfigurationSection("Stations").getKeys(true)) {
			if (string.contains(".displayname")) {
				pathToStation = "Stations." + string;
				pathToStation = pathToStation.replace(".displayname", "");

				String worldname = DragonTravelMain.dbStationsConfig.getString(pathToStation + ".world");

				if (worldname == null) {
					DragonTravelMain.logger.log(Level.SEVERE, "[DragonTravel] [Error] The world of the station " + DragonTravelMain.dbStationsConfig.getString(pathToStation + ".displayname") + " could not be read from the database, please check it for errors!");
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.DatabaseCorrupted"));
					return false;
				}

				world = Bukkit.getWorld(worldname);

				if (world == null) {
					DragonTravelMain.logger.log(Level.SEVERE, "[DragonTravel] Skipping station '" + DragonTravelMain.dbStationsConfig.getString(pathToStation + ".displayname") + "' while checking for a station. There is no world '" + DragonTravelMain.dbStationsConfig.getString(pathToStation + ".world") + "' on the server!");
					continue;
				}


				if (!world.getName().equalsIgnoreCase(player.getWorld().getName()))
					continue;

				x = DragonTravelMain.dbStationsConfig.getInt(pathToStation + ".x");
				y = DragonTravelMain.dbStationsConfig.getInt(pathToStation + ".y");
				z = DragonTravelMain.dbStationsConfig.getInt(pathToStation + ".z");

				tempLoc = new Location(world, x, y, z);

				if (tempLoc.distance(playerLoc) <= DragonTravelMain.config.getInt("MountingLimit.Radius"))
					return true;
			}
		}

		return false;
	}
}
