package eu.phiwa.dt.filehandlers;

import eu.phiwa.dt.DragonTravelMain;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.logging.Level;

public class Messages {

	private String language = "";	
	String pathInsideJAR = "main/resources/messages/";
	
	String pathOnServer = "plugins/DragonTravel/messages";
	
	DragonTravelMain plugin;
	public Messages (DragonTravelMain plugin) {
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
		
		if (DragonTravelMain.messagesFile.exists())
			return;

		try {
			DragonTravelMain.messagesFile.createNewFile();
			copy(this.plugin.getResource("messages/messages-"+language+".yml"), DragonTravelMain.messagesFile);
			DragonTravelMain.logger.info("[DragonTravel] Created messages file.");
		}
		catch(Exception e) {
			DragonTravelMain.logger.info("[DragonTravel] [Error] Could not create the languages file - check the language!");
			e.printStackTrace();
		}	
	}
	
	public String getMessage(String path) {
	
		String message;

		message = replaceColors(DragonTravelMain.messages.getString(path));
		
		if(message == null) {
			DragonTravelMain.logger.log(Level.SEVERE, "[DragonTravel] Could not find the message looking for at path '"+path+"' which leads to a serious problem! Be try to generate a new language file if you previously updated DragonTravel!");
			return replaceColors("&cAn error occured, please contact the admin! Missing message '"+path+"'");
		}
			
		if(message.length() == 0)
			return ChatColor.RED + "Error, could not read message-text from file, please contact the admin.";
		
		return message;
	}
		
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
			create();
		
		DragonTravelMain.messages = YamlConfiguration.loadConfiguration(DragonTravelMain.messagesFile);
		updateConfig();
	}
	private void newlyRequiredMessages(){
    
		// Add new keys here!
		
	  // v0.0.0.9
        if (DragonTravelMain.messages.get("Messages.Flights.Error.OnlySigns") == null)
        	DragonTravelMain.messages.set("Messages.Flights.Error.OnlySigns", "&cThis command has been disabled by the admin, you can only use flights using signs.");      
        if (DragonTravelMain.messages.get("Messages.Stations.Error.NotCreateStationWithRandomstatName") == null)
        	DragonTravelMain.messages.set("Messages.Stations.Error.NotCreateStationWithRandomstatName", "&cYou cannot create a staion with the name of the RandomDest.");
        
      // v0.0.0.17
        if (DragonTravelMain.messages.get("Messages.Factions.Error.NotYourFaction") == null)
        	DragonTravelMain.messages.set("Messages.Factions.Error.NotYourFaction", ": &cThis is not your faction.");      
	
      // 0.5
        if (DragonTravelMain.messages.get("Messages.General.Error.BelowMinMountHeight") == null)
        	DragonTravelMain.messages.set("Messages.General.Error.BelowMinMountHeight", "&cYou are below the minimum height required to mount a dragon. Minimum height is &f{minheight}&c.");      
        if (DragonTravelMain.messages.get("Messages.General.Error.DamageCooldown") == null)
        	DragonTravelMain.messages.set("Messages.General.Error.DamageCooldown", "&cYou must wait &f{seconds} &cmore seconds before you can mount a dragon.");

		//0.6
		if (DragonTravelMain.messages.get("Messages.General.Error.StatDragonExists") == null)
			DragonTravelMain.messages.set("Messages.General.Error.StatDragonExists", ": &cThis name is already taken.");
		if (DragonTravelMain.messages.get("Messages.General.Error.StatDragonNotExists") == null)
			DragonTravelMain.messages.set("Messages.General.Error.StatDragonNotExists", ": &cThis name is not recognised.");
        if (DragonTravelMain.messages.get("Messages.General.Error.StatDragonCmdRevised") == null)
            DragonTravelMain.messages.set("Messages.General.Error.StatDragonCmdRevised", ": &cThis command now takes a parameter - you must include a name. Check help page 5 for more details.");

		// Update the file version
		DragonTravelMain.messages.set("File.Version", DragonTravelMain.messagesVersion);
	}
	
	
	private void noLongerRequiredMessages() {
		// DragonTravelMain.config.set("example key", null);
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
}
