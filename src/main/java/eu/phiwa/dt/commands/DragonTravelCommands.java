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

public final class DragonTravelCommands {

	public static class DragonTravelParentCommand {
		@Command(aliases = {"dt"}, desc = "DragonTravel commands", flags = "d", min = 1, max = 3)
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
	@CommandPermissions({"dt.admin.remdragon"})
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

	// Editing
	public boolean __SECTION_FLYING__;
	@Command(aliases = {"flight"},
			desc = "Start a Flight",
			usage = "/dt remflight <flight name>",
			min = 1, max = 1)
	@CommandPermissions({"dt.edit.flights", "dt.edit"})
	public static void startFlight(CommandContext args, CommandSender sender) throws CommandException {

	}


	// Editing
	public boolean __SECTION_EDITING__;

	@Command(aliases = {"remflight", "delflight"},
			desc = "Delete a Flight",
			usage = "/dt remflight <flight name>",
			min = 1, max = 1)
	@CommandPermissions({"dt.edit.flights", "dt.edit"})
	public static void removeFlight(CommandContext args, CommandSender sender) throws CommandException {
		if (DragonTravelMain.dbFlightsHandler.getFlight(args.getString(1)) == null) {
			sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Error.FlightDoesNotExist"));
			return;
		}

		DragonTravelMain.dbFlightsHandler.deleteFlight(args.getString(1));

		sender.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.Flights.Successful.RemovedFlight"));
	}

}
