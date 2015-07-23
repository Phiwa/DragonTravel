package eu.phiwa.dt.commands;

import eu.phiwa.dt.DragonTravelMain;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandHelp {

	public static ChatColor blue = ChatColor.BLUE;
	public static ChatColor darkgrey = ChatColor.DARK_GRAY;
	public static ChatColor gold = ChatColor.GOLD;
	public static ChatColor grey = ChatColor.GRAY;
	public static ChatColor purple = ChatColor.LIGHT_PURPLE;
	public static ChatColor red = ChatColor.RED;
	public static ChatColor white = ChatColor.WHITE;
	public static ChatColor yellow = ChatColor.YELLOW;
	
	public static final int HELP_Page1 = 1;	
	public static final int HELP_Page2 = 2;
	public static final int HELP_Page3 = 3;
	public static final int HELP_Page4 = 4;
	public static final int HELP_Page5 = 5;
	
	public static void page1_TableOfContents(Player player) {		
		player.sendMessage("    1 - Table of Contents");
		// TODO: ADD Permission-Checks to show the correct pages
		player.sendMessage("    2 - General");
		player.sendMessage("    3 - Travels");
		player.sendMessage("    4 - Flights");
		player.sendMessage("    5 - Administrative");
	}
	
	public static void page2_General(Player player) {
		player.sendMessage(grey+"/dt dismount" + darkgrey + " - " + white + "Dismounts you from the dragons. \n"
						  + "                   Depending on the server's settings, you might\n"
						  + "                   be teleported back to the point you started\n"
						  + "                   your journey from.");
		player.sendMessage(grey+"/dt sethome" + darkgrey + " - " + white + "Sets your home.");
		player.sendMessage(grey+"/dt ptoggle" + darkgrey + " - " + white + "Toogles whether you allow/don't allow\n"
						  + "                   player-travels to you.");
	}
	
	public static void page3_Travels(Player player) {
		player.sendMessage(purple + "Arguments in [] are optional.\n");
		player.sendMessage(grey+"/dt travel <stationname>" + darkgrey + " - " + white + "Brings you to the specified station.");
		player.sendMessage(grey+"/dt ctravel <x> <y> <z> [<world>]" + darkgrey + " - " + white + "Brings you to the specified\n"
							+ "                                             location.");
		player.sendMessage(grey+"/dt ptravel <playername>" + darkgrey + " - " + white + "Brings you to the specified player.");
		player.sendMessage(grey+"/dt home" + darkgrey + " - " + white + "Brings you to your home.");
		if(player.hasPermission("dt.fhome") && DragonTravelMain.pm.getPlugin("Factions") != null)
			player.sendMessage(grey+"/dt fhome" + darkgrey + " - " + white + "Brings you to your faction's home.");
		player.sendMessage(grey+"/dt showstats" + darkgrey + " - " + white + "Shows a list of all available stations.");
	}
	
	public static void page4_Flights(Player player) {
		player.sendMessage(grey+"/dt flight <flightname>" + darkgrey + " - " + white + "Starts the specified flight.");
		player.sendMessage(grey+"/dt showflights" + darkgrey + " - " + white + "Shows a list of all available flights.");
	}
	
	public static void page5_Administrative(Player player) {	
		player.sendMessage(grey+"/dt remdragons" + darkgrey + " - " + white + "Removes all dragons (except stationary\n"
							+ "                        dragons) without riders.");
		player.sendMessage("\n" + darkgrey+"------Stations------");
		player.sendMessage(grey+"/dt setstat <stationname>" + darkgrey + " - " + white + "Creates a new station with the\n"
							+ "                                    specified name at your\n"
							+ "                                    current location.");
		player.sendMessage(grey+"/dt remstat <stationname>" + darkgrey + " - " + white + "Removes the station with\n"
							+ "                                    the specified name.");
		player.sendMessage("\n" + darkgrey+"------Flights------");
		player.sendMessage(grey+"/dt createflight <flightname>" + darkgrey + " - " + white + "Creates a new flight and puts\n"
							+ "                                        you into the\n"
							+ "                                        flight-creation mode.");
		player.sendMessage(grey+"/dt saveflight" + darkgrey + " - " + white + "Saves the flight an finishes\n"
							+ "                      the flight-creation mode.");                        
		player.sendMessage(grey+"/dt remflight <flightname>" + darkgrey + " - " + white + "Removes the flight with the\n"
							+ "                                        specified name.");
		player.sendMessage(grey+"/dt setwp" + darkgrey + " - " + white + "Only works in flight-creation mode:\n"
							+ "                Sets the next waypoint..");
		player.sendMessage(grey+"/dt remlastwp" + darkgrey + " - " + white + "Only works in flight-creation mode:\n"
							+ "                     Removes the most recently set waypoint.");
		player.sendMessage(grey+"/dt addstatdragon <name> [display name]" + darkgrey + " - " + white + "Create a new stationary dragon");
		player.sendMessage(grey+"/dt remstatdragon <name>" + darkgrey + " - " + white + "Remove a stationary dragon");
		player.sendMessage(grey+"/dt reload" + darkgrey + " - " + white + "Reloads all files (extremely buggy!)");
		//player.sendMessage(grey+"/dt flight <flightname>" + darkgrey + " - " + white + "Starts the specified flight.");
		//player.sendMessage(grey+"/dt flight <flightname>" + darkgrey + " - " + white + "Starts the specified flight.");
	}
}
