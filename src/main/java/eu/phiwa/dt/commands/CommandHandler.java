package eu.phiwa.dt.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.Flight;
import eu.phiwa.dt.Home;
import eu.phiwa.dt.Station;
import eu.phiwa.dt.flights.FlightEditor;
import eu.phiwa.dt.flights.Waypoint;
import eu.phiwa.dt.modules.DragonManagement;
import eu.phiwa.dt.modules.StationaryDragon;
import eu.phiwa.dt.movement.Flights;
import eu.phiwa.dt.movement.Travels;
import eu.phiwa.dt.payment.PaymentHandler;
import eu.phiwa.dt.permissions.PermissionsHandler;

public class CommandHandler implements CommandExecutor {
	DragonTravelMain plugin;

	public CommandHandler(DragonTravelMain plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {

		// Length of the commands
		int length = args.length;


		// Return if not right length
		if (length < 1) {
			if (sender instanceof ConsoleCommandSender)
				sendUsage(sender);
			else if (sender instanceof Player)
				sendUsage((Player) sender, 1);
			// TODO: ---ADD MESSAGE Command-Help
			return false;
		}

		String command = args[0]; // e.g. "dismount"/"remdragons"/"travel"/"sethome"/...

		String argument1;
		String argument2;
		String argument3;
		String argument4;

		/* Console Command Execution */
		if (sender instanceof ConsoleCommandSender) {

			switch (length) {

			case 1:

				if (command.equalsIgnoreCase("reload")) {
					plugin.reload();
					return true;
				}

				else if (command.equalsIgnoreCase("showstats")) {
					DragonTravelMain.dbStationsHandler.showStations();
					return true;
				}

				else if (command.equalsIgnoreCase("showflights")) {
					DragonTravelMain.dbFlightsHandler.showFlights();
					return true;
				}

				return false;

			case 2:
				argument1 = args[1];


				if (command.equalsIgnoreCase("remdragons")) {
					if (argument1 == "all") {
						for (World world : Bukkit.getWorlds()) {
							sender.sendMessage("[DragonTravel] " + DragonManagement.removeDragons(world));
						}
						return true;
					} else {
						World world = Bukkit.getWorld(argument1);
						if (world != null) {
							sender.sendMessage("[DragonTravel] " + DragonManagement.removeDragons(world));
							return true;
						} else {
							sender.sendMessage("[DragonTravel][Error] World does not exist.");
							return false;
						}
					}
				}

				else if (command.equalsIgnoreCase("remflight")) {

					// TODO: Permission-Check

					if (DragonTravelMain.dbFlightsHandler.getFlight(argument1) == null) {
						sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.FlightDoesNotExist"));
						// TODO: ---ADD MESSAGE There is no flight with that name
						return false;
					}

					DragonTravelMain.dbFlightsHandler.deleteFlight(argument1);

					sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Successful.RemovedFlight"));
					// TODO: ---ADD MESSAGE Successfully removed the flight
					return true;
				}

				return false;

			case 3:
				argument1 = args[1];
				argument2 = args[2];

				if (command.equalsIgnoreCase("flight")) {
					Player player = Bukkit.getPlayer(argument2);
					if (player == null) {
						System.out.println("[DragonTravel] Couldn't find player '" + argument2 + "'!");
						return false;
					}
					Flights.startFlight(player, argument1, false, true, null);
					return true;
				}

				return false;

			case 4:
				argument1 = args[1];
				argument2 = args[2];
				argument3 = args[3];


				return false;

			case 5:
				argument1 = args[1];
				argument2 = args[2];
				argument3 = args[3];
				argument4 = args[4];


				return false;

			default:
				// TODO: ADD MESSAGE Commandhelp for Console
				return false;
			}
		}

		Player player = (Player) sender;

		/* Player Command Execution */

		switch (length) {

		case 1:

			if (command.equalsIgnoreCase("reload")) {
				if (player.hasPermission("dt.admin.*")) {
					player.sendMessage(ChatColor.RED + "We recommend not to reload your files this way. It is safer to restart your server!");
					plugin.reload();
					player.sendMessage(ChatColor.GREEN + "Reloaded files");
					return true;
				} else {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
					return true;
				}


			}

			else if (command.equalsIgnoreCase("help")) {
				sendUsage(player, 1);
				// TODO: ---ADD MESSAGE Help
				return true;
			}

			else if (command.equalsIgnoreCase("statdragon")) {
				if (player.hasPermission("dt.admin.statdragon")) {
					StationaryDragon.createStatDragon(player);
					return true;
				} else {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
					return false;
				}
			}

			else if (command.equalsIgnoreCase("dismount")) {
				DragonManagement.dismount(player, false);
				return true;
			}

			else if (command.equalsIgnoreCase("remdragons")) {
				if (player.hasPermission("dt.admin.remdragon")) {
					DragonManagement.removeDragons(player.getWorld());
					return true;
				} else {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
					return false;
				}
			}

			else if (command.equalsIgnoreCase("sethome")) {
				if (player.hasPermission("dt.home")) {
					if (DragonTravelMain.usePayment)
						if (!PaymentHandler.chargePlayerNORMAL(DragonTravelMain.SETHOME, player))
							return false;

					if (DragonTravelMain.dbHomesHandler.getHome(player.getName()) != null)
						DragonTravelMain.dbHomesHandler.deleteHome(player.getName());
					Home home = new Home(player.getLocation());
					DragonTravelMain.dbHomesHandler.saveHome(player.getName(), home);
					return true;
				} else {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
					return false;
				}
			}

			else if (command.equalsIgnoreCase("home")) {
				if (player.hasPermission("dt.home")) {
					if (DragonTravelMain.usePayment)
						if (!PaymentHandler.chargePlayerNORMAL(DragonTravelMain.TRAVEL_TOHOME, player))
							return false;
					Travels.toHome(player, true);
					return true;
				} else {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
					return false;
				}
			}

			else if (command.equalsIgnoreCase("fhome")) {
				if (player.hasPermission("dt.fhome")) {
					if (DragonTravelMain.usePayment)
						if (!PaymentHandler.chargePlayerNORMAL(DragonTravelMain.TRAVEL_TOFACTIONHOME, player))
							return false;
					Travels.toFactionhome(player, true);
					return true;
				} else {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
					return false;
				}
			}

			else if (command.equalsIgnoreCase("showstats")) {
				DragonTravelMain.dbStationsHandler.showStations(player);
				return true;
			}

			else if (command.equalsIgnoreCase("showflights")) {
				DragonTravelMain.dbFlightsHandler.showFlights(player);
				return true;
			}

			else if (command.equalsIgnoreCase("saveflight")) {
				if (player.hasPermission("dt.admin.flights")) {
					if (!FlightEditor.editors.containsKey(player)) {
						player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.NotInFlightCreationMode"));
						// TODO: ---ADD MESSAGE You are not in Flight-creation mode
						return false;
					}

					if (FlightEditor.editors.get(player).wpcount < 1) {
						player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.AtLeastOneWaypoint"));
						// TODO: ---ADD MESSAGE You need to create at least one waypoint
						return false;
					}

					DragonTravelMain.dbFlightsHandler.createFlight(FlightEditor.editors.get(player));
					Waypoint.removeWaypointMarkersOfFlight(FlightEditor.editors.get(player));
					FlightEditor.editors.remove(player);

					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Successful.FlightSaved"));
					// TODO: ---ADD MESSAGE Successfully saved the flight
					return true;
				} else {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
					return false;
				}
			}

			else if (command.equalsIgnoreCase("setwp")) {
				if (player.hasPermission("dt.admin.flights")) {
					// TODO: Permission-Check

					if (!FlightEditor.editors.containsKey(player)) {
						player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.NotInFlightCreationMode"));
						// TODO: ---ADD MESSAGE You are not in Flight-creation mode
						return false;
					}

					Flight flight = FlightEditor.editors.get(player);
					Waypoint wp = new Waypoint();
					Location loc = player.getLocation();
					wp.x = (int) loc.getX();
					wp.y = (int) loc.getY();
					wp.z = (int) loc.getZ();

					// Create a marker at the waypoint
					wp.setMarker(player);
					Block block = player.getLocation().getBlock();
					DragonTravelMain.globalwaypointmarkers.put(block, block);

					flight.addWaypoint(wp);

					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Successful.WaypointAdded"));
					// TODO: ---ADD MESSAGE Successfully added a waypoint
					return true;
				} else {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
					return false;
				}
			}

			else if (command.equalsIgnoreCase("remlastwp")) {
				if (player.hasPermission("dt.admin.flights")) {
					// TODO: Permission-Check

					if (!FlightEditor.editors.containsKey(player)) {
						player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.NotInFlightCreationMode"));
						// TODO: ---ADD MESSAGE You are not in Flight-creation mode
						return false;
					}

					FlightEditor.editors.get(player).removelastWaypoint();

					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Successful.WaypointRemoved"));
					// TODO: ---ADD MESSAGE Successfully removed the last waypoint.
					return true;
				} else {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
					return false;
				}
			}

			else if (command.equalsIgnoreCase("ptoggle")) {

				if (!player.hasPermission("dt.ptoggle")) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
					return false;
				}

				if (DragonTravelMain.ptogglers.get(player.getName())) {
					DragonTravelMain.ptogglers.put(player.getName(), false);
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Successful.ToggledPTravelOff"));
					return true;
				} else {
					DragonTravelMain.ptogglers.put(player.getName(), true);
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Successful.ToggledPTravelOn"));
					return true;
				}
			}

			sendUsage(player, 1);
			return false;

		case 2:
			argument1 = args[1];

			if (command.equalsIgnoreCase("help")) {
				try {
					int page = Integer.parseInt(argument1);
					sendUsage(player, page);
				} catch (NumberFormatException ex) {
					player.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/dt help <page> " + ChatColor.RED + "to open the help-section.");
				}
			} else if (command.equalsIgnoreCase("travel")) {


				if (DragonTravelMain.onlysigns) {
					if (!player.hasPermission("dt.*")) {
						// TODO: ---ADD MESSAGE This command has been disabled by the admin, you can only travel using signs
						player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Travels.Error.OnlySigns"));
						return false;
					}
				}

				if (argument1.equalsIgnoreCase((DragonTravelMain.config.getString("RandomDest.Name")))) {
					if (PermissionsHandler.hasTravelPermission(player, "travel", argument1)) {
						if (DragonTravelMain.usePayment)
							if (!PaymentHandler.chargePlayerNORMAL(DragonTravelMain.TRAVEL_TORANDOM, player))
								return false;
						Travels.toRandomdest(player, true);
						return true;
					} else {
						player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
						return false;
					}
				} else {
					if (PermissionsHandler.hasTravelPermission(player, "travel", argument1)) {
						if (DragonTravelMain.usePayment)
							if (!PaymentHandler.chargePlayerNORMAL(DragonTravelMain.TRAVEL_TOSTATION, player))
								return false;
						Travels.toStation(player, argument1, true);
						return true;
					} else {

						player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
						return false;
					}
				}

			}

			else if (command.equalsIgnoreCase("ptravel")) {

				if (!player.hasPermission("dt.ptravel")) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
					return false;
				}

				Player targetplayer = Bukkit.getPlayer(argument1);

				if (targetplayer != null) {
					if (targetplayer == player) {
						player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Travels.Error.CannotTravelToYourself"));
						return false;
					}
					if (DragonTravelMain.usePayment)
						if (!PaymentHandler.chargePlayerNORMAL(DragonTravelMain.TRAVEL_TOPLAYER, player))
							return false;
					if (!DragonTravelMain.ptogglers.get(targetplayer.getName())) {
						player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Travels.Error.TargetPlayerDoesnotAllowPTravel").replace("{playername}", argument1));
						return false;
					}
					Travels.toPlayer(player, targetplayer, true);
					return true;
				} else {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Travels.Error.PlayerNotOnline").replace("{playername}", argument1));
					// TODO: ---ADD MESSAGE Player not online
					return false;
				}
			}

			else if (command.equalsIgnoreCase("flight")) {

				if (DragonTravelMain.onlysigns) {
					if (!player.hasPermission("dt.*")) {
						// TODO: ---ADD MESSAGE This command has been disabled by the admin, you can only travel using signs
						player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.OnlySigns"));
						return false;
					}
				}

				if (!PermissionsHandler.hasFlightPermission(player, argument1)) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
					return false;
				}

				if (DragonTravelMain.usePayment)
					if (!PaymentHandler.chargePlayerNORMAL(DragonTravelMain.FLIGHT, player))
						return false;
				Flights.startFlight(player, argument1, true, false, null);
				return true;
			}

