package eu.phiwa.dragontravel.core.listeners;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.hooks.payment.ChargeType;
import eu.phiwa.dragontravel.core.hooks.permissions.PermissionsHandler;
import eu.phiwa.dragontravel.core.movement.flight.Flights;
import eu.phiwa.dragontravel.core.movement.travel.Travels;
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        DragonTravel.getInstance().getDragonManager().getPlayerToggles().put(event.getPlayer().getUniqueId(), DragonTravel.getInstance().getConfigHandler().isPtoggleDefault());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerKick(PlayerKickEvent event) {

        Player player = event.getPlayer();

        if (!DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player))
            return;

        DragonTravel.getInstance().getDragonManager().getPlayerToggles().remove(player.getUniqueId());
        DragonTravel.getInstance().getDragonManager().removeRiderAndDragon(DragonTravel.getInstance().getDragonManager().getRiderDragons().get((player)).getEntity(), false);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        if (!DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player))
            return;

        DragonTravel.getInstance().getDragonManager().getPlayerToggles().remove(player.getUniqueId());

        DragonTravel.getInstance().getDragonManager().removeRiderAndDragon(DragonTravel.getInstance().getDragonManager().getRiderDragons().get((player)).getEntity(), false);
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
                    Travels.toRandomdest(player, !DragonTravel.getInstance().getConfig().getBoolean("MountingLimit.ExcludeSigns"));
                } else if (DragonTravel.getInstance().getDbStationsHandler().getStation(stationname) == null) {
                    player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.StationDoesNotExist").replace("{stationname}", stationname));
                    return;
                } else {
                    if (!DragonTravel.getInstance().getPaymentManager().chargePlayerCustom(ChargeType.TRAVEL_TOSTATION, player, Double.parseDouble(lines[3]))) {
                        return;
                    }
                    Travels.toStation(player, stationname, !DragonTravel.getInstance().getConfig().getBoolean("MountingLimit.ExcludeSigns"));
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

                    Faction faction = UPlayer.get(player).getFaction();

                    if (faction.isNone()) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.NoFactionMember"));
                        return;
                    }

                    if (!faction.hasHome()) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.FactionHasNoHome"));
                        return;
                    } else
                        Travels.travel(player, faction.getHome().asBukkitLocation(), false, DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToFactionHome"));

                } else {

                    if (!player.hasPermission("dt.ftravel")) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
                        return;
                    }

                    Faction faction = UPlayer.get(player).getFaction();

                    if (faction.isNone()) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.NoFactionMember"));
                        return;
                    }

                    if (!faction.getName().equals(factiontag)) {
                        // TODO: ADD MESSAGE to other messages-xy.yml
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.NotYourFaction"));
                        return;
                    }

                    if (!faction.hasHome()) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.FactionHasNoHome"));
                        return;
                    } else
                        Travels.travel(player, faction.getHome().asBukkitLocation(), false, DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToFactionHome"));
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
                    Flights.startFlight(player, flightName, !DragonTravel.getInstance().getConfig().getBoolean("MountingLimit.ExcludeSigns"), false, null);
                }
                break;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        if (player.hasPermission("dt.ignoredamagerestriction"))
            return;

        DragonTravel.getInstance().getDragonManager().getDamageReceipts().put(player.getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (!DragonTravel.getInstance().getDragonManager().getRiderDragons().keySet().contains(event.getPlayer()))
            return;
        List<String> commands = (List<String>) DragonTravel.getInstance().getConfig().getList("CommandPrevent");
        commands.stream().filter(command -> command.contains(event.getMessage())).forEach(command -> event.setCancelled(true));
    }

}