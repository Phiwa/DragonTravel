package eu.phiwa.dt.listeners;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.modules.DragonManagement;
import eu.phiwa.dt.movement.Flights;
import eu.phiwa.dt.movement.Travels;
import eu.phiwa.dt.payment.PaymentHandler;
import eu.phiwa.dt.permissions.PermissionsHandler;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

	DragonTravelMain plugin;

	public PlayerListener(DragonTravelMain plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		DragonTravelMain.ptogglers.put(event.getPlayer().getName(), DragonTravelMain.ptoggleDefault);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerKick(PlayerKickEvent event) {

		Player player = event.getPlayer();

		if (!DragonTravelMain.listofDragonriders.containsKey(player))
			return;

		if (DragonTravelMain.ptogglers.containsKey(player.getName()))
			DragonTravelMain.ptogglers.remove(player.getName());

		DragonManagement.removeRiderandDragon(DragonTravelMain.listofDragonriders.get((player)).getEntity(), false);

	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {

		Player player = event.getPlayer();

		if (!DragonTravelMain.listofDragonriders.containsKey(player))
			return;

		if (DragonTravelMain.ptogglers.containsKey(player.getName()))
			DragonTravelMain.ptogglers.remove(player.getName());

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
				player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
				return;
			}

			if (stationname.equalsIgnoreCase((DragonTravelMain.config.getString("RandomDest.Name")))) {
				if (lines[3].length() != 0) {
					try {
						double costOnSign = Double.parseDouble(lines[3]);
						if (!PaymentHandler.chargePlayerCUSTOMCOST(costOnSign, DragonTravelMain.TRAVEL_TORANDOM, player))
							return;
					} catch (NumberFormatException ex) {
						player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Signs.Error.SignCorrupted"));
						// TODO: ---ADD MESSAGE Corrupted sign
					}
				} else {
					if (!PaymentHandler.chargePlayerNORMAL(DragonTravelMain.TRAVEL_TORANDOM, player))
						return;
				}

				Travels.toRandomdest(player, !DragonTravelMain.config.getBoolean("MountingLimit.ExcludeSigns"));
			}

			else if (DragonTravelMain.dbStationsHandler.getStation(stationname) == null) {
				player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Stations.Error.StationDoesNotExist").replace("{stationname}", stationname));
				// TODO: ---ADD MESSAGE Station does not exist
				return;
			}

			else {
				// If parseDouble throws an exception, the last line is either empty or a string, so the default cost is taken from the config.
				// If parseDouble does not throw an exception, the last line is a double(a valid cost) and is taken for the payment.
				try {
					double costOnSign = Double.parseDouble(lines[3]);

					if (!PaymentHandler.chargePlayerCUSTOMCOST(costOnSign, DragonTravelMain.TRAVEL_TOSTATION, player))
						return;
				} catch (NumberFormatException ex) {
					if (!PaymentHandler.chargePlayerNORMAL(DragonTravelMain.TRAVEL_TOSTATION, player))
						return;
				}
				Travels.toStation(player, stationname, !DragonTravelMain.config.getBoolean("MountingLimit.ExcludeSigns"));
			}
		}

		else if (lines[1].equals("Flight")) {
			String flightname = lines[2].replaceAll(ChatColor.WHITE.toString(), "");

			if (!PermissionsHandler.hasFlightPermission(player, flightname)) {
				player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
				return;
			}

			if (DragonTravelMain.dbFlightsHandler.getFlight((flightname)) == null) {
				player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.FlightDoesNotExist"));
				// TODO: ---ADD MESSAGE Flight does not exist
			}

			else {
				if (lines[3].length() != 0) {

					try {
						double costOnSign = Double.parseDouble(lines[3]);
						if (!PaymentHandler.chargePlayerCUSTOMCOST(costOnSign, DragonTravelMain.FLIGHT, player))
							return;
					} catch (NumberFormatException ex) {
						player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Signs.Error.SignCorrupted"));
						// TODO: ---ADD MESSAGE Corrupted sign
					}
				} else {
					if (!PaymentHandler.chargePlayerNORMAL(DragonTravelMain.FLIGHT, player))
						return;
				}
				Flights.startFlight(player, flightname, !DragonTravelMain.config.getBoolean("MountingLimit.ExcludeSigns"), false, null);
			}
		}
	}
}
