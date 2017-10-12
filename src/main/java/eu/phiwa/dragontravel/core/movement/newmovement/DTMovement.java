package eu.phiwa.dragontravel.core.movement.newmovement;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.movement.DragonType;
import eu.phiwa.dragontravel.core.movement.flight.Flight;
import eu.phiwa.dragontravel.core.movement.flight.Waypoint;
import eu.phiwa.dragontravel.core.movement.travel.Home;
import eu.phiwa.dragontravel.core.movement.travel.Station;

public class DTMovement {

	private String destinationName;
    private List<Waypoint> waypoints;
    private DragonType movementType;


    public DTMovement(String destinationName, List<Waypoint> waypoints) {
    	this.destinationName = destinationName;
        this.waypoints = waypoints;
    }
    
    public String getDestinationName() {
    	return this.destinationName;
    }
    
    public List<Waypoint> getWaypoints() {
    	return this.waypoints;
    }
    
    public DragonType getType() {
    	return movementType;
    }
    
    
    public static DTMovement fromStation(Player player, Station station) {
        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToStation").replace("{stationname}", station.getDisplayName()));       
    	return fromLocation(station.toLocation());
    }
    
    public static DTMovement fromStation(Player player, Station station, CommandSender sender) {
    	sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.SendingPlayer").replace("{playername}", player.getName()).replace("{stationname}", station.getDisplayName()));
        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.SentPlayer").replace("{stationname}", station.getDisplayName()));                
        return fromStation(player, station);
    }
    
    public static DTMovement fromRandom(Player player) {    	
    	
    	// Get limits from random location
    	// TODO: Add config option
    	String worldname = DragonTravel.getInstance().getConfig().getString("RandomDest.World");
        int minX = DragonTravel.getInstance().getConfig().getInt("RandomDest.Limits.X-Axis.Min");
        int maxX = DragonTravel.getInstance().getConfig().getInt("RandomDest.Limits.X-Axis.Max");
        int minZ = DragonTravel.getInstance().getConfig().getInt("RandomDest.Limits.Z-Axis.Min");
        int maxZ = DragonTravel.getInstance().getConfig().getInt("RandomDest.Limits.Z-Axis.Max");

        // Generate random location
        double x = minX + (Math.random() * (maxX - 1));
        double z = minZ + (Math.random() * (maxZ - 1));

        Location randomLoc = new Location(Bukkit.getServer().getWorld(worldname), x, 10, z);
        randomLoc.setY(randomLoc.getWorld().getHighestBlockAt(randomLoc).getY());

        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToRandomLocation"));
        return fromLocation(randomLoc);
    }
    
    public static DTMovement fromRandom(Player player, CommandSender sender) {	
        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.SentPlayer").replace("{stationname}", DragonTravel.getInstance().getConfig().getString("RandomDest.Name")));
        sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.SendingPlayer").replace("{playername}", player.getName()).replace("{stationname}", DragonTravel.getInstance().getConfig().getString("RandomDest.Name")));
        return fromRandom(player);
    }
    
    
    public static DTMovement fromPlayer(Player player, Player targetplayer) {
        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToPlayer").replace("{playername}", targetplayer.getDisplayName()));    	
    	return fromLocation(targetplayer.getLocation());
    }
    
    public static DTMovement fromLocation(Location loc) {       
        List<Waypoint> wpList = new LinkedList<Waypoint>();
    	Waypoint wp = new Waypoint(loc.getWorld().getName(), (int)loc.getX(), (int)loc.getY(), (int)loc.getZ());
    	wpList.add(wp);
    	DTMovement movement = new DTMovement("SomeMovement", wpList);
    	return movement;	
    }
    
    public static DTMovement fromHome(Player player) {
    	// Get player's home
	    Home home = DragonTravel.getInstance().getDbHomesHandler().getHome(player.getUniqueId().toString());
	
	    if (home == null) {
	        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Error.NoHomeSet"));
	        return null;
	    }
	    
	    player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToHome"));
	    return fromLocation(home.toLocation());
    }
    
    public static DTMovement fromFaction(Player player) {
    	// Check if factions is installed
	    if (Bukkit.getPluginManager().getPlugin("Factions") == null) {
	        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.FactionsNotInstalled"));
	        return null;
	    }
	
        // Check if "RequireItem" is enabled
        if (DragonTravel.getInstance().getConfigHandler().isRequireItemTravelTownSpawn()) {
        	
        	// Check if player has required item
            if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                return null;
            }
        }
	
        // Check if player has a faction
        if(!MPlayer.get(player).hasFaction()) {
        	player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.NoFactionMember"));
    		return null;
        }
        
	    // Get player's faction
	    Faction faction = MPlayer.get(player).getFaction();
	
	    // Check if faction has a home
	    if (!faction.hasHome()) {
	        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.FactionHasNoHome"));
	        return null;
	    }
	    
        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToFactionHome"));
//	    travel(player, faction.getHome().asBukkitLocation(), checkForStation, DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToFactionHome"), DragonType.FACTION_TRAVEL, null);
        return fromLocation(faction.getHome().asBukkitLocation());
    }
    
    public static DTMovement fromTown(Player player) { 	       
        // Check if Towny is installed
        if (Bukkit.getPluginManager().getPlugin("Towny") == null) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Towny.Error.TownyNotInstalled"));
            return null;
        }
        
        // Check if "RequireItem" is enabled
        if (DragonTravel.getInstance().getConfigHandler().isRequireItemTravelTownSpawn()) {
        	
        	// Check if player has required item
            if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                return null;
            }
        }
        
        // Check if player is a resident
    	Resident res = null;
        
        try {
			res = TownyUniverse.getDataSource().getResident(player.getName());
		} catch (Exception e1) {}
        
        if(res == null) {
        	player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Towny.Error.NoTown"));
        	return null;
        }
        
        // Get player's town
        Town town = null;       

      	try {
            town = res.getTown();         
		} catch (Exception e) {}
      	
      	if(town == null){
      		player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Towny.Error.NoTown"));
        	return null;
      	}
      	
      	// Get town's spawn
        Location tspawn = null;
        
  		try {
			tspawn = town.getSpawn();
		} catch (Exception e) {}
       
  		if(tspawn == null){
      		player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Towny.Error.NoTown"));
        	return null;
      	}
  		
    	player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToTownSpawn"));
//        travel(player, tspawn, checkForStation, DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToTownSpawn"), DragonType.FACTION_TRAVEL, null);        	
        return fromLocation(tspawn);
    }
    
    public static DTMovement fromFlight(Flight flight) {
    	DTMovement movement = new DTMovement(flight.getDisplayName(), flight.getWaypoints());
    	return movement;
    }
	
    public static DTMovement fromWaypoints(String displayname, List<Waypoint> waypoints) {
    	DTMovement movement = new DTMovement(displayname, waypoints);
    	return movement;
    }
    
}
