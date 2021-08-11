package eu.phiwa.dragontravel.core.listeners;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import eu.phiwa.dragontravel.api.DragonException;
import eu.phiwa.dragontravel.core.DragonManager;
import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.hooks.payment.ChargeType;
import eu.phiwa.dragontravel.core.hooks.permissions.PermissionsHandler;
import eu.phiwa.dragontravel.core.movement.DragonType;
import eu.phiwa.dragontravel.nms.CompatibilityUtils;
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
import java.util.logging.Level;


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

        if (!CompatibilityUtils.typeIsSign(block.getType()))
            return;

        Sign sign = (Sign) block.getState();
        String[] lines = sign.getLines();

        if (!lines[0].equals(ChatColor.GOLD.toString() + "DragonTravel"))
            return;

        switch (lines[1]) {
            case "Travel":
                String stationname = lines[2].replaceAll(ChatColor.WHITE.toString(), "");

                if (!PermissionsHandler.hasTravelPermission(player, "travel", stationname)) {
                    player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
                    return;
                }

                if (stationname.equalsIgnoreCase((DragonTravel.getInstance().getConfig().getString("RandomDest.Name")))) {
                    if (lines[3].length() != 0) {
                        if (!DragonTravel.getInstance().getPaymentManager().chargePlayerCustom(ChargeType.TRAVEL_TORANDOM, player, Double.parseDouble(lines[3]))) {
                            return;
                        }
                    } else {
                        if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.TRAVEL_TORANDOM, player)) {
                            return;
                        }
                    }
                    try {
                        DragonManager.getDragonManager().getTravelEngine().toRandomDest(player, !DragonTravel.getInstance().getConfig().getBoolean("MountingLimit.ExcludeSigns"), null);
                    } catch (DragonException e) {
                        e.printStackTrace();
                    }
                } else if (DragonTravel.getInstance().getDbStationsHandler().getStation(stationname) == null) {
                    player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.StationDoesNotExist").replace("{stationname}", stationname));
                    return;
                } else {
                	
                	if(lines[3].isEmpty()) {
                		if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.TRAVEL_TOSTATION, player)) {
                			return;
                		}
                	}
                	else {
	                    if (!DragonTravel.getInstance().getPaymentManager().chargePlayerCustom(ChargeType.TRAVEL_TOSTATION, player, Double.parseDouble(lines[3]))) {
	                        return;
	                    }
                	}
                    try {
                        DragonManager.getDragonManager().getTravelEngine().toStation(player, stationname, !DragonTravel.getInstance().getConfig().getBoolean("MountingLimit.ExcludeSigns"), null);
                    } catch (DragonException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "Faction":

                if (Bukkit.getPluginManager().getPlugin("Factions") == null) {
                    player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.FactionsNotInstalled"));
                    return;
                }

                String factiontag = lines[2].replaceAll(ChatColor.WHITE.toString(), "");

                if (factiontag.isEmpty()) {

                    if (!player.hasPermission("dt.ftravel")) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
                        return;
                    }

                    Faction faction = MPlayer.get(player).getFaction();

                    if (faction.isNone()) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.NoFactionMember"));
                        return;
                    }

                    if (!faction.hasHome()) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.FactionHasNoHome"));
                        return;
                    } else
                        try {
                            DragonManager.getDragonManager().getTravelEngine().travel(player, faction.getHome().asBukkitLocation(), false, DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToFactionHome"), DragonType.FACTION_TRAVEL, null);
                        } catch (DragonException e) {
                            e.printStackTrace();
                        }
                } else {

                    if (!player.hasPermission("dt.ftravel")) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
                        return;
                    }

                    Faction faction = MPlayer.get(player).getFaction();

                    if (faction.isNone()) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.NoFactionMember"));
                        return;
                    }

                    if (!faction.getName().equals(factiontag)) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.NotYourFaction"));
                        return;
                    }

                    if (!faction.hasHome()) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.FactionHasNoHome"));
                        return;
                    } else
                        try {
                            DragonManager.getDragonManager().getTravelEngine().travel(player, faction.getHome().asBukkitLocation(), false, DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToFactionHome"), DragonType.FACTION_TRAVEL, null);
                        } catch (DragonException e) {
                            e.printStackTrace();
                        }
                }
                break;
            case "Flight":
                String flightName = lines[2].replaceAll(ChatColor.WHITE.toString(), "");

                if (!PermissionsHandler.hasFlightPermission(player, flightName)) {
                    player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
                    return;
                }

                if (DragonTravel.getInstance().getDbFlightsHandler().getFlight((flightName)) == null) {
                    player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Error.FlightDoesNotExist"));
                } else {
                    if (lines[3].length() != 0) {
                        if (!DragonTravel.getInstance().getPaymentManager().chargePlayerCustom(ChargeType.FLIGHT, player, Double.parseDouble(lines[3]))) {
                            return;
                        }
                    } else {
                        if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.FLIGHT, player)) {
                            return;
                        }
                    }
                    try {
                        DragonManager.getDragonManager().getFlightEngine().startFlight(player, flightName, !DragonTravel.getInstance().getConfig().getBoolean("MountingLimit.ExcludeSigns"), null);
                    } catch (DragonException e) {
                        e.printStackTrace();
                    }
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
        // If a player received fall damage, this might be fall damage caused by the dismount process
        if(event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            // If the player received fall damage, check if he just dismounted
            if (DragonManager.getDragonManager().isInDismountedList(player)) {
                // If it was dismount damage, prevent it
                event.setCancelled(true);
                DragonManager.getDragonManager().removeFromDismountedList(player);
            }
        }

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
            if(event.getMessage().contains(command)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.CommandPrevented"));
                Bukkit.getLogger().log(Level.INFO, "[DragonTravel] Player '" + event.getPlayer().getDisplayName() + "' tried to use the command '"
                                       + event.getMessage() + "' while riding a dragon. The command was cancelled.");
            }
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