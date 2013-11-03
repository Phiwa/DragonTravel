package eu.phiwa.dt.filehandlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import org.bukkit.configuration.file.YamlConfiguration;

import eu.phiwa.dt.DragonTravelMain;

public class Config {

	DragonTravelMain plugin;

	public Config(DragonTravelMain plugin) {
		this.plugin = plugin;
	}

	public void loadConfig() {
		DragonTravelMain.configFile = new File(plugin.getDataFolder(), "config.yml");
		if (!DragonTravelMain.configFile.exists())
			deployDefaultFile("config.yml");
		DragonTravelMain.config = YamlConfiguration.loadConfiguration(DragonTravelMain.configFile);
		updateConfig();
	}

	private void updateConfig() {
		if (DragonTravelMain.config.getDouble("File.Version") != DragonTravelMain.configVersion)
			newlyRequiredConfig();
		noLongerRequiredConfig();
		// Refresh file and config variables for persistence.
		try {
			DragonTravelMain.config.save(DragonTravelMain.configFile);
			DragonTravelMain.config = YamlConfiguration.loadConfiguration(DragonTravelMain.configFile);
		} catch (IOException e) {
			e.printStackTrace();
			DragonTravelMain.logger.log(Level.SEVERE, "Could not update config, disabling plugin!");
		}
	}

	private void newlyRequiredConfig() {

		// New options in version 0.2
		if (!DragonTravelMain.config.isSet("PToggleDefault"))
			DragonTravelMain.config.set("PToggleDefault", true);


		// Update the file version
		DragonTravelMain.config.set("File.Version", DragonTravelMain.configVersion);

	}

	private void noLongerRequiredConfig() {
		// DragonTravelMain.config.set("example key", null);
	}


	private void deployDefaultFile(String name) {
		try {
			File target = new File(this.plugin.getDataFolder(), name);
			InputStream source = this.plugin.getResource("eu/phiwa/dt/filehandlers/" + name);

			if (!target.exists()) {
				OutputStream output = new FileOutputStream(target);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = source.read(buffer)) > 0)
					output.write(buffer, 0, len);
				output.close();
			}
			source.close();
			DragonTravelMain.logger.info("Deployed " + name);
		} catch (Exception e) {
			DragonTravelMain.logger.info("Could not save default file");
		}
	}

}