			else if (command.equalsIgnoreCase("setstat")) {

				if (!player.hasPermission("dt.admin.stations")) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
					return false;
				}

				Location loc = player.getLocation();

				if (argument1.equalsIgnoreCase(DragonTravelMain.config.getString("RandomDest.Name"))) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Stations.Error.NotCreateStationWithRandomstatName"));
					return false;
				}

				if (DragonTravelMain.dbStationsHandler.getStation(argument1) != null) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Stations.Error.StationAlreadyExists").replace("{stationname}", argument1));
					return false;
				} else {
					if (DragonTravelMain.dbStationsHandler.saveStation(new Station(argument1, loc))) {
						player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Stations.Successful.StationCreated").replace("{stationname}", argument1));
						return true;
					} else {
						player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Stations.Error.CouldNotCreateStation"));
						return false;
					}
				}
			}

			else if (command.equalsIgnoreCase("remstat")) {

				if (!player.hasPermission("dt.admin.stations")) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
					return false;
				}

				if (DragonTravelMain.dbStationsHandler.getStation(argument1) == null) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Stations.Error.StationDoesNotExist"));
					// TODO: ---ADD MESSAGE There is no flight with that name
					return false;
				} else {
					if (DragonTravelMain.dbStationsHandler.deleteStation(argument1)) {
						player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Stations.Successful.StationRemoved").replace("{stationname}", argument1));
						return true;
					} else {
						player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Stations.Error.CouldNotRemoveStation"));
						return false;
					}
				}
			}

			else if (command.equalsIgnoreCase("createflight")) {

				if (!player.hasPermission("dt.admin.flights")) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
					return false;
				}

				if (FlightEditor.editors.containsKey(player)) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.AlreadyInFlightCreationMode"));
					// TODO: ---ADD MESSAGE You already are in Flight-creation mode
					return false;
				}

				if (DragonTravelMain.dbFlightsHandler.getFlight(argument1) != null) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.FlightAlreadyExists"));
					// TODO: ---ADD MESSAGE There already is a flight with that name
					return false;
				}

				FlightEditor.addEditor(player, argument1);

				player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Successful.NowInFlightCreationMode"));
				// TODO: ---ADD MESSAGE Help-Message for Flight-creation mode
				return true;
			}

			else if (command.equalsIgnoreCase("remflight")) {

				if (!player.hasPermission("dt.admin.flights")) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
					return false;
				}

				if (DragonTravelMain.dbFlightsHandler.getFlight(argument1) == null) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.FlightDoesNotExist"));
					// TODO: ---ADD MESSAGE There is no flight with that name
					return false;
				}

				DragonTravelMain.dbFlightsHandler.deleteFlight(argument1);

				player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Successful.RemovedFlight"));
				// TODO: ---ADD MESSAGE Successfully removed the flight
				return true;
			}

			return false;

		case 3:
			argument1 = args[1];
			argument2 = args[2];

			// Sending other players on a flight
			if (command.equalsIgnoreCase("flight")) {

				if (!player.hasPermission("dt.*")) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
					return false;
				}

				Player playerToSendToFlight = Bukkit.getPlayer(argument2);

				if (playerToSendToFlight == null) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.CouldNotfindPlayerToSend").replace("{playername}", argument2));
					return false;
				}

				Flights.startFlight(playerToSendToFlight, argument1, true, true, player);
				return true;
			}

			sendUsage(player, 1);
			return false;

		case 4:
			argument1 = args[1];
			argument2 = args[2];
			argument3 = args[3];

			if (command.equalsIgnoreCase("ctravel")) {

				if (!player.hasPermission("dt.ctravel")) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
					return false;
				}

				try {
					int x = Integer.parseInt(argument1);
					int y = Integer.parseInt(argument2);
					int z = Integer.parseInt(argument3);

					if (DragonTravelMain.usePayment)
						if (!PaymentHandler.chargePlayerNORMAL(DragonTravelMain.TRAVEL_TOCOORDINATES, player))
							return false;

					Travels.toCoordinates(player, x, y, z, null, true);
					return true;
				} catch (NumberFormatException ex) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Travels.Error.InvalidCoordinates"));
					// TODO: ---ADD MESSAGE Invalid coordinates
					return false;
				}
			}

			sendUsage(player, 1);
			return false;

		case 5:
			argument1 = args[1];
			argument2 = args[2];
			argument3 = args[3];
			argument4 = args[4];

			if (command.equalsIgnoreCase("ctravel")) {

				if (!player.hasPermission("dt.ctravel")) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
					return false;
				}

				try {
					int x = Integer.parseInt(argument1);
					int y = Integer.parseInt(argument2);
					int z = Integer.parseInt(argument3);

					if (DragonTravelMain.usePayment)
						if (!PaymentHandler.chargePlayerNORMAL(DragonTravelMain.TRAVEL_TOCOORDINATES, player))
							return false;

					Travels.toCoordinates(player, x, y, z, argument4, true);
					return true;
				} catch (NumberFormatException ex) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Travels.Error.InvalidCoordinates"));
					// TODO: ---ADD MESSAGE Invalid Coordinates
					return false;
				}
			}

			sendUsage(player, 1);
			return false;

		default:
			sendUsage(player, 1);
			// TODO: ---ADD MESSAGE Commandhelp
			return false;
		}
	}

	/////////////////////////////////
	public void sendUsage(CommandSender sender) {

	}

	public void sendUsage(Player player, int page) {

		ChatColor white = ChatColor.WHITE;
		ChatColor purple = ChatColor.LIGHT_PURPLE;
		ChatColor blue = ChatColor.BLUE;


		player.sendMessage(ChatColor.GOLD + "\n   >>> DragonTravel Help <<<  ");

		switch (page) {
		case CommandHelp.HELP_Page1:
			player.sendMessage(String.format("            Page (%d/5)    \n--------------------------", page));
			player.sendMessage(purple + "Use " + white + "/dt help <page> " + purple + "to view a page.");
			player.sendMessage(blue + "      Table of contents  ");
			player.sendMessage("--------------------------");
			CommandHelp.page1_TableOfContents(player);
			break;
		case CommandHelp.HELP_Page2:
			player.sendMessage(String.format("            Page (%d/5)", page));
			player.sendMessage(blue + "              General  ");
			player.sendMessage("--------------------------");
			CommandHelp.page2_General(player);
			break;
		case CommandHelp.HELP_Page3:
			player.sendMessage(String.format("            Page (%d/5)", page));
			player.sendMessage(blue + "              Travels  ");
			player.sendMessage("--------------------------");
			CommandHelp.page3_Travels(player);
			break;
		case CommandHelp.HELP_Page4:
			player.sendMessage(String.format("            Page (%d/5)", page));
			player.sendMessage(blue + "               Flights  ");
			player.sendMessage("--------------------------");
			CommandHelp.page4_Flights(player);
			break;
		case CommandHelp.HELP_Page5:
			player.sendMessage(String.format("            Page (%d/5)", page));
			player.sendMessage(blue + "           Administrative  ");
			player.sendMessage("--------------------------");
			CommandHelp.page5_Administrative(player);
			break;
		default:
			break;
		}
		player.sendMessage("\n--------------------------");
	}
}
