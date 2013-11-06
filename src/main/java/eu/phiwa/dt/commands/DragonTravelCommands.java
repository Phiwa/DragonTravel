package eu.phiwa.dt.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.help.HelpTopic;
import org.bukkit.util.ChatPaginator;

import com.sk89q.minecraft.util.commands.ChatColor;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.Console;
import com.sk89q.minecraft.util.commands.NestedCommand;

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
import eu.phiwa.dt.payment.ChargeType;
import eu.phiwa.dt.permissions.PermissionsHandler;

public final class DragonTravelCommands {

	public static class DragonTravelParentCommand {
		@Command(aliases = {"dt", "dragontravel"}, desc = "DragonTravel commands")
		@CommandPermissions({"dt.seecommand"})
		@NestedCommand({DragonTravelCommands.class})
		public static void dragonTravel() { }
	}

	/**********************************************
	 *                  GENERAL                   *
	 **********************************************/
	public byte __SECTION_GENERAL__;


	@Console
	@Command(aliases = {"help", "?", "h"},
			desc = "This help",
			usage = "[subcommand] [page]",
			min = 0, max = 2,
			help = "Shows more extensive help for each subcommand")
	@CommandPermissions({"dt.seecommand"})
	public static void help(CommandContext args, CommandSender sender) throws CommandException {
		int page = 1;
		if (args.argsLength() == 0) {
			sendHelpTopic(sender, DragonTravelMain.plugin.help, 1);
			sender.sendMessage(ChatColor.AQUA + "For additional help, use " + ChatColor.LIGHT_PURPLE + "/dt help <subcommand>" + ChatColor.AQUA + ".");
			return;
		}
		if (args.argsLength() == 1) {
			try {
				page = Integer.parseInt(args.getString(0));
				sendHelpTopic(sender, DragonTravelMain.plugin.help, page);
				sender.sendMessage(ChatColor.AQUA + "For additional help, use " + ChatColor.LIGHT_PURPLE + "/dt help <subcommand>" + ChatColor.AQUA + ".");
				return;
			} catch (NumberFormatException caught) { }
		}
		if (args.argsLength() == 2) {
			// Uncaught - turns into a message at highest level
			page = Integer.parseInt(args.getString(1));
		}

		HelpTopic topic = DragonTravelMain.plugin.help.getSubcommandHelp(sender, args.getString(0));
		if (topic == null) {
			sender.sendMessage(ChatColor.RED + "No help for " + args.getString(0));
			return;
		}

		sendHelpTopic(sender, topic, page);
	}

	// Ripped from org.bukkit.command.defaults.HelpCommand
	public static void sendHelpTopic(CommandSender sender, HelpTopic topic, int pageNumber) {
		int pageHeight, pageWidth;
		if (sender instanceof ConsoleCommandSender) {
			pageHeight = ChatPaginator.UNBOUNDED_PAGE_HEIGHT;
			pageWidth = ChatPaginator.UNBOUNDED_PAGE_WIDTH;
		} else {
			pageHeight = ChatPaginator.CLOSED_CHAT_PAGE_HEIGHT - 2;
			pageWidth = ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH;
		}
		ChatPaginator.ChatPage page = ChatPaginator.paginate(topic.getFullText(sender), pageNumber, pageWidth, pageHeight);

		StringBuilder header = new StringBuilder();
		header.append(ChatColor.YELLOW);
		header.append("--------- ");
		header.append(ChatColor.WHITE);
		header.append("Help: ");
		header.append(topic.getName());
		header.append(" ");
		if (page.getTotalPages() > 1) {
			header.append("(");
			header.append(page.getPageNumber());
			header.append("/");
			header.append(page.getTotalPages());
			header.append(") ");
		}
		header.append(ChatColor.YELLOW);
		for (int i = header.length(); i < ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH; i++) {
			header.append("-");
		}
		sender.sendMessage(header.toString());

		sender.sendMessage(page.getLines());
	}

