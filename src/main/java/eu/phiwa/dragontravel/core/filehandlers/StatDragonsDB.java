package eu.phiwa.dragontravel.core.filehandlers;

import eu.phiwa.dragontravel.core.DragonTravelMain;
import eu.phiwa.dragontravel.nms.IRyeDragon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Level;

public class StatDragonsDB {

	// StatDragonsDB
	private FileConfiguration dbStatDragonsConfig;
	private File dbStatDragonsFile;

	public StatDragonsDB() {
		init();
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
		
		if (dbStatDragonsFile.exists())
			return;

		try {
			dbStatDragonsFile.createNewFile();
			copy(DragonTravelMain.getInstance().getResource("databases/statdragons.yml"), dbStatDragonsFile);
			Bukkit.getLogger().log(Level.INFO, "Created statdragons-database.");
		}
		catch(Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not create the statdragons-database!");
		}
		
		
	}
	
	/**
	 * Creates a new stat dragon.
	 * 
	 * @param name
	 * 			Dragon name to create.
	 * @param loc
	 * 			Dragon location.
	 * @return
	 * 			Returns true if the stat dragon was created successfully, false if not.
	 */
	@SuppressWarnings("static-access")
	public boolean createStatDragon(String name, String displayName, Location loc)  {
		
		String path = "StatDragons." + name;
		
		ConfigurationSection sec = dbStatDragonsConfig.createSection(path);
		dbStatDragonsConfig.createPath(sec, "x");
		dbStatDragonsConfig.createPath(sec, "y");
		dbStatDragonsConfig.createPath(sec, "z");
		dbStatDragonsConfig.createPath(sec, "yaw");
		dbStatDragonsConfig.createPath(sec, "pitch");
		dbStatDragonsConfig.createPath(sec, "world");
		dbStatDragonsConfig.createPath(sec, "displayname");
		dbStatDragonsConfig.set(path + ".x", loc.getX());
		dbStatDragonsConfig.set(path+".y", loc.getY());
		dbStatDragonsConfig.set(path+".z", loc.getZ());
		dbStatDragonsConfig.set(path+".yaw", loc.getYaw());
		dbStatDragonsConfig.set(path+".pitch", loc.getPitch());
		dbStatDragonsConfig.set(path + ".world", loc.getWorld().getName());
		dbStatDragonsConfig.set(path + ".displayname", displayName);

		try{
			dbStatDragonsConfig.save(dbStatDragonsFile);
			return true;
		}
		catch(Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not write new home to config.");
			return false;
		}
	}

	/**
	 * Deletes the given dragon.
	 * 
	 * @param name
	 * 			Name of the stat dragon to delete
	 * @return
	 * 			True if successful, false if not.
	 */
	public boolean deleteStatDragon(String name) {
		
		name = "StatDragons." + name;
		
		dbStatDragonsConfig.set(name, null);
		
		try{
			dbStatDragonsConfig.save(dbStatDragonsFile);
			return true;
		}
		catch(Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not delete stat dragon from config.");
			return false;
		}
	}

	
	/**
	 * Returns the details of the dragon with the given name.
	 * 
	 * @param name
	 * 			Name of the dragon which should be returned.
	 * @return
	 * 			The dragon as a ryedragon-object.
	 */
	public IRyeDragon getStatDragon(String name) {

		name = "StatDragons." + name;
		
		if(!DragonTravelMain.listofStatDragons.containsKey(name))
			return null;
		return DragonTravelMain.listofStatDragons.get(name);

	}
	
	public void init() {
	
		dbStatDragonsFile = new File("plugins/DragonTravel/databases", "statdragons.yml");
	
		try {
			create();
		}
		catch (Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not initialize the statdragons-database.");
			e.printStackTrace();
		}
	
		dbStatDragonsConfig = new YamlConfiguration();
		load();
	
	}
	
	private void load() {
		try {
			dbStatDragonsConfig.load(dbStatDragonsFile);
			Bukkit.getLogger().log(Level.INFO, "Loaded statdragons-database.");
		}
		catch (Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "No statdragons-database found");
			e.printStackTrace();
		}
	}
	
	public void showStatDragons(Player player) {
		player.sendMessage("Stationary Dragons [Name (X, Y, Z, World)]: ");
		for(Map.Entry<String, IRyeDragon> entry : DragonTravelMain.listofStatDragons.entrySet()) {
			String loc = "(" + entry.getValue().getEntity().getLocation().getBlockX()+", " + entry.getValue().getEntity().getLocation().getBlockY()+", " + entry.getValue().getEntity().getLocation().getBlockZ() + ", " + entry.getValue().getEntity().getLocation().getWorld().getName() + ")";
            player.sendMessage("- " + entry.getKey() + " " + loc);
		}			
	}

	public FileConfiguration getDbStatDragonsConfig() {
		return dbStatDragonsConfig;
	}

	public void setDbStatDragonsConfig(FileConfiguration dbStatDragonsConfig) {
		this.dbStatDragonsConfig = dbStatDragonsConfig;
	}

	public File getDbStatDragonsFile() {
		return dbStatDragonsFile;
	}

	public void setDbStatDragonsFile(File dbStatDragonsFile) {
		this.dbStatDragonsFile = dbStatDragonsFile;
	}
}
