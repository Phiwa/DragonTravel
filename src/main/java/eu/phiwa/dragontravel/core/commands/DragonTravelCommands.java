package eu.phiwa.dragontravel.core.commands;

import com.sk89q.minecraft.util.commands.*;
import eu.phiwa.dragontravel.api.DragonException;
import eu.phiwa.dragontravel.core.DragonManager;
import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.hooks.payment.ChargeType;
import eu.phiwa.dragontravel.core.hooks.permissions.PermissionsHandler;
import eu.phiwa.dragontravel.core.movement.flight.Flight;
import eu.phiwa.dragontravel.core.movement.flight.Waypoint;
import eu.phiwa.dragontravel.core.movement.stationary.StationaryDragon;
import eu.phiwa.dragontravel.core.movement.travel.Home;
import eu.phiwa.dragontravel.core.movement.travel.Station;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.help.HelpTopic;
import org.bukkit.util.ChatPaginator;

import java.util.UUID;

/*
Class adapted from Riking's contribution
 */
public final class DragonTravelCommands {

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
            sendHelpTopic(sender, DragonTravel.getInstance().getHelp(), 1);
            sender.sendMessage(ChatColor.AQUA + "For additional help, use " + ChatColor.LIGHT_PURPLE + "/dt help <subcommand>" + ChatColor.AQUA + ".");
            return;
        }
        if (args.argsLength() == 1) {
            try {
                page = Integer.parseInt(args.getString(0));
                sendHelpTopic(sender, DragonTravel.getInstance().getHelp(), page);
                sender.sendMessage(ChatColor.AQUA + "For additional help, use " + ChatColor.LIGHT_PURPLE + "/dt help <subcommand>" + ChatColor.AQUA + ".");
                return;
            } catch (NumberFormatException caught) {
            }
        }
        if (args.argsLength() == 2) {
            page = Integer.parseInt(args.getString(1));
        }

        HelpTopic topic = DragonTravel.getInstance().getHelp().getSubcommandHelp(sender, args.getString(0));
        if (topic == null) {
            sender.sendMessage(ChatColor.RED + "No help for " + args.getString(0));
            return;
        }

        sendHelpTopic(sender, topic, page);
    }

    // Ripped from org.bukkit.command.defaults.HelpCommand
    private static void sendHelpTopic(CommandSender sender, HelpTopic topic, int pageNumber) {
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
            usage = "/dt reload",
            help = "Reloads all files (extremely buggy!)")
    @CommandPermissions({"dt.admin.reload"})
    public static void reload(CommandContext args, CommandSender sender) throws CommandException {
        DragonTravel.getInstance().reload();
    }

    @Console
    @Command(aliases = {"showstations", "showstats"},
            desc = "Show available stations",
            usage = "/dt showstations",
            help = "Shows a list of all available stations.")
    public static void showStations(CommandContext args, CommandSender sender) throws CommandException {
        DragonTravel.getInstance().getDbStationsHandler().showStations(sender);
    }

    @Console
    @Command(aliases = {"showflights"},
            desc = "Show available flights",
            usage = "/dt showflights",
            help = "Shows a list of all available flights.")
    public static void showFlights(CommandContext args, CommandSender sender) throws CommandException {
        DragonTravel.getInstance().getDbFlightsHandler().showFlights(sender);
    }

    @Console
    @Command(aliases = {"removedragons", "remdragons"},
            desc = "Remove all dragons",
            usage = "/dt remdragons [-g | world]",
            min = 0, max = 1, flags = "g",
            help = "Removes all dragons (except stationary dragons) without riders.\n"
                    + "It only acts on the world you're currently in, unless you use the -g ('global')")
    @CommandPermissions({"dt.admin.remdragons"})
    public static void removeDragons(CommandContext args, CommandSender sender) throws CommandException {
        if (args.hasFlag('g')) {
            for (World world : Bukkit.getWorlds()) {
                sender.sendMessage("[DragonTravel] " + DragonTravel.getInstance().getDragonManager().removeDragons(world, false));
            }
            return;
        }
        switch (args.argsLength()) {
            case 0:
                if (sender instanceof Player) {
                    sender.sendMessage("[DragonTravel] " + DragonTravel.getInstance().getDragonManager().removeDragons(((Player) sender).getWorld(), false));
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
                sender.sendMessage("[DragonTravel] " + DragonTravel.getInstance().getDragonManager().removeDragons(world, false));
        }
    }

    @Command(aliases = {"addstatdragon", "stationarydragon"},
            desc = "Create a stationary dragon where you are",
            usage = "/dt addstatdragon <name> [display_name]",
            min = 1, max = 2)
    @CommandPermissions({"dt.admin.statdragon"})
    public static void createStationaryDragon(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        Player player = (Player) sender;
        String name = args.getString(0).toLowerCase();
        String displayName = name;
        if (args.argsLength() == 2) {
            displayName = args.getString(1).replace('_', ' ');
        }
        if (DragonTravel.getInstance().getDragonManager().getStationaryDragons().containsKey(name)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NameTaken"));
            return;
        }
        StationaryDragon sDragon = new StationaryDragon(player, name, displayName, player.getLocation(), true);
        DragonTravel.getInstance().getDragonManager().getStationaryDragons().put(name.toLowerCase(), sDragon);
    }

    @Command(aliases = {"remstatdragon", "remstationarydragon"},
            desc = "Delete a stationary dragon",
            usage = "/dt remstatdragon <name>",
            min = 1, max = 2)
    @CommandPermissions({"dt.admin.statdragon"})
    public static void deleteStationaryDragon(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        Player player = (Player) sender;
        String name = args.getString(0).toLowerCase();
        if (!player.hasPermission("dt.admin.statdragon")) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
            return;
        }

        if (!DragonTravel.getInstance().getDragonManager().getStationaryDragons().keySet().contains(name)) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.StatDragonNotExists"));
            return;
        }

        StationaryDragon sDragon = DragonTravel.getInstance().getDragonManager().getStationaryDragons().get(name);
        sDragon.removeDragon(true);
    }

    @Command(aliases = {"dismount"},
            desc = "Get off of the dragon",
            usage = "/dt dismount",
            help = "Dismounts you from the dragons. "
                    + "Depending on the server's settings, "
                    + "you might be teleported back to the "
                    + "point you started your journey from.")
    @CommandPermissions({"dt.dismount"})
    public static void dismount(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        Player player = (Player) sender;
        DragonTravel.getInstance().getDragonManager().dismount(player, false);
    }

    @Console
    @Command(aliases = {"ptoggle"},
            desc = "Toggle whether you can recieve player dragon travels",
            usage = "/dt ptoggle [-y|-n]",
            min = 0, max = 1,
            flags = "yn",
            help = "Toggles whether you allow/don't allow\n player-travels to you.")
    @CommandPermissions({"dt.ptoggle", "dt.ptoggle.other"})
    public static void ptoggle(CommandContext args, CommandSender sender) throws CommandException {
        String playerName;
        String playerId;

        if (args.getString(0, null) != null) {
            if (!sender.hasPermission("dt.ptoggle.other")) {
            	sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
                throw new CommandPermissionsException();
            }
            Player p = Bukkit.getPlayer(args.getString(0));
            if (p == null) {
                sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Error.PlayerNotOnline").replace("{playername}", args.getString(0)));
                return;
            }
            playerName = p.getName();
            playerId = p.getUniqueId().toString();
        } else if (sender instanceof Player) {
            playerName = sender.getName();
            playerId = ((Player) sender).getUniqueId().toString();
        } else {
            // TODO localize
            sender.sendMessage("The console must provide a player for this command");
            return;
        }

        if (args.hasFlag('y')) {
            // Allow
            DragonTravel.getInstance().getDragonManager().getPlayerToggles().put(UUID.fromString(playerId), true);
        } else if (args.hasFlag('n')) {
            // Disallow
            DragonTravel.getInstance().getDragonManager().getPlayerToggles().put(UUID.fromString(playerId), false);
        } else {
            if (DragonTravel.getInstance().getDragonManager().getPlayerToggles().get(playerName)) {
                // Disallow
                DragonTravel.getInstance().getDragonManager().getPlayerToggles().put(UUID.fromString(playerId), false);
            } else {
                // Allow
                DragonTravel.getInstance().getDragonManager().getPlayerToggles().put(UUID.fromString(playerId), true);
            }
        }
        // Fancy message sending with the ternary operator
        sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage(DragonTravel.getInstance().getDragonManager().getPlayerToggles().get(playerName) ? "Messages.General.Successful.ToggledPTravelOn" : "Messages.General.Successful.ToggledPTravelOff"));
    }

    @Command(aliases = {"sethome"},
            desc = "Set your DragonTravel home",
            usage = "/dt sethome",
            help = "Sets your DragonTravel home.")
    @CommandPermissions({"dt.sethome"})
    public static void setHome(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        Player player = (Player) sender;

        if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.SETHOME, player))
            return;
        Home home = new Home(player.getLocation());
        DragonTravel.getInstance().getDbHomesHandler().saveHome(player.getUniqueId().toString(), home);
        sender.sendMessage(ChatColor.GREEN + "Home set!"); //TODO: Add to messages
    }

    @Console
    @Command(aliases = {"flight"},
            desc = "Start a Flight",
            usage = "/dt flight <flight name> [player=you]",
            min = 1, max = 2,
            help = "Starts the specified flight.")
    //@CommandPermissions({"dt.start.flight.command", "dt.start.flight.command.other"})
    public static void startFlight(CommandContext args, CommandSender sender) throws CommandException {
        String flight = args.getString(0);
        Player player;
        if (!PermissionsHandler.hasFlightPermission(sender, flight)) {
        	sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
            throw new CommandPermissionsException();
        }
        switch (args.argsLength()) {
            case 1:
                if (!(sender instanceof Player)) {
                    sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
                    return;
                }
//              if (!sender.hasPermission("dt.start.flight.command")) {
//                  sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
//                  throw new CommandPermissionsException();
//              }

                player = (Player) sender;

                if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.FLIGHT, player)) {
                    return;
                }
                try {
                    DragonManager.getDragonManager().getFlightEngine().startFlight(player, flight, true, false, sender);
                } catch (DragonException e) {
                    e.printStackTrace();
                }
                return;

            case 2:
