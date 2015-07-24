package eu.phiwa.dragontravel.core.movement;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import eu.phiwa.dragontravel.core.DragonTravelMain;
import eu.phiwa.dragontravel.nms.IRyeDragon;
import eu.phiwa.dragontravel.core.modules.DragonManagement;
import eu.phiwa.dragontravel.core.objects.Home;
import eu.phiwa.dragontravel.core.objects.Station;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Travels {
	
	private static float getCorrectYawForPlayer(Player player, Location destination) {

		if (player.getLocation().getBlockZ() > destination.getBlockZ())
			return (float) (-Math.toDegrees(Math.atan((player.getLocation().getBlockX() - destination.getBlockX())	/ (player.getLocation().getBlockZ() - destination.getBlockZ())))) + 180.0F;
		else if (player.getLocation().getBlockZ() < destination.getBlockZ())
			return (float) (-Math.toDegrees(Math.atan((player.getLocation().getBlockX() - destination.getBlockX())	/ (player.getLocation().getBlockZ() - destination.getBlockZ()))));
		else
			return player.getLocation().getYaw();
	}
	
	/**
	 * Travel to specified coordinates
	 * 
	 * @param player
	 * @param x
	 * @param y
	 * @param z
	 * @param worldname
	 * 				If "null", the player's current world is used.
	 * @param checkForStation
	 * 			Whether or not DragonTravel should check
	 * 			if the player is at a station and return if not.
	 * 			If the admin disabled the station-check globally,
	 * 			this has no function.
	 */
	public static void toCoordinates(Player player, int x, int y, int z, String worldname, Boolean checkForStation) {
		
		World world;
		
		if(worldname == null) {
			//No world was used in the command, using player's current world
			world = player.getWorld(); 		
		}
		else {
			// Trying to find the world with the name the player used in the command
			world = Bukkit.getWorld(worldname);
			
			// If the world cannot be found, send an error-message to the player
			if(world == null) {
				player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Travels.Error.WorldNotFound"));
				return;
			}
		}
			
		if(DragonTravelMain.getInstance().getConfigHandler().isRequireItemTravelCoordinates()) {
			if(!player.getInventory().contains(DragonTravelMain.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
				player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
				return;
			}
		}
		
		Location loc = new Location(world, x, y, z);
		String message = "";
		if(world.getName() == player.getWorld().getName()) {
			message = DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToCoordinatesSameWorld");
			message = message.replace("{x}", "%d");
			message = String.format(message, x);
			message = message.replace("{y}", "%d");
			message = String.format(message, y);
			message = message.replace("{z}", "%d");
			message = String.format(message, z);
			player.sendMessage(message);
		}
		else {
			message =  DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToCoordinatesOtherWorld");
			message.replace("{x}", "%d");
			message = String.format(message, x);
			message.replace("{y}", "%d");
			message = String.format(message, y);
			message.replace("{z}", "%d");
			message = String.format(message, z);
			message.replace("{worldname}", "%s");
			message = String.format(message, world.getName());
			player.sendMessage(message);
		}		
		
		travel(player, loc, checkForStation, message);
		
	}
	
	/**
	 * Travel to the specified player's faction's home
	 * 
	 * @param player
	 * 			Player to bring to his faction's home
	 * @param checkForStation
	 * 			Whether or not DragonTravel should check
	 * 			if the player is at a station and return if not.
	 * 			If the admin disabled the station-check globally,
	 * 			this has no function.
	 */
	public static void toFactionhome(Player player, Boolean checkForStation) {
		
		if(Bukkit.getPluginManager().getPlugin("Factions") == null) {
			player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.FactionsNotInstalled"));
			return;
		}
		
		if(DragonTravelMain.getInstance().getConfigHandler().isRequireItemTravelFactionhome()) {
			if(!player.getInventory().contains(DragonTravelMain.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
				player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
				return;
			}
		}
		
		Faction faction = UPlayer.get(player).getFaction();
		
		
		if(faction.isNone()) {
			player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.NoFactionMember"));
			return;
		}
	
		if(!faction.hasHome()) {
			player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.FactionHasNoHome"));
			return;
		}
		else
			travel(player, faction.getHome().asBukkitLocation(), checkForStation, DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToFactionHome"));
		
	}
	
	/**
	 * Travel to the specified player's home
	 * 
	 * @param player
	 * 			Player to bring to his home
	 * @param checkForStation
	 * 			Whether or not DragonTravel should check
	 * 			if the player is at a station and return if not.
	 * 			If the admin disabled the station-check globally,
	 * 			this has no function.
	 */
	public static void toHome(Player player, Boolean checkForStation) {
		
		Home home = DragonTravelMain.getInstance().getDbHomesHandler().getHome(player.getUniqueId().toString());
		
		if((home) == null)  {
			player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Travels.Error.NoHomeSet"));
			return;
		}

		if(DragonTravelMain.getInstance().getConfigHandler().isRequireItemTravelHome()) {
			if(!player.getInventory().contains(DragonTravelMain.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
				player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
				return;
			}
		}
		

		Location destinationLoc = new Location(home.world, home.x, home.y, home.z);
		travel(player, destinationLoc, checkForStation, DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToHome"));
		
		player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToHome"));
	}
	
	/**
	 * Travel to a specified player
	 * 
	 * @param player
	 * 			Player to bring to the other player
	 * @param targetplayer
	 * 			Player to bring the traveling player to
	 * @param checkForStation
	 * 			Whether or not DragonTravel should check
	 * 			if the player is at a station and return if not.
	 * 			If the admin disabled the station-check globally,
	 * 			this has no function.
	 */
	public static void toPlayer(Player player, Player targetplayer, Boolean checkForStation) {
		
		if(DragonTravelMain.getInstance().getConfigHandler().isRequireItemTravelPlayer()) {
			if(!player.getInventory().contains(DragonTravelMain.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
				player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
				return;
			}
		}
		
		Location targetLoc = targetplayer.getLocation();
		travel(player, targetLoc, checkForStation, DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToPlayer").replace("{playername}", targetplayer.getDisplayName()));
		
	}
	
	/**
	 * Travel to a random destination within the borders set in the config
	 * 
	 * @param player
	 * 			Player to bring to a random destination
	 * @param checkForStation
	 * 			Whether or not DragonTravel should check
	 * 			if the player is at a station and return if not
	 */
	public static void toRandomdest(Player player, Boolean checkForStation) {
		
		if(DragonTravelMain.getInstance().getConfigHandler().isRequireItemTravelRandom()) {
			if(!player.getInventory().contains(DragonTravelMain.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
				player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
				return;
			}
		}
		
		int minX = DragonTravelMain.getInstance().getConfig().getInt("RandomDest.Limits.X-Axis.Min");
		int maxX = DragonTravelMain.getInstance().getConfig().getInt("RandomDest.Limits.X-Axis.Max");
		int minZ = DragonTravelMain.getInstance().getConfig().getInt("RandomDest.Limits.Z-Axis.Min");
		int maxZ = DragonTravelMain.getInstance().getConfig().getInt("RandomDest.Limits.Z-Axis.Max");

		double x = minX + (Math.random() * (maxX - 1));
		double z = minZ + (Math.random() * (maxZ - 1));

		Location randomLoc = new Location(player.getWorld(), x, 10, z);
		randomLoc.setY(randomLoc.getWorld().getHighestBlockAt(randomLoc).getY());
		
		player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToRandomLocation"));
		
		travel(player, randomLoc, checkForStation, DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToRandomLocation"));
	}
	
	/**
	 * Travel to a specified station
	 * 
	 * @param player
	 * @param stationname
	 * @param checkForStation
	 * 			Whether or not DragonTravel should check
	 * 			if the player is at a station and return if not.
	 * 			If the admin disabled the station-check globally,
	 * 			this has no function.
	 */
	public static void toStation(Player player, String stationname, Boolean checkForStation) {
		
		Station destination = DragonTravelMain.getInstance().getDbStationsHandler().getStation(stationname);

		if(destination == null)  {
			player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.StationDoesNotExist").replace("{stationname}", stationname));
			return;
		}
			
		if(DragonTravelMain.getInstance().getConfigHandler().isRequireItemTravelStation()) {
			if(!player.getInventory().contains(DragonTravelMain.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
				player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
				return;
			}
		}	

		player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToStation").replace("{stationname}", destination.displayname));
		
		Location destinationLoc = new Location(destination.world, destination.x, destination.y, destination.z);
		travel(player, destinationLoc, checkForStation, DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToStation").replace("{stationname}", destination.displayname));
	}
	
	/**
	 * Core-method of this class, handles the travel itself (e.g. the difference between normal and interworld-travels
	 * 
	 * @param player
	 * @param destination
	 * @param checkForStation
	 * 			Whether or not DragonTravel should check
	 * 			if the player is at a station and return if not.
	 * 			If the admin disabled the station-check globally,
	 * 			this has no function.
	 */
	public static void travel(Player player, Location destination, Boolean checkForStation, String destName) {

		// Check for station
		if(checkForStation && DragonTravelMain.getInstance().getConfig().getBoolean("MountingLimit.EnableForTravels") && !player.hasPermission("dt.ignoreusestations.travels")) {
			if(!DragonTravelMain.getInstance().getDbStationsHandler().checkForStation(player)) {
				player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.NotAtAStation"));
				return;
			}
		}
		
		Location temploc = player.getLocation();

        if (!player.hasPermission("dt.bypassrequireskylight") && (temploc.getWorld().getHighestBlockYAt(temploc) < temploc.getY() || destination.getWorld().getHighestBlockYAt(destination) < destination.getY())) {
            player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequireSkyLight"));
            return;
        }

		// Check if max distance to target is exceeded
		int maxdist = DragonTravelMain.getInstance().getConfig().getInt("MaxTravelDistance");
		
		if(maxdist != -1) {
			if(temploc.distance(destination) >= maxdist) {
				player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Travels.Error.MaxTravelDistanceExceeded"));
				return;
			}
		}
		
        if(destination.getWorld().getName() == player.getWorld().getName()){
            temploc.setYaw(getCorrectYawForPlayer(player, destination));
            player.teleport(temploc);
        }
        else {
            Location temploc2 = new Location(player.getWorld(), player.getLocation().getX()+80, player.getLocation().getY()+80, player.getLocation().getZ()+80);
            temploc.setYaw(getCorrectYawForPlayer(player, temploc2));            
            player.teleport(temploc);
        }
		
		if (!DragonManagement.mount(player, true))
			return;
		
		if (!DragonTravelMain.listofDragonriders.containsKey(player))
			return;
	
		IRyeDragon dragon = DragonTravelMain.listofDragonriders.get(player);
		dragon.setCustomName(ChatColor.translateAlternateColorCodes('&', destName));
        dragon.setTotalDist(Math.hypot(temploc.getBlockX() - destination.getBlockX(), temploc.getBlockZ() - destination.getBlockZ()));
        dragon.setCoveredDist(0);
        ((LivingEntity)dragon.getEntity()).setMaxHealth(60);
		if(destination.getWorld().getName() == player.getWorld().getName())
			dragon.startTravel(destination, false);
		else
			dragon.startTravel(destination, true);
		
	}
}