	@Console
	@Command(aliases = {"reload"},
			desc = "Reload the config",
			help = "Reloads all files (extremely buggy!)")
	@CommandPermissions({"dt.admin.reload"})
	public static void reload(CommandContext args, CommandSender sender) throws CommandException {
		DragonTravelMain.plugin.reload();
	}

	@Console
	@Command(aliases = {"showstations", "showstats"},
			desc = "Show available stations",
			help = "Shows a list of all available stations.")
	public static void showStations(CommandContext args, CommandSender sender) throws CommandException {
		DragonTravelMain.dbStationsHandler.showStations(sender);
	}

	@Console
	@Command(aliases = {"showflights"},
			desc = "Show available flights",
			help = "Shows a list of all available flights.")
	public static void showFlights(CommandContext args, CommandSender sender) throws CommandException {
		DragonTravelMain.dbFlightsHandler.showFlights(sender);
	}

	@Console
	@Command(aliases = {"removedragons", "remdragons"},
			desc = "Remove all dragons",
			usage = "[-g | world]",
			min = 0, max = 1, flags="g",
			help = "Removes all dragons (except stationary dragons) without riders.\n"
					+ "It only acts on the world you're currently in, unless you use the -g ('global') flag, or specify a world in the command.")
	@CommandPermissions({"dt.admin.remdragons"})
	public static void removeDragons(CommandContext args, CommandSender sender) throws CommandException {
		if (args.hasFlag('g')) {
			for (World world : Bukkit.getWorlds()) {
				sender.sendMessage("[DragonTravel] " + DragonManagement.removeDragons(world));
			}
			return;
		}
		switch (args.argsLength()) {
		case 0:
			if (sender instanceof Player) {
				sender.sendMessage("[DragonTravel] " + DragonManagement.removeDragons(((Player) sender).getWorld()));
				return;
			}
			sender.sendMessage(ChatColor.RED + "You must specify a world to clear");
			return;
		case 1:
			String w = args.getString(0);
			World world = Bukkit.getWorld(w);
			if (world == null) {
				sender.sendMessage(ChatColor.RED + "The world " + w + " does not exist!"); // TODO locale
				return;
			}
			sender.sendMessage("[DragonTravel] " + DragonManagement.removeDragons(world));
		}
	}

	@Command(aliases = {"statdragon", "stationarydragon"},
			desc = "Create a stationary dragon where you are",
			help = "Create a new stationary dragon at your location.")
	@CommandPermissions({"dt.admin.statdragon"})
	public static void createStationaryDragon(CommandContext args, CommandSender sender) throws CommandException {
		if (!(sender instanceof Player)) {
			sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoConsole"));
			return;
		}
		Player player = (Player) sender;

		StationaryDragon.createStatDragon(player);
	}

	@Command(aliases = {"dismount"},
			desc = "Get off of the dragon",
			help = "Dismounts you from the dragons. "
					+ "Depending on the server's settings, "
					+ "you might be teleported back to the "
					+ "point you started your journey from.")
	@CommandPermissions({"dt.dismount"})
	public static void dismount(CommandContext args, CommandSender sender) throws CommandException {
		if (!(sender instanceof Player)) {
			sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoConsole"));
			return;
		}
		Player player = (Player) sender;
		DragonManagement.dismount(player, false);
	}