//              if (!sender.hasPermission("dt.start.flight.command.other")) {
//           		sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
//                  throw new CommandPermissionsException();
//              }
            	
            	if (!sender.hasPermission("dt.*")) {
            		sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
            		throw new CommandPermissionsException();
            	}

                player = Bukkit.getPlayer(args.getString(1));
                if (player == null) {
                    sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Error.CouldNotfindPlayerToSend").replace("{playername}", args.getString(1)));
                    return;
                }
                try {
                    DragonManager.getDragonManager().getFlightEngine().startFlight(player, flight, true, true, sender);
                } catch (DragonException e) {
                    e.printStackTrace();
                }
                return;
        }
    }

    @Command(aliases = {"travel"},
            desc = "Travel to another station",
            usage = "/dt travel <station name>",
            min = 1, max = 1,
            help = "Brings you to the specified station")
    @CommandPermissions({"dt.start.travel.command"})
    public static void startStationTravel(CommandContext args, CommandSender sender) throws CommandException {

        String station = args.getString(0);
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        if (!PermissionsHandler.hasTravelPermission(sender, "travel", station)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
            return;
        }
        Player player = (Player) sender;

        if (station.equalsIgnoreCase((DragonTravel.getInstance().getConfig().getString("RandomDest.Name")))) {
            if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.TRAVEL_TORANDOM, player))
                return;
            try {
                DragonManager.getDragonManager().getTravelEngine().toRandomDest(player, true);
            } catch (DragonException e) {
                e.printStackTrace();
            }
        } else {
            if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.TRAVEL_TOSTATION, player))
                return;
            try {
                DragonManager.getDragonManager().getTravelEngine().toStation(player, station, true);
            } catch (DragonException e) {
                e.printStackTrace();
            }
        }
    }

    @Command(aliases = {"ptravel", "player"},
            desc = "Travel to another player",
            usage = "/dt ptravel <player>",
            min = 1, max = 1,
            help = "Brings you to the specified player")
    @CommandPermissions({"dt.start.player.command"})
    public static void startPlayerTravel(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        Player player = (Player) sender;
        Player targetPlayer = Bukkit.getPlayer(args.getString(0));

        if (targetPlayer == null) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Error.PlayerNotOnline").replace("{playername}", args.getString(0)));
            return;
        }
        if (targetPlayer == sender) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Error.CannotTravelToYourself"));
            return;
        }
        if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.TRAVEL_TOPLAYER, player)) {
            return;
        }
        if (!DragonTravel.getInstance().getDragonManager().getPlayerToggles().get(targetPlayer.getUniqueId())) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Error.TargetPlayerDoesnotAllowPTravel").replace("{playername}", args.getString(0)));
            return;
        }
        try {
            DragonManager.getDragonManager().getTravelEngine().toPlayer(player, targetPlayer, true);
        } catch (DragonException e) {
            e.printStackTrace();
        }
    }

    @Command(aliases = {"ctravel", "coord", "coords"},
            desc = "Travel to some coordinates",
            usage = "/dt ctravel x y z [world]",
            min = 3, max = 4,
            help = "Brings you to the specified location")
    @CommandPermissions({"dt.start.coord.command"})
    public static void startCoordsTravel(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        Player player = (Player) sender;

        try {
            int x = args.getInteger(0);
            int y = args.getInteger(1);
            int z = args.getInteger(2);
            String world = args.getString(3, null);

            if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.TRAVEL_TOCOORDINATES, (Player) sender))
                return;

            DragonManager.getDragonManager().getTravelEngine().toCoordinates(player, x, y, z, world, true);
        } catch (NumberFormatException ex) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Error.InvalidCoordinates"));
        } catch (DragonException e) {
            e.printStackTrace();
        }
    }

    @Command(aliases = {"home"},
            desc = "Travel to your home",
            usage = "/dt home",
            help = "Brings you to your home")
    @CommandPermissions({"dt.start.home.command"})
    public static void startHomeTravel(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        Player player = (Player) sender;

        if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.TRAVEL_TOHOME, player))
            return;
        try {
            DragonManager.getDragonManager().getTravelEngine().toHome(player, true);
        } catch (DragonException e) {
            e.printStackTrace();
        }
    }

    @Command(aliases = {"fhome"},
            desc = "Travel to your faction home",
            usage = "/dt fhome")
    @CommandPermissions({"dt.start.fhome.command"})
    public static void startFactionHomeTravel(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        Player player = (Player) sender;

        if (Bukkit.getPluginManager().getPlugin("Factions") == null) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.FactionsNotInstalled"));
            return;
        }
        if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.TRAVEL_TOFACTIONHOME, player))
            return;
        try {
            DragonManager.getDragonManager().getTravelEngine().toFactionHome(player, true);
        } catch (DragonException e) {
            e.printStackTrace();
        }
    }

    @Command(aliases = {"createflight", "newflight"},
            desc = "Create a new Flight",
            usage = "/dt createflight",
            help = "Creates a new flight and puts you into the flight-creation mode.\n\n"
                    + "You MUST NOT be in Flight Editing mode when you use this command.")
    @CommandPermissions({"dt.edit.flights", "dt.edit.*"})
    public static void newFlight(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        Player player = (Player) sender;

        if (DragonTravel.getInstance().getFlightEditor().getEditors().containsKey(player)) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Error.AlreadyInFlightCreationMode"));
            return;
        }

        String flight = args.getString(0).toLowerCase();
        String displayName = flight;
        if (args.argsLength() == 2) {
            displayName = args.getString(1);
        }
        if (DragonTravel.getInstance().getDbFlightsHandler().getFlight(flight) != null) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Error.FlightAlreadyExists"));
            return;
        }

        DragonTravel.getInstance().getFlightEditor().addEditor(player, flight, displayName);

        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Successful.NowInFlightCreationMode"));
    }

    @Command(aliases = {"remflight", "delflight"},
            desc = "Delete a Flight",
            usage = "/dt remflight <name>",
            min = 1, max = 1,
            help = "Removes the flight with the specified name.")
    @CommandPermissions({"dt.edit.flights", "dt.edit.*"})
    public static void removeFlight(CommandContext args, CommandSender sender) throws CommandException {
        if (DragonTravel.getInstance().getDbFlightsHandler().getFlight(args.getString(0)) == null) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Error.FlightDoesNotExist"));
            return;
        }

        DragonTravel.getInstance().getDbFlightsHandler().deleteFlight(args.getString(0));

        sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Successful.RemovedFlight"));
    }

    @Command(aliases = {"saveflight"},
            desc = "Save the flight you are editing",
            usage = "/dt saveflight",
            help = "Saves the flight and ends flight-creation mode.\n\n"
                    + "You MUST be in Flight Editing mode when you use this command.")
    @CommandPermissions({"dt.edit.flights", "dt.edit.*"})
    public static void saveFlight(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        Player player = (Player) sender;

        Flight wipFlight = DragonTravel.getInstance().getFlightEditor().getEditors().get(player);
        if (wipFlight == null) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Error.NotInFlightCreationMode"));
            return;
        }
        if (wipFlight.getWaypoints().size() < 1) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Error.AtLeastOneWaypoint"));
            return;
        }

        DragonTravel.getInstance().getDbFlightsHandler().saveFlight(wipFlight);
        Waypoint.removeWayPointMarkersOfFlight(wipFlight);
        DragonTravel.getInstance().getFlightEditor().removeEditor(player);

        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Successful.FlightSaved"));
    }

    @Command(aliases = {"setwp"},
            desc = "Set a waypoint for the flight",
            usage = "/dt setwp [<x> <y> <z> [world]]",
            min = 0, max = 4,
            help = "Add a new waypoint to the flight where you're standing, or at the given coordinates.\n\n"
                    + "You MUST be in Flight Editing mode when you use this command.")
    @CommandPermissions({"dt.edit.flights", "dt.edit.*"})
    public static void setWaypoint(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        Player player = (Player) sender;

        Flight wipFlight = DragonTravel.getInstance().getFlightEditor().getEditors().get(player);
        if (wipFlight == null) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Error.NotInFlightCreationMode"));
            return;
        }

        Location loc = player.getLocation();

        if (args.argsLength() == 3) {
            loc.setX(args.getInteger(0));
            loc.setY(args.getInteger(1));
            loc.setZ(args.getInteger(2));
        }
        if (args.argsLength() == 4) {
            loc.setX(args.getInteger(0));
            loc.setY(args.getInteger(1));
            loc.setZ(args.getInteger(2));
            loc.setWorld(Bukkit.getWorld(args.getString(3)));
        }
        Waypoint wp = new Waypoint(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        wp.setMarker(player);
        Block block = loc.getBlock();
        DragonTravel.getInstance().getFlightEditor().getWayPointMarkers().put(block, block);

        wipFlight.addWayPoint(wp);

        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Successful.WaypointAdded") + String.format("%s (%s @ %d,%d,%d)", ChatColor.GRAY, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
    }

    @Command(aliases = {"remlastwp", "remwp"},
            desc = "Remove the most recent waypoint",
            usage = "/dt remwp",
            help = "Remove the most recently added waypoint from the flight.\n\n"
                    + "You MUST be in Flight Editing mode when you use this command.")
    @CommandPermissions({"dt.edit.flights", "dt.edit.*"})
    public static void removeWaypoint(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        Player player = (Player) sender;

        Flight wipFlight = DragonTravel.getInstance().getFlightEditor().getEditors().get(player);
        if (wipFlight == null) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Error.NotInFlightCreationMode"));
            return;
        }

        wipFlight.removelastWayPoint();
        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Successful.WaypointRemoved"));
    }

    @Command(aliases = {"setstation", "setstat"},
            desc = "Creates a new station here.",
            usage = "/dt setstation <name> [display_name]",
            min = 1, max = 2,
            help = "Creates a new station with the given name at your current location.")
    @CommandPermissions({"dt.edit.stations", "dt.edit.*"})
    public static void setStation(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        Player player = (Player) sender;
        String station = args.getString(0).toLowerCase();
        String displayName = station;
        if (args.argsLength() == 2) {
            displayName = args.getRemainingString(1);
        }

        if (station.equalsIgnoreCase(DragonTravel.getInstance().getConfig().getString("RandomDest.Name"))) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.NotCreateStationWithRandomstatName"));
            return;
        }

        if (DragonTravel
                .getInstance()
                .getDbStationsHandler()
                .getStation(
                        station) != null) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.StationAlreadyExists").replace("{stationname}", station));
        } else {
            if (DragonTravel.getInstance().getDbStationsHandler().saveStation(new Station(station, displayName, player.getLocation(), player.getUniqueId().toString()))) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Successful.StationCreated").replace("{stationname}", station));
            } else {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.CouldNotCreateStation"));
            }
        }
    }

    @Command(aliases = {"deletestation", "removestat", "remstation", "removestation",
            "delstat", "deletestat", "delstation", "remstat"},
            desc = "Delete a station",
            usage = "/dt delstation <name>",
            help = "Removes the station with the specified name.")
    @CommandPermissions({"dt.edit.stations", "dt.edit.*"})
    public static void removeStation(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        Player player = (Player) sender;
        String station = args.getString(0);

        if (station.equalsIgnoreCase(DragonTravel.getInstance().getConfig().getString("RandomDest.Name"))) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.NotCreateStationWithRandomstatName"));
            return;
        }

        if (DragonTravel.getInstance().getDbStationsHandler() .getStation(station) == null) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.StationDoesNotExist").replace("{stationname}", station));
        } else {
            if (DragonTravel.getInstance().getDbStationsHandler().deleteStation(station)) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Successful.StationRemoved").replace("{stationname}", station));
            } else {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.CouldNotRemoveStation"));
            }
        }
    }

    public static class DragonTravelParentCommand {
        @Command(aliases = {"dt", "dragontravel"}, desc = "DragonTravel commands")
        @CommandPermissions({"dt.seecommand"})
        @NestedCommand({DragonTravelCommands.class})
        public static void dragonTravel() {
        }
    }

}