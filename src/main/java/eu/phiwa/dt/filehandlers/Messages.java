package eu.phiwa.dt.filehandlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import eu.phiwa.dt.DragonTravelMain;

public class Messages {

	String pathInsideJAR = "eu/phiwa/dt/filehandlers/messages/";	
	String pathOnServer = "plugins/DragonTravel/messages";
	
	DragonTravelMain plugin;
	
	public Messages (DragonTravelMain plugin) {
		this.plugin = plugin;
	}
	private String language = "";
	
	
	public void loadMessages(){
		
		language = this.plugin.getConfig().getString("Language");
		
		if(language == null) {
			DragonTravelMain.logger.log(Level.SEVERE,
				"Could not load messages-file because the language could not be read from the config! Disabling plugin!");
			
			DragonTravelMain.pm.disablePlugin(DragonTravelMain.plugin);
			return;
		}			
		
		DragonTravelMain.messagesFile = new File(plugin.getDataFolder(), "messages-"+language+".yml");
		
		if(!DragonTravelMain.messagesFile.exists())
			deployDefaultFile("messages-"+language+".yml");
		
		DragonTravelMain.messages = YamlConfiguration.loadConfiguration(DragonTravelMain.messagesFile);
		updateConfig();
	}
	
	private void updateConfig(){
		
		if(DragonTravelMain.messages.getDouble("File.Version") != DragonTravelMain.messagesVersion)
			newlyRequiredMessages();
		
		noLongerRequiredMessages();
		
		// Refresh file and config variables for persistence.
		try {
			DragonTravelMain.messagesFile = new File(plugin.getDataFolder(), "messages-"+language+".yml");
			DragonTravelMain.messages.save(DragonTravelMain.messagesFile);
			DragonTravelMain.messages = YamlConfiguration.loadConfiguration(DragonTravelMain.messagesFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	private void newlyRequiredMessages(){
    
		// Add new keys here!
		
	  // v0.0.0.9
        if (DragonTravelMain.config.get("Messages.Flights.Error.OnlySigns") == null)
        	DragonTravelMain.config.set("Messages.Flights.Error.OnlySigns", "&cThis command has been disabled by the admin, you can only use flights using signs.");      
        if (DragonTravelMain.config.get("Messages.Stations.Error.NotCreateStationWithRandomstatName") == null)
        	DragonTravelMain.config.set("Messages.Stations.Error.NotCreateStationWithRandomstatName", "&cYou cannot create a staion with the name of the RandomDest.");
	}
	private void noLongerRequiredMessages() {
		// DragonTravelMain.config.set("example key", null);
	}
	
	
	private void deployDefaultFile(String name) {
		try {
			File target = new File(this.plugin.getDataFolder(), name);
			InputStream source = this.plugin.getResource("eu/phiwa/dt/filehandlers/messages/"+name);

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
			DragonTravelMain.logger.log(Level.SEVERE, "Could not save default file");
		}
	}
	

	public String getMessage(String path) {
	
		String message;

		message = replaceColors(DragonTravelMain.messages.getString(path));
		
		if(message == null) {
			DragonTravelMain.logger.log(Level.SEVERE, "[DragonTravel] Could not find the message looking for at path '"+path+"' which leads to a serious problem! Be try to generate a new language file if you previously updated DragonTravel!");
			return replaceColors("&cAn error occured, please contact the admin!");
		}
			
		if(message.length() == 0)
			return ChatColor.RED + "Error, could not read message-text from file, please contact the admin.";
		
		return message;
	}
	
	private String replaceColors(String string) {
		
		String formattedMessage = null;
		
		try {
			formattedMessage = string.replaceAll("(?i)&([a-f0-9])", "\u00A7$1");
		}
		catch(Exception ex) {
			DragonTravelMain.logger.warning("[DragonTravel] [Error] Could not read a message from the messages-xx.yml!");
		}
		
		return formattedMessage;
	}
}
