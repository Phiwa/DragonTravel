package eu.phiwa.dragontravel.core.listeners;

import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.nms.CompatibilityUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

public class BlockListener implements Listener {

	@EventHandler
	public void onMarkerDestroy(BlockBreakEvent event) {
        if (DragonTravel.getInstance().getFlightEditor().getWayPointMarkers().containsKey(event.getBlock())) {
            event.getPlayer().sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Error.CannotDestroyMarkerByHand"));
            event.setCancelled(true);
        }
    }

	@EventHandler(priority = EventPriority.LOW)
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();

		if (!event.getLine(0).equalsIgnoreCase("[DragonTravel]"))
			return;

		if (!player.hasPermission("dt.admin.signs")) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
            event.setCancelled(true);
            return;
        }

		if (event.getLine(1).equals("Flight")) {
			if (event.getLine(2).isEmpty()) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Signs.Error.NoTargetFlightSpecified"));
                return;
            }

            if (DragonTravel.getInstance().getDbFlightsHandler().getFlight(event.getLine(2)) == null) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Error.FlightDoesNotExist"));
                return;
            }

            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Signs.Successful.SignCreated"));
            createSign(event, "Flight");
        } else if (event.getLine(1).equals("Travel")) {
            if (event.getLine(2).isEmpty()) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Signs.Error.NoTargetStationSpecified"));
                return;
            }
            if (DragonTravel.getInstance().getDbStationsHandler().getStation(event.getLine(2)) == null
                    && !event.getLine(2).equalsIgnoreCase(DragonTravel.getInstance().getConfig().getString("RandomDest.Name"))) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.StationDoesNotExist").replace("{stationname}", event.getLine(2)));
                return;
            }

            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Signs.Successful.SignCreated"));
            createSign(event, "Travel");
        } else if (event.getLine(1).equals("Faction")) {

			if (Bukkit.getPluginManager().getPlugin("Factions") == null) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.FactionsNotInstalled"));
                return;
            }

            createSign(event, "Faction");
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Signs.Successful.SignCreated"));
        }
    }

    private void createSign(SignChangeEvent event, String type) {
        event.setLine(0, ChatColor.GOLD + "DragonTravel");
        event.setLine(1, type);
        event.setLine(2, ChatColor.WHITE + event.getLine(2));
        event.setLine(3, event.getLine(3));
    }

	@EventHandler(priority = EventPriority.LOW)
	public void onSignDestroyed(BlockBreakEvent event) {
        if (!CompatibilityUtils.typeIsSign(event.getBlock().getType()))
            return;

		Sign sign = (Sign) event.getBlock().getState();
		String[] lines = sign.getLines();

		if (!lines[0].equalsIgnoreCase(ChatColor.GOLD + "DragonTravel"))
			return;

		if (!event.getPlayer().hasPermission("dt.admin.signs")) {
            event.getPlayer().sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
            event.setCancelled(true);
        }
    }
}
