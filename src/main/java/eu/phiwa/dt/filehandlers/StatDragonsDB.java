package eu.phiwa.dt.filehandlers;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.RyeDragon;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class StatDragonsDB {

	DragonTravelMain plugin;

	public StatDragonsDB(DragonTravelMain plugin) {
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
		
		if (DragonTravelMain.dbStatDragonsFile.exists())
			return;

		try {
			DragonTravelMain.dbStatDragonsFile.createNewFile();
			copy(this.plugin.getResource("databases/statdragons.yml"), DragonTravelMain.dbStatDragonsFile);
			DragonTravelMain.logger.info("[DragonTravel] Created statdragons-database.");
		}
		catch(Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not create the statdragons-database!");
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
	public boolean createStatDragon(String name, Location loc)  {
		
		String path = "StatDragons." + name;
		
		ConfigurationSection sec = DragonTravelMain.dbStatDragonsConfig.createSection(path);
		DragonTravelMain.dbStatDragonsConfig.createPath(sec, "x");
		DragonTravelMain.dbStatDragonsConfig.createPath(sec, "y");
		DragonTravelMain.dbStatDragonsConfig.createPath(sec, "z");
		DragonTravelMain.dbStatDragonsConfig.createPath(sec, "yaw");
		DragonTravelMain.dbStatDragonsConfig.createPath(sec, "pitch");
		DragonTravelMain.dbStatDragonsConfig.createPath(sec, "world");
		DragonTravelMain.dbStatDragonsConfig.set(path + ".x", loc.getX());
		DragonTravelMain.dbStatDragonsConfig.set(path+".y", loc.getY());
		DragonTravelMain.dbStatDragonsConfig.set(path+".z", loc.getZ());
		DragonTravelMain.dbStatDragonsConfig.set(path+".yaw", loc.getYaw());
		DragonTravelMain.dbStatDragonsConfig.set(path+".pitch", loc.getPitch());
		DragonTravelMain.dbStatDragonsConfig.set(path + ".world", loc.getWorld().getName());
		
		try{
			DragonTravelMain.dbStatDragonsConfig.save(DragonTravelMain.dbStatDragonsFile);
			return true;
		}
		catch(Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not write new home to config.");
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
		
		DragonTravelMain.dbStatDragonsConfig.set(name, null);
		
		try{
			DragonTravelMain.dbStatDragonsConfig.save(DragonTravelMain.dbStatDragonsFile);
			return true;
		}
		catch(Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not delete stat dragon from config.");
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
	public RyeDragon getStatDragon(String name) {

		name = "StatDragons." + name.toLowerCase();
		
		if(!DragonTravelMain.listofStatDragons.containsKey(name))
			return null;
		return DragonTravelMain.listofStatDragons.get(name);

	}
	
	public void init() {
	
		DragonTravelMain.dbStatDragonsFile = new File("plugins/DragonTravel/databases", "statdragons.yml");
	
		try {
			create();
		}
		catch (Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] Could not initialize the statdragons-database.");
			e.printStackTrace();
		}
	
		DragonTravelMain.dbStatDragonsConfig = new YamlConfiguration();
		load();
	
	}
	
	private void load() {
		try {
			DragonTravelMain.dbStatDragonsConfig.load(DragonTravelMain.dbStatDragonsFile);
			DragonTravelMain.logger.info("[DragonTravel] Loaded statdragons-database.");
		}
		catch (Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] No statdragons-database found");
			e.printStackTrace();
		}
	}
	
	public void showStatDragons(Player player) {
		player.sendMessage("Stationary Dragons [Name (X, Y, Z, World)]: ");
		for(Map.Entry<String, RyeDragon> entry : DragonTravelMain.listofStatDragons.entrySet()) {
			String loc = "(" + entry.getValue().getEntity().getLocation().getBlockX()+", " + entry.getValue().getEntity().getLocation().getBlockY()+", " + entry.getValue().getEntity().getLocation().getBlockZ() + ", " + entry.getValue().getEntity().getLocation().getWorld().getName() + ")";
            player.sendMessage("- " + entry.getKey() + " " + loc);
		}			
	}
}