	@Console
	@Command(aliases = {"ptoggle"},
			desc = "Toggle whether you can recieve player dragon travels",
			usage = "[-y|-n] [player]",
			min = 0, max = 1,
			flags = "yn",
			help = "Toggles whether you allow/don't allow player-travels to you.\n"
					+ "Admins can forcibly change the status of other players.")
	@CommandPermissions({"dt.ptoggle", "dt.ptoggle.other"})
	public static void ptoggle(CommandContext args, CommandSender sender) throws CommandException {
		String playerName;

		if (args.getString(0, null) != null) {
			if (!sender.hasPermission("dt.ptoggle.other")) {
				throw new CommandPermissionsException();
			}
			Player p = Bukkit.getPlayer(args.getString(0));
			if (p == null) {
				sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Travels.Error.PlayerNotOnline").replace("{playername}", args.getString(0)));
				return;
			}
			playerName = p.getName();
		} else if (sender instanceof Player) {
			playerName = sender.getName();
		} else {
			// TODO localize
			sender.sendMessage("The console must provide a player for this command");
			return;
		}

		if (args.hasFlag('y')) {
			// Allow
			DragonTravelMain.ptogglers.put(playerName, true);
		} else if (args.hasFlag('n')) {
			// Disallow
			DragonTravelMain.ptogglers.put(playerName, false);
		} else {
			if (DragonTravelMain.ptogglers.get(playerName)) {
				// Disallow
				DragonTravelMain.ptogglers.put(playerName, false);
			} else {
				// Allow
				DragonTravelMain.ptogglers.put(playerName, true);
			}
		}
		// Fancy message sending with the ternary operator
		sender.sendMessage(DragonTravelMain.messagesHandler.getMessage(DragonTravelMain.ptogglers.get(playerName) ? "Messages.General.Successful.ToggledPTravelOn" : "Messages.General.Successful.ToggledPTravelOff"));
	}

	@Command(aliases = {"sethome"},
			desc = "Set your DragonTravel home",
			help = "Sets your DragonTravel home so that you can return later with /dt home.")
	@CommandPermissions({"dt.sethome"})
	public static void setHome(CommandContext args, CommandSender sender) throws CommandException {
		if (!(sender instanceof Player)) {
			sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoConsole"));
			return;
		}
		Player player = (Player) sender;

		if (!DragonTravelMain.plugin.paymentManager.chargePlayer(ChargeType.SETHOME, player))
			return;
		Home home = new Home(player.getLocation());
		DragonTravelMain.dbHomesHandler.saveHome(player.getName(), home);
	}


	/**********************************************
	 *                   FLYING                   *
	 **********************************************/
	public byte __SECTION_FLYING__;

	@Console
	@Command(aliases = {"flight"},
			desc = "Start a Flight",
			usage = "<flight name> [player=you]",
			min = 1, max = 2,
			help = "Starts the specified flight.")
	@CommandPermissions({"dt.start.flight.command", "dt.start.flight.command.other"})
	public static void startFlight(CommandContext args, CommandSender sender) throws CommandException {
		String flight = args.getString(0);
		Player player;

		if (!PermissionsHandler.hasFlightPermission(sender, flight)) {
			sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
			return;
		}

		switch (args.argsLength()) {
		case 1:
			if (!(sender instanceof Player)) {
				sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoConsole"));
				return;
			}
			if (!sender.hasPermission("dt.start.flight.command")) {
				throw new CommandPermissionsException();
			}

			player = (Player) sender;

			if (!DragonTravelMain.plugin.paymentManager.chargePlayer(ChargeType.FLIGHT, player)) {
				return;
			}
			Flights.startFlight(player, flight, true, false, sender);
			return;

		case 2:
			if (!sender.hasPermission("dt.start.flight.command.other")) {
				throw new CommandPermissionsException();
			}

			player = Bukkit.getPlayer(args.getString(1));
			if (player == null) {
				sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.CouldNotfindPlayerToSend").replace("{playername}", args.getString(1)));
				return;
			}

			// TODO: Should we do this?
			// if (sender instanceof Player) {
			// 	if (!DragonTravelMain.plugin.paymentManager.chargePlayer(ChargeType.FLIGHT, (Player) sender)) {
			// 		return;
			// 	}
			// }

			Flights.startFlight(player, flight, true, true, sender);
			return;
		}
	}

