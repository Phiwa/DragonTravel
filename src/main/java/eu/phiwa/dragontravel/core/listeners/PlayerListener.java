package eu.phiwa.dragontravel.core.listeners;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import eu.phiwa.dragontravel.core.DragonTravelMain;
import eu.phiwa.dragontravel.core.modules.DragonManagement;
import eu.phiwa.dragontravel.core.movement.Flights;
import eu.phiwa.dragontravel.core.movement.Travels;
import eu.phiwa.dragontravel.core.payment.ChargeType;
import eu.phiwa.dragontravel.core.permissions.PermissionsHandler;
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

    DragonTravelMain plugin;

    public PlayerListener(DragonTravelMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        DragonTravelMain.ptogglers.put(event.getPlayer().getUniqueId(), DragonTravelMain.getInstance().getConfigHandler().isPtoggleDefault());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerKick(PlayerKickEvent event) {

        Player player = event.getPlayer();

        if (!DragonTravelMain.listofDragonriders.containsKey(player))
            return;

        DragonTravelMain.ptogglers.remove(player.getUniqueId().toString());

        DragonManagement.removeRiderandDragon(DragonTravelMain.listofDragonriders.get((player)).getEntity(), false);

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        if (!DragonTravelMain.listofDragonriders.containsKey(player))
            return;

        DragonTravelMain.ptogglers.remove(player.getUniqueId().toString());

        DragonManagement.removeRiderandDragon(DragonTravelMain.listofDragonriders.get((player)).getEntity(), false);
    }

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

        if (lines[1].equals("Travel")) {
            String stationname = lines[2].replaceAll(ChatColor.WHITE.toString(), "");

            if (!PermissionsHandler.hasTravelPermission(player, "travel", stationname)) {
                player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
                return;
            }

            if (stationname.equalsIgnoreCase((DragonTravelMain.getInstance().getConfig().getString("RandomDest.Name")))) {
                if (lines[3].length() != 0) {
                    if (!DragonTravelMain.getInstance().getPaymentManager().chargePlayerCustom(ChargeType.TRAVEL_TORANDOM, player, Double.parseDouble(lines[3]))) {
                        return;
                    }
                } else {
                    if (!DragonTravelMain.getInstance().getPaymentManager().chargePlayer(ChargeType.TRAVEL_TORANDOM, player)) {
                        return;
                    }
                }
                Travels.toRandomdest(player, !DragonTravelMain.getInstance().getConfig().getBoolean("MountingLimit.ExcludeSigns"));
            } else if (DragonTravelMain.getInstance().getDbStationsHandler().getStation(stationname) == null) {
                player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.StationDoesNotExist").replace("{stationname}", stationname));
                return;
            } else {
                if (!DragonTravelMain.getInstance().getPaymentManager().chargePlayerCustom(ChargeType.TRAVEL_TOSTATION, player, Double.parseDouble(lines[3]))) {
                    return;
                }
                Travels.toStation(player, stationname, !DragonTravelMain.getInstance().getConfig().getBoolean("MountingLimit.ExcludeSigns"));
            }
        } else if (lines[1].equals("Faction")) {

            if (Bukkit.getPluginManager().getPlugin("Factions") == null) {
                player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.FactionsNotInstalled"));
                return;
            }

            String factiontag = lines[2].replaceAll(ChatColor.WHITE.toString(), "");

            if (factiontag.isEmpty()) {

                if (!player.hasPermission("dt.ftravel")) {
                    player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
                    return;
                }

                Faction faction = UPlayer.get(player).getFaction();

                if (faction.isNone()) {
                    player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.NoFactionMember"));
                    return;
                }

                if (!faction.hasHome()) {
                    player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.FactionHasNoHome"));
                    return;
                } else
                    Travels.travel(player, faction.getHome().asBukkitLocation(), false, DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToFactionHome"));

            } else {

                if (!player.hasPermission("dt.ftravel")) {
                    player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
                    return;
                }

                Faction faction = UPlayer.get(player).getFaction();

                if (faction.isNone()) {
                    player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.NoFactionMember"));
                    return;
                }

                if (!faction.getName().equals(factiontag)) {
                    // TODO: ADD MESSAGE to other messages-xy.yml
                    player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.NotYourFaction"));
                    return;
                }

                if (!faction.hasHome()) {
                    player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.FactionHasNoHome"));
                    return;
                } else
                    Travels.travel(player, faction.getHome().asBukkitLocation(), false, DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToFactionHome"));
            }
        } else if (lines[1].equals("Flight")) {
            String flightname = lines[2].replaceAll(ChatColor.WHITE.toString(), "");

            if (!PermissionsHandler.hasFlightPermission(player, flightname)) {
                player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
                return;
            }

            if (DragonTravelMain.getInstance().getDbFlightsHandler().getFlight((flightname)) == null) {
                player.sendMessage(DragonTravelMain.getInstance().getMessagesHandler().getMessage("Messages.Flights.Error.FlightDoesNotExist"));
            } else {
                if (lines[3].length() != 0) {
                    if (!DragonTravelMain.getInstance().getPaymentManager().chargePlayerCustom(ChargeType.FLIGHT, player, Double.parseDouble(lines[3]))) {
                        return;
                    }
                } else {
                    if (!DragonTravelMain.getInstance().getPaymentManager().chargePlayer(ChargeType.FLIGHT, player)) {
                        return;
                    }
                }
                Flights.startFlight(player, flightname, !DragonTravelMain.getInstance().getConfig().getBoolean("MountingLimit.ExcludeSigns"), false, null);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        if (player.hasPermission("dt.ignoredamagerestriction"))
            return;

        DragonTravelMain.dmgReceivers.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {

        if (!DragonTravelMain.listofDragonriders.keySet().contains(event.getPlayer()))
            return;

        List<String> commands = (List<String>) DragonTravelMain.getInstance().getConfig().getList("CommandPrevent");

        for (String command : commands) {
            if (command.contains(event.getMessage()))
                event.setCancelled(true);
        }

    }

}