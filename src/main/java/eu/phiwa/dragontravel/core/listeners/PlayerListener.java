package eu.phiwa.dragontravel.core.listeners;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;

import eu.phiwa.dragontravel.api.DragonException;
import eu.phiwa.dragontravel.core.DragonManager;
import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.hooks.payment.ChargeType;
import eu.phiwa.dragontravel.core.hooks.permissions.PermissionsHandler;
import eu.phiwa.dragontravel.core.movement.DragonType;
import eu.phiwa.dragontravel.core.movement.flight.Flight;
import eu.phiwa.dragontravel.core.movement.newmovement.DTMovement;
import eu.phiwa.dragontravel.core.movement.travel.Station;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

import java.util.List;


public class PlayerListener implements Listener {

	/** 
	 * Sets player-specific options to default values
	 * 
	 * @param event
	 */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        DragonTravel.getInstance().getDragonManager().getPlayerToggles().put(event.getPlayer().getUniqueId(), DragonTravel.getInstance().getConfigHandler().isPtoggleDefault());
    }

    /** 
     * Dismounts players leaving the server (being kicked)
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerKick(PlayerKickEvent event) {

        Player player = event.getPlayer();

        if (!DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player))
            return;

        DragonTravel.getInstance().getDragonManager().getPlayerToggles().remove(player.getUniqueId());
        DragonTravel.getInstance().getDragonManager().removeRiderAndDragon(DragonTravel.getInstance().getDragonManager().getRiderDragons().get((player)).getEntity(), false);
    }

    /** 
     * Dismounts players leaving the server
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        if (!DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player))
            return;

        DragonTravel.getInstance().getDragonManager().getPlayerToggles().remove(player.getUniqueId());

        DragonTravel.getInstance().getDragonManager().removeRiderAndDragon(DragonTravel.getInstance().getDragonManager().getRiderDragons().get((player)).getEntity(), false);
    }

    /** 
     * Handles players clicking signs 
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null)
            return;

        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_AIR))
            return;

        if (block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN)
            return;

        Sign sign = (Sign) block.getState();
        String[] lines = sign.getLines();

        if (!lines[0].equals(ChatColor.GOLD.toString() + "DragonTravel"))
            return;

        switch (lines[1]) {
            case "Travel":
                String stationname = lines[2].replaceAll(ChatColor.WHITE.toString(), "");

                // Player does not have the permission to use this function
                if (!PermissionsHandler.hasTravelPermission(player, "travel", stationname)) {
                    player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
                    return;
                }
                
                // Check for mounting limit
                if (DragonTravel.getInstance().getConfig().getBoolean("MountingLimit.EnableForTravels") && !player.hasPermission("dt.ignoreusestations.travels")) {

                	// Do not check mounting limit if signs are excluded in config
                	if(!DragonTravel.getInstance().getConfig().getBoolean("MountingLimit.ExcludeSigns")) {
                		
	                	// Player is not at a station
	                    if (!DragonTravel.getInstance().getDbStationsHandler().checkForStation(player)) {
	                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.NotAtAStation"));
	                        return;
	                    }
                	}
                }

                // Travelling to random destination
                if (stationname.equalsIgnoreCase((DragonTravel.getInstance().getConfig().getString("RandomDest.Name")))) {
                	
                	// Check if "RequireItem" is enabled
                    if (DragonTravel.getInstance().getConfigHandler().isRequireItemTravelStation()) {
                    	
                    	// Check if player has required item
                        if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
                            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                            return;
                        }
                    }
                	
                	// If costs are provided on sign, use this amount
                    if (lines[3].isEmpty()) {
                    	
                    	// Charge palyer custom amount on sign
                        if(!DragonTravel.getInstance().getPaymentManager().chargePlayerCustom(ChargeType.TRAVEL_TORANDOM, player, Double.parseDouble(lines[3])))
                            return;
                    }
                    // No costs provided on sign
                    else {
                    	
                    	// Charge player normal amount
                        if(!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.TRAVEL_TORANDOM, player))
                            return;
                    }

                    try {
                    	DTMovement movement = DTMovement.fromRandom(player);
                        DragonManager.getDragonManager().getMovementEngine().startMovement(player, movement);
                    } catch (DragonException e) {
                        e.printStackTrace();
                    }
                }
                // Trying to travel to a mnormal station, but it does not exist
                else if (DragonTravel.getInstance().getDbStationsHandler().getStation(stationname) == null) {
                    player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.StationDoesNotExist").replace("{stationname}", stationname));
                    return;
                }
                // Travelling to a normal station
                else { 	
                	Station station = DragonTravel.getInstance().getDbStationsHandler().getStation(stationname);
                	
                	// Station does not exist
                	if(station == null) {
                		player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.StationDoesNotExist").replace("{stationname}", stationname));
                		return;
                	}
                	
                	// Check if "RequireItem" is enabled
                    if (DragonTravel.getInstance().getConfigHandler().isRequireItemTravelStation()) {
                    	
                    	// Check if player has required item
                        if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
                            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                            return;
                        }
                    }
                    
                	// No costs provided on sign, use default amount
                	if(lines[3].isEmpty()) {
                		
                		// Charge player normal amount
                		if(!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.TRAVEL_TOSTATION, player))
                			return;
                	}
                	// If costs are provided on sign, use this amount
                	else {
                		
                		// Charge player custom amount on sign
	                    if(!DragonTravel.getInstance().getPaymentManager().chargePlayerCustom(ChargeType.TRAVEL_TOSTATION, player, Double.parseDouble(lines[3])))
	                        return;
                	}
                	
                    try {
                    	DTMovement movement = DTMovement.fromStation(player, station);
                    	DragonManager.getDragonManager().getMovementEngine().startMovement(player, movement);
                    } catch (DragonException e) {
                        e.printStackTrace();
                    }
                }
                
                break;
            case "Faction":

            	// Check if Factions is loaded
                if (Bukkit.getPluginManager().getPlugin("Factions") == null) {
                    player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.FactionsNotInstalled"));
                    return;
                }

                // Player does not have the permission to use this function
                if (!player.hasPermission("dt.ftravel")) {
                    player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
                    return;
                }
                
                // Reformat sign code to be able parse it
                String factiontag = lines[2].replaceAll(ChatColor.WHITE.toString(), "");

                Faction faction = null;
                
                // No faction tag is specified, use the player's faction
                if (factiontag.isEmpty()) {

                	// Get player's faction
                    faction = MPlayer.get(player).getFaction();

                    // Player has no faction
                    if (faction.isNone()) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.NoFactionMember"));
                        return;
                    }

                    // Player's faction has no home
                    if (!faction.hasHome()) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.FactionHasNoHome"));
                        return;
                    }
                        
                }
                // Faction tag specified on sign, use this faction
                else {

                	// Get player's faction
                    faction = MPlayer.get(player).getFaction();

                    // Player has no faction
                    if (faction.isNone()) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.NoFactionMember"));
                        return;
                    }

                    // Player's faction is not the one specified on the sign
                    if (!faction.getName().equals(factiontag)) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.NotYourFaction"));
                        return;
                    }

                    // Specified faction has no home
                    if (!faction.hasHome()) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.FactionHasNoHome"));
                        return;
                    }
                }
                
                // Check if player is already riding a dragon
                if (DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player)) {
                	player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.AlreadyMounted"));
                    return;
                }
                
                // Check for mounting limit
                if (DragonTravel.getInstance().getConfig().getBoolean("MountingLimit.EnableForTravels") && !player.hasPermission("dt.ignoreusestations.travels")) {

                	// Player is not at a station
                    if (!DragonTravel.getInstance().getDbStationsHandler().checkForStation(player)) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.NotAtAStation"));
                        return;
                    }
                }
                
                // Check if "RequireItem" is enabled
                if (DragonTravel.getInstance().getConfigHandler().isRequireItemTravelFactionhome()) {
                	
                	// Check if player has required item
                    if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                        return;
                    }
                }               
                
                // No costs provided on sign, use default amount
                if (lines[3].isEmpty()) {
                	
                	// Charge player normal amount
                	if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.TRAVEL_TOFACTIONHOME, player))
                        return;
                }
                // If costs are provided on sign, use this amount
                else {
                    
                	// Charge player custom amount on sign
                    if (!DragonTravel.getInstance().getPaymentManager().chargePlayerCustom(ChargeType.TRAVEL_TOFACTIONHOME, player, Double.parseDouble(lines[3])))
                        return;
                }
                
                try {
                	DTMovement movement = DTMovement.fromFaction(player);
                	DragonManager.getDragonManager().getMovementEngine().startMovement(player, movement);
                } catch (DragonException e) {
                    e.printStackTrace();
                }

                break;
            case "Flight":
                String flightName = lines[2].replaceAll(ChatColor.WHITE.toString(), "");

                // Player does not have the permission to use this function
                if (!PermissionsHandler.hasFlightPermission(player, flightName)) {
                    player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
                    return;
                }

                Flight flight = DragonTravel.getInstance().getDbFlightsHandler().getFlight((flightName));
                
                // Flight does not exist
                if (flight == null) {
                    player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Error.FlightDoesNotExist"));
                    return;
                }
                
                // Check if player is already riding a dragon
                if (DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player)) {
                	player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.AlreadyMounted"));
                    return;
                }
                
                // Check for mounting limit
                if (DragonTravel.getInstance().getConfig().getBoolean("MountingLimit.EnableForFlights") && !player.hasPermission("dt.ignoreusestations.flights")) {

                	// Player is not at a station
                    if (!DragonTravel.getInstance().getDbStationsHandler().checkForStation(player)) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.NotAtAStation"));
                        return;
                    }
                }

                // Check if "RequireItem" is enabled
                if (DragonTravel.getInstance().getConfigHandler().isRequireItemFlight()) { 
                	
                	// Check if player has required item
                    if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.flight")) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                        return;
                    }
                }
                
                // No costs provided on sign, use default amount
                if (lines[3].isEmpty()) {
                    
                	// Charge player normal amount
                	if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.FLIGHT, player))
                        return;
                }
                // If costs are provided on sign, use this amount
                else {

                	// Charge player custom amount on sign
                    if (!DragonTravel.getInstance().getPaymentManager().chargePlayerCustom(ChargeType.FLIGHT, player, Double.parseDouble(lines[3])))
                        return;
                }
                
                try {
                	DTMovement movement = DTMovement.fromFlight(flight);
                	DragonManager.getDragonManager().getMovementEngine().startMovement(player, movement);
                } catch (DragonException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    /** 
     * Prevents players who received damage from escaping by dragon
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        if (player.hasPermission("dt.ignoredamagerestriction"))
            return;

        DragonTravel.getInstance().getDragonManager().getDamageReceipts().put(player.getUniqueId(), System.currentTimeMillis());
    }

    /** 
     * Prevents dragon riders from using commands specified in config
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (!DragonTravel.getInstance().getDragonManager().getRiderDragons().keySet().contains(event.getPlayer()))
            return;
        List<String> commands = DragonTravel.getInstance().getConfig().getStringList("CommandPrevent");
        for(String command : commands)
            if(command.contains(event.getMessage()))
                event.setCancelled(true);
    }

    /** 
     * Prevents riders from picking up items while flying
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemPick(PlayerPickupItemEvent event) {
    	
        Player player = event.getPlayer();
        
        if (DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player))
            event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerDismount(PlayerToggleSneakEvent event) {    	
        if(!DragonTravel.getInstance().getDragonManager().getRiderDragons().keySet().contains(event.getPlayer())){
        	return;
        }
        Player player = event.getPlayer();
        if(DragonTravel.getInstance().getConfigHandler().isDismountOnShift()){
        	DragonTravel.getInstance().getDragonManager().dismount(player, false);
        }
    }
}