package eu.phiwa.dt.filehandlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import eu.phiwa.dt.DragonTravelMain;

import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

	DragonTravelMain plugin;
	private File configFile;
	private YamlConfiguration configuration;

	public Config (DragonTravelMain plugin) {
		this.plugin = plugin;
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
		
		if (DragonTravelMain.configFile.exists())
			return;

		try {
			DragonTravelMain.configFile.createNewFile();
			copy(this.plugin.getResource("config.yml"), DragonTravelMain.configFile);
			DragonTravelMain.logger.info("[DragonTravel] Created config file.");
		}
		catch(Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not create the configuration!");
			e.printStackTrace();
		}
	}
	
	public void loadConfig(){
		DragonTravelMain.configFile = new File(plugin.getDataFolder(), "config.yml");
		if(!DragonTravelMain.configFile.exists())
			create();
		DragonTravelMain.config = YamlConfiguration.loadConfiguration(DragonTravelMain.configFile);
		updateConfig();
	}
	private void newlyRequiredConfig(){
		
	  // New options in version 0.2					
		if(!DragonTravelMain.config.isSet("PToggleDefault"))
			DragonTravelMain.config.set("PToggleDefault", true);
		
	  // New options in version 0.3
		if(!DragonTravelMain.config.isSet("MaxTravelDistance"))
			DragonTravelMain.config.set("MaxTravelDistance", -1);		
		
	  // New options in version 0.4				
		if(!DragonTravelMain.config.isSet("DismountAtExactLocation"))
			DragonTravelMain.config.set("DismountAtExactLocation", false);
		
	 // New options in version 0.5				
		if(!DragonTravelMain.config.isSet("MinimumMountHeight"))
			DragonTravelMain.config.set("MinimumMountHeight", -1);				
		if(!DragonTravelMain.config.isSet("DamageCooldown"))
			DragonTravelMain.config.set("DamageCooldown", -1);		
		
		
	  // Update the file version
		DragonTravelMain.config.set("File.Version", DragonTravelMain.configVersion);
		
	}
	private void noLongerRequiredConfig() {
		// DragonTravelMain.config.set("example key", null);
	}
	
	
	private void updateConfig(){
		if(DragonTravelMain.config.getDouble("File.Version") != DragonTravelMain.configVersion) 
			newlyRequiredConfig();
		noLongerRequiredConfig();
		// Refresh file and config variables for persistence.
		try {
			DragonTravelMain.config.save(DragonTravelMain.configFile);
			DragonTravelMain.config = YamlConfiguration.loadConfiguration(DragonTravelMain.configFile);
		} catch (IOException e) {
			e.printStackTrace();
			DragonTravelMain.logger.log(Level.SEVERE , "Could not update config, disabling plugin!");
		}
	}
	
}