	@Command(aliases = {"travel"},
			desc = "Travel to another station",
			usage = "<station name>",
			min = 1, max = 1,
			help = "Brings you to the specified station")
	@CommandPermissions({"dt.start.travel.command"})
	public static void startStationTravel(CommandContext args, CommandSender sender) throws CommandException {
		String station = args.getString(0);

		if (!PermissionsHandler.hasTravelPermission(sender, "travel", station)) {
			sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
			return;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoConsole"));
			return;
		}
		Player player = (Player) sender;

		if (station.equalsIgnoreCase((DragonTravelMain.config.getString("RandomDest.Name")))) {
			if (!DragonTravelMain.plugin.paymentManager.chargePlayer(ChargeType.TRAVEL_TORANDOM, player))
				return;
			Travels.toRandomdest(player, true);
		} else {
			if (!DragonTravelMain.plugin.paymentManager.chargePlayer(ChargeType.TRAVEL_TOSTATION, player))
				return;
			Travels.toStation(player, station, true);
		}
	}

	@Command(aliases = {"ptravel", "player"},
			desc = "Travel to another player",
			usage = "<player>",
			min = 1, max = 1,
			help = "Brings you to the specified player")
	@CommandPermissions({"dt.start.player.command"})
	public static void startPlayerTravel(CommandContext args, CommandSender sender) throws CommandException {
		if (!(sender instanceof Player)) {
			sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoConsole"));
			return;
		}
		Player player = (Player) sender;
		Player targetplayer = Bukkit.getPlayer(args.getString(0));

		if (targetplayer == null) {
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Travels.Error.PlayerNotOnline").replace("{playername}", args.getString(0)));
			return;
		}
		if (targetplayer == sender) {
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Travels.Error.CannotTravelToYourself"));
			return;
		}
		if (!DragonTravelMain.plugin.paymentManager.chargePlayer(ChargeType.TRAVEL_TOPLAYER, player)) {
			return;
		}
		if (!DragonTravelMain.ptogglers.get(targetplayer.getName())) {
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Travels.Error.TargetPlayerDoesnotAllowPTravel").replace("{playername}", args.getString(0)));
			return;
		}
		Travels.toPlayer(player, targetplayer, true);
	}

	@Command(aliases = {"ctravel", "coord", "coords"},
			desc = "Travel to some coordinates",
			usage = "x y z [world]",
			min = 3, max = 4,
			help = "Brings you to the specified location")
	@CommandPermissions({"dt.start.coord.command"})
	public static void startCoordsTravel(CommandContext args, CommandSender sender) throws CommandException {
		if (!(sender instanceof Player)) {
			sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoConsole"));
			return;
		}
		Player player = (Player) sender;

		try {
			int x = args.getInteger(0);
			int y = args.getInteger(1);
			int z = args.getInteger(2);
			String world = args.getString(3, null);

			if (!DragonTravelMain.plugin.paymentManager.chargePlayer(ChargeType.TRAVEL_TOCOORDINATES, (Player) sender))
				return;

			Travels.toCoordinates(player, x, y, z, world, true);
		} catch (NumberFormatException ex) {
			sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Travels.Error.InvalidCoordinates"));
			return;
		}
	}

	@Command(aliases = {"home"},
			desc = "Travel to your home",
			help = "Brings you to your set home aboard a dragon.")
	@CommandPermissions({"dt.start.home.command"})
	public static void startHomeTravel(CommandContext args, CommandSender sender) throws CommandException {
		if (!(sender instanceof Player)) {
			sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoConsole"));
			return;
		}
		Player player = (Player) sender;

		if (!DragonTravelMain.plugin.paymentManager.chargePlayer(ChargeType.TRAVEL_TOHOME, player))
			return;
		Travels.toHome(player, true);
	}

