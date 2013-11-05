package eu.phiwa.dt.filehandlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.Home;

public class HomesDB {
	@SuppressWarnings("unused")
	private DragonTravelMain plugin;
	private File dbHomesFile;
	private FileConfiguration dbHomesConfig;
	private ConfigurationSection homeSection;

	public HomesDB(DragonTravelMain plugin) {
		this.plugin = plugin;
	}

	public void init() {

		dbHomesFile = new File(DragonTravelMain.databaseFolder, "homes.yml");

		try {
			create();
		} catch (Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] Could not initialize the homes-database.");
			e.printStackTrace();
		}

		dbHomesConfig = new YamlConfiguration();
		load();

		homeSection = dbHomesConfig.getConfigurationSection("Homes");
		if (homeSection == null) {
			homeSection = dbHomesConfig.createSection("Homes");
		}
	}

	private void create() {

		if (dbHomesFile.exists())
			return;

		try {
			dbHomesFile.createNewFile();
			copy(getClass().getResourceAsStream("homes.yml"), dbHomesFile);
			DragonTravelMain.logger.info("[DragonTravel] Created homes-database.");
		} catch (Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not create the homes-database!");
		}


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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void load() {
		try {
			dbHomesConfig.load(dbHomesFile);
			DragonTravelMain.logger.info("[DragonTravel] Loaded homes-database.");
		} catch (Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] No homes-database found");
			e.printStackTrace();
		}
	}


	/**
	 * Returns the details of the home with the given name.
	 *
	 * @param homename Name of the home which should be returned.
	 * @return The home as a home-object, or null if no home is set.
	 */
	public Home getHome(String playerName) {
		Object obj = homeSection.get(playerName, null);
		if (obj == null) {
			return null;
		}

		// Transition support
		if (obj instanceof ConfigurationSection) {
			Home h = new Home(((ConfigurationSection) obj).getValues(true));
			saveHome(playerName, h);
			return h;
		} else {
			return (Home) obj;
		}
	}

	/**
	 * Saves a new home.
	 *
	 * @param home Home to create.
	 * @return Returns true if the home was created successfully, false if
	 *         not.
	 */
	public boolean saveHome(String playerName, Home home) {
		homeSection.set(playerName, home);

		try {
			dbHomesConfig.save(dbHomesFile);
			return true;
		} catch (Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not write new home to config.");
			return false;
		}
	}

	/**
	 * Deletes the given home.
	 *
	 * @param homename Name of the home to delete
	 * @return True if successful, false if not.
	 */
	public boolean deleteHome(String playername) {

		playername = "Homes." + playername.toLowerCase();

		dbHomesConfig.set(playername, null);

		try {
			dbHomesConfig.save(dbHomesFile);
			return true;
		} catch (Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not delete home from config.");
			return false;
		}
	}

	public void showHomes() {
		System.out.println("Player's who registered a home: ");
		for (String string : dbHomesConfig.getConfigurationSection("Homes").getKeys(true)) {
			if (!string.contains("."))
				System.out.println("- " + string);
		}
	}

	public void showStations(Player player) {
		player.sendMessage("Player's who registered a home: ");
		for (String string : dbHomesConfig.getConfigurationSection("Homes").getKeys(true)) {
			if (!string.contains("."))
				player.sendMessage("- " + string);
		}
	}
}
