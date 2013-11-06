package eu.phiwa.dt.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.minecraft.util.commands.ChatColor;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.Console;
import com.sk89q.minecraft.util.commands.NestedCommand;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.modules.DragonManagement;
import eu.phiwa.dt.movement.Flights;
import eu.phiwa.dt.movement.Travels;
import eu.phiwa.dt.payment.ChargeType;
import eu.phiwa.dt.permissions.PermissionsHandler;

public final class DragonTravelCommands {

	public static class DragonTravelParentCommand {
		@Command(aliases = {"dt"}, desc = "DragonTravel commands", flags = "d", min = 1, max = 3)
		@CommandPermissions({"dt.seecommand"})
		@NestedCommand({DragonTravelCommands.class })
		public static void dragonTravel() { }
	}

	// General
	public boolean __SECTION_GENERAL__;

	@Console
	@Command(aliases = {"reload"}, desc = "Reload the config")
	@CommandPermissions({"dt.admin.reload"})
	public static void reload(CommandContext args, CommandSender sender) throws CommandException {
		DragonTravelMain.plugin.reload();
	}

	@Console
	@Command(aliases = {"showstations", "showstats"},
			desc = "Show available stations")
	public static void showStations(CommandContext args, CommandSender sender) throws CommandException {
		DragonTravelMain.dbStationsHandler.showStations(sender);
	}

	@Console
	@Command(aliases = {"showflights"},
			desc = "Show available flights")
	public static void showFlights(CommandContext args, CommandSender sender) throws CommandException {
		DragonTravelMain.dbFlightsHandler.showFlights(sender);
	}

	@Console
	@Command(aliases = {"removedragons", "remdragons"},
			desc = "Remove all dragons",
			usage = "/dt remdragons [-g] [worldname]",
			min = 0, max = 1, flags="g")
	@CommandPermissions({"dt.admin.remdragons"})
	public static void removeDragons(CommandContext args, CommandSender sender) throws CommandException {
		switch (args.argsLength()) {
		case 0:
			if (args.hasFlag('g')) {
				for (World world : Bukkit.getWorlds()) {
					sender.sendMessage("[DragonTravel] " + DragonManagement.removeDragons(world));
				}
				return;
			} else if (sender instanceof Player) {
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

	// Flying
	public boolean __SECTION_FLYING__;

	@Console
	@Command(aliases = {"flight"},
			desc = "Start a Flight",
			usage = "/dt flight <flight name> [player=you]",
			min = 1, max = 2)
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
				sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
				return;
			}

			player = (Player) sender;

			if (!DragonTravelMain.plugin.paymentManager.chargePlayer(ChargeType.FLIGHT, player)) {
				return;
			}
			Flights.startFlight(player, flight, true, false, sender);
			return;

		case 2:
			if (!sender.hasPermission("dt.start.flight.command.other")) {
				sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NoPermission"));
				return;
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
			usage = "/dt travel <station name>",
			min = 1, max = 1)
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
			usage = "/dt ptravel <player>",
			min = 1, max = 1)
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

	// Editing
	public boolean __SECTION_EDITING__;

	@Command(aliases = {"remflight", "delflight"},
			desc = "Delete a Flight",
			usage = "/dt remflight <flight name>",
			min = 1, max = 1)
	@CommandPermissions({"dt.edit.flights", "dt.edit.*"})
	public static void removeFlight(CommandContext args, CommandSender sender) throws CommandException {
		if (DragonTravelMain.dbFlightsHandler.getFlight(args.getString(0)) == null) {
			sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.FlightDoesNotExist"));
			return;
		}

		DragonTravelMain.dbFlightsHandler.deleteFlight(args.getString(0));

		sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Successful.RemovedFlight"));
	}

}
