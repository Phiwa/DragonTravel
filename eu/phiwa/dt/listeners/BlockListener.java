package eu.phiwa.dt.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.signs.Signs;


public class BlockListener implements Listener {

	DragonTravelMain plugin;

	public BlockListener(DragonTravelMain plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onMarkerDestroy(BlockBreakEvent event) {
		if (DragonTravelMain.globalwaypointmarkers.containsKey(event.getBlock())) {
			event.getPlayer().sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.CannotDestroyMarkerByHand"));
			// TODO: ---ADD MESSAGE You can't destory a marker by hand, use "/dt remlastwp" instead
			event.setCancelled(true);
		}
	}
	
	
	@EventHandler(priority = EventPriority.LOW)
	public void onSignChange(SignChangeEvent event) {

		Player player = event.getPlayer();

		if (!event.getLine(0).equalsIgnoreCase("[DragonTravel]"))
			return;
		
		if (!player.hasPermission("dt.admin.signs")) {
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
			event.setCancelled(true);
			return;
			// TODO: ---ADD MESSAGE No permission
		}
		
		// FLIGHTSIGNS		
		if(event.getLine(1).equals("Flight")) {		
			if (event.getLine(2).isEmpty()) {	
				player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Signs.Error.NoTargetFlightSpecified"));
				// TODO: ---ADD MESSAGE Please put a flight
			}
			
			if (DragonTravelMain.dbFlightsHandler.getFlight(event.getLine(2)) == null) {
				player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.FlightDoesNotExist"));
				// TODO: ---ADD MESSAGE Flight does not exist
				return;
			}
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Signs.Successful.SignCreated"));
			// TODO: ---ADD MESSAGE Sign created successfully
			Signs.createSign(event, "Flight");
			return;
			
		}
		
		else if(event.getLine(1).equals("Travel")) {		
			if (event.getLine(2).isEmpty()) {	
				player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Signs.Error.NoTargetStationSpecified"));
				// TODO: ---ADD MESSAGE Please put a travel
				return;
			}		
			if (DragonTravelMain.dbStationsHandler.getStation(event.getLine(2)) == null
					&& !event.getLine(2).equalsIgnoreCase( DragonTravelMain.config.getString("RandomDest.Name"))) {	
				player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Stations.Error.StationDoesNotExist").replace("{stationname}", event.getLine(2)));
				// TODO: ---ADD MESSAGE Station does not exist
				return;
			}
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Signs.Successful.SignCreated"));
			// TODO: ---ADD MESSAGE Sign created successfully
			Signs.createSign(event, "Travel");
			return;
		}
		
	}
	
	
	@EventHandler(priority = EventPriority.LOW)
	public void onSignDestroyed(BlockBreakEvent event) {

		if (event.getBlock().getType() != Material.SIGN_POST && event.getBlock().getType() != Material.WALL_SIGN)
			return;

		Sign sign = (Sign) event.getBlock().getState(); 
		String[] lines = sign.getLines();
	
		if (!lines[0].equalsIgnoreCase(ChatColor.GOLD + "DragonTravel"))
			return;
		
		if(!event.getPlayer().hasPermission("dt.admin.signs")) {

			event.getPlayer().sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
			event.setCancelled(true);
			return;
		}
	}
}