	@Command(aliases = {"fhome"},
			desc = "Travel to your faction home",
			help = "Brings you to your faction's home.")
	@CommandPermissions({"dt.start.fhome.command"})
	public static void startFHomeTravel(CommandContext args, CommandSender sender) throws CommandException {
		if (!(sender instanceof Player)) {
			sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoConsole"));
			return;
		}
		Player player = (Player) sender;

		if (DragonTravelMain.pm.getPlugin("Factions") == null) {
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Factions.Error.FactionsNotInstalled"));
			return;
		}
		if (!DragonTravelMain.plugin.paymentManager.chargePlayer(ChargeType.TRAVEL_TOFACTIONHOME, player))
			return;
		Travels.toFactionhome(player, true);
	}

	/**********************************************
	 *                  EDITING                   *
	 **********************************************/
	public byte __SECTION_EDITING__;

	@Command(aliases = {"createflight", "newflight"},
			desc = "Create a new Flight",
			usage = "<name>",
			min = 1, max = 1,
			help = "Creates a new flight with the given name and puts you into the flight-creation mode.\n"
					+ "You MUST NOT be in Flight Editing mode when you use this command.")
	@CommandPermissions({"dt.edit.flights", "dt.edit.*"})
	public static void newFlight(CommandContext args, CommandSender sender) throws CommandException {
		if (!(sender instanceof Player)) {
			sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoConsole"));
			return;
		}
		Player player = (Player) sender;

		if (FlightEditor.editors.containsKey(player)) {
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.AlreadyInFlightCreationMode"));
			return;
		}

		String flight = args.getString(0);
		if (DragonTravelMain.dbFlightsHandler.getFlight(flight) != null) {
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.FlightAlreadyExists"));
			return;
		}

		FlightEditor.addEditor(player, flight);

		player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Successful.NowInFlightCreationMode"));
	}

	@Command(aliases = {"remflight", "delflight"},
			desc = "Delete a Flight",
			usage = "<flight name>",
			min = 1, max = 1,
			help = "Removes the flight with the specified name.")
	@CommandPermissions({"dt.edit.flights", "dt.edit.*"})
	public static void removeFlight(CommandContext args, CommandSender sender) throws CommandException {
		if (DragonTravelMain.dbFlightsHandler.getFlight(args.getString(0)) == null) {
			sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.FlightDoesNotExist"));
			return;
		}

		DragonTravelMain.dbFlightsHandler.deleteFlight(args.getString(0));

		sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Successful.RemovedFlight"));
	}

	@Command(aliases = {"saveflight"},
			desc = "Save the flight you are editing",
			help = "Saves the flight and ends flight-creation mode.\n\n"
					+ "You MUST be in Flight Editing mode when you use this command.")
	@CommandPermissions({"dt.edit.flights", "dt.edit.*"})
	public static void saveFlight(CommandContext args, CommandSender sender) throws CommandException {
		if (!(sender instanceof Player)) {
			sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoConsole"));
			return;
		}
		Player player = (Player) sender;

		Flight wipFlight = FlightEditor.editors.get(player);
		if (wipFlight == null) {
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.NotInFlightCreationMode"));
			return;
		}
		if (wipFlight.getWaypointCount() < 1) {
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.AtLeastOneWaypoint"));
			return;
		}

		DragonTravelMain.dbFlightsHandler.saveFlight(wipFlight);
		Waypoint.removeWaypointMarkersOfFlight(wipFlight);
		FlightEditor.removeEditor(player);

		player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Successful.FlightSaved"));
	}

	@Command(aliases = {"setwp"},
			desc = "Set a waypoint for the flight",
			usage = "[x y z]",
			min = 0, max = 3,
			help = "Add a new waypoint to the flight where you're standing, or at the given coordinates.\n\n"
					+ "You MUST be in Flight Editing mode when you use this command.")
	@CommandPermissions({"dt.edit.flights", "dt.edit.*"})
	public static void setWaypoint(CommandContext args, CommandSender sender) throws CommandException {
		if (!(sender instanceof Player)) {
			sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoConsole"));
			return;
		}
		Player player = (Player) sender;

		Flight wipFlight = FlightEditor.editors.get(player);
		if (wipFlight == null) {
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.NotInFlightCreationMode"));
			return;
		}

		Location loc = player.getLocation();

		if (!wipFlight.worldName.equals(loc.getWorld().getName())) {
			loc.setWorld(Bukkit.getWorld(wipFlight.worldName));
		}

		if (args.argsLength() == 3) {
			loc.setX(args.getInteger(0));
			loc.setY(args.getInteger(1));
			loc.setZ(args.getInteger(2));
		}
		Waypoint wp = new Waypoint(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

		wp.setMarker(loc);
		Block block = loc.getBlock();
		DragonTravelMain.globalwaypointmarkers.put(block, block);

		wipFlight.addWaypoint(wp);

		player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Successful.WaypointAdded") + String.format("%s (%s @ %d,%d,%d)", ChatColor.GRAY, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
	}

	@Command(aliases = {"remlastwp", "remwp"},
			desc = "Remove the most recent waypoint",
			help = "Remove the most recently added waypoint from the flight.\n\n"
					+ "You MUST be in Flight Editing mode when you use this command.")
	@CommandPermissions({"dt.edit.flights", "dt.edit.*"})
	public static void removeWaypoint(CommandContext args, CommandSender sender) throws CommandException {
		if (!(sender instanceof Player)) {
			sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoConsole"));
			return;
		}
		Player player = (Player) sender;

		Flight wipFlight = FlightEditor.editors.get(player);
		if (wipFlight == null) {
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.NotInFlightCreationMode"));
			return;
		}

		wipFlight.removelastWaypoint();
		player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Successful.WaypointRemoved"));
	}

	@Command(aliases = {"setstation", "setstat"},
			desc = "Creates a new station here.",
			usage = "<name>",
			min = 1, max = 1,
			help = "Creates a new station with the given name at your current location.")
	@CommandPermissions({"dt.edit.stations", "dt.edit.*"})
	public static void setStation(CommandContext args, CommandSender sender) throws CommandException {
		if (!(sender instanceof Player)) {
			sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoConsole"));
			return;
		}
		Player player = (Player) sender;
		String station = args.getString(0);

		if (station.equalsIgnoreCase(DragonTravelMain.config.getString("RandomDest.Name"))) {
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Stations.Error.NotCreateStationWithRandomstatName"));
			return;
		}

		if (DragonTravelMain.dbStationsHandler.getStation(station) != null) {
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Stations.Error.StationAlreadyExists").replace("{stationname}", station));
		} else {
			if (DragonTravelMain.dbStationsHandler.saveStation(new Station(station, player.getLocation()))) {
				player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Stations.Successful.StationCreated").replace("{stationname}", station));
			} else {
				player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Stations.Error.CouldNotCreateStation"));
			}
		}
	}

	@Command(aliases = {"deletestation", "removestat", "remstation", "removestation",
					"delstat", "deletestat", "delstation", "remstat"},
			desc = "Delete a station",
			usage = "<name>",
			min = 1, max = 1,
			help = "Removes the station with the specified name.")
	@CommandPermissions({"dt.edit.stations", "dt.edit.*"})
	public static void removeStation(CommandContext args, CommandSender sender) throws CommandException {
		if (!(sender instanceof Player)) {
			sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoConsole"));
			return;
		}
		Player player = (Player) sender;
		String station = args.getString(0);

		if (station.equalsIgnoreCase(DragonTravelMain.config.getString("RandomDest.Name"))) {
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Stations.Error.NotCreateStationWithRandomstatName"));
			return;
		}

		if (DragonTravelMain.dbStationsHandler.getStation(station) == null) {
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Stations.Error.StationDoesNotExist"));
		} else {
			if (DragonTravelMain.dbStationsHandler.deleteStation(station)) {
				player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Stations.Successful.StationRemoved").replace("{stationname}", station));
			} else {
				player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Stations.Error.CouldNotRemoveStation"));
			}
		}
	}
}
