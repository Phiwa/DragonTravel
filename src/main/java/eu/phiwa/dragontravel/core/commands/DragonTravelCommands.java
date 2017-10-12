package eu.phiwa.dragontravel.core.commands;

import com.sk89q.minecraft.util.commands.*;
import eu.phiwa.dragontravel.api.DragonException;
import eu.phiwa.dragontravel.core.DragonManager;
import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.hooks.payment.ChargeType;
import eu.phiwa.dragontravel.core.hooks.permissions.PermissionsHandler;
import eu.phiwa.dragontravel.core.movement.flight.Flight;
import eu.phiwa.dragontravel.core.movement.flight.Waypoint;
import eu.phiwa.dragontravel.core.movement.newmovement.DTMovement;
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
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.help.HelpTopic;
import org.bukkit.util.ChatPaginator;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

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
    @Command(aliases = {"showflight"},
            desc = "Show details for specified flight",
            usage = "/dt showflight <flightname>",
            help = "Show details for specified flight")
    public static void showFlightDetails(CommandContext args, CommandSender sender) throws CommandException {
        DragonTravel.getInstance().getDbFlightsHandler().showFlightDetails(sender, args.getString(0));
    }

    @Console
    @Command(aliases = {"removedragons", "remdragons"},
            desc = "Remove all dragons",
            usage = "/dt remdragons [-g | world]",
            min = 0, max = 1, flags = "g",
            help = "Removes all dragons (except stationary dragons) without riders.\n"
                    + "It only acts on the world you're currently in, unless you use the -g ('global')")
    //@CommandPermissions({"dt.admin.remdragons"})
    public static void removeDragons(CommandContext args, CommandSender sender) throws CommandException {
    	
    	if(sender instanceof Player) {
    		Player player = (Player)sender;
    		if(!player.hasPermission("dt.admin.remdragons")) {
    			sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
    			return;
    		}
    	}
    	
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
                    sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.WorldNotFound"));
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
        String displayName = args.getString(0);
        if (args.argsLength() == 2) {
            displayName = args.getString(1).replace('_', ' ');
        }
        if (DragonTravel.getInstance().getDragonManager().getStationaryDragons().containsKey(name)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NameTaken"));
            return;
        }
        StationaryDragon sDragon = new StationaryDragon(player, name, displayName, player.getLocation(), true);
        DragonTravel.getInstance().getDragonManager().getStationaryDragons().put(name.toLowerCase(), sDragon);
        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Successful.AddedStatDragon").replace("{dragonname}", displayName));
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
        String inputname = args.getString(0);
        String name = inputname.toLowerCase();
        if (!player.hasPermission("dt.admin.statdragon")) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
            return;
        }

        if (!DragonTravel.getInstance().getDragonManager().getStationaryDragons().keySet().contains(name)) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.StatDragonNotExists").replace("{name}", inputname));
            return;
        }

        StationaryDragon sDragon = DragonTravel.getInstance().getDragonManager().getStationaryDragons().get(name);
        String displayName = sDragon.getDisplayName();
        sDragon.removeDragon(true);
        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Successful.RemovedStatDragon").replace("{dragonname}", displayName));
    }

    @Command(aliases = {"rembugdragon"},
            desc = "Removes the enderdragon with the shortest distance to you (within a 10 block radius). Used if there is a buggy stationary dragon which cannot be removed using the normal command.",
            usage = "/dt rembugdragon",
            min = 0, max = 0)
    public static void deleteStationaryDragonBuggy(CommandContext args, CommandSender sender) throws CommandException {
    	
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("dt.admin.*")) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
            return;
        }
        
        boolean found = false;
        double maxdist = 10;
        for(double dist = 1.0; dist <= maxdist; dist += 0.5) {
            List<Entity> entities = player.getNearbyEntities(dist, dist, dist);
            for(Entity ent: entities) {
                if(!(ent instanceof EnderDragon))
					continue;
                player.sendMessage("Removed bugging dragon "+dist+" blocks away from you.");
                ent.remove();
                found = true;
                break;
            }
			if(found)
				return;
		}
	
		player.sendMessage("There are no dragons within "+(int)maxdist+" blocks around you.");
    }
    
    @Command(aliases = {"dismount"},
            desc = "Get off of the dragon",
            usage = "/dt dismount",
            help = "Dismounts you from the dragons. "
                    + "Depending on the server's settings, "
                    + "you might be teleported back to the "
                    + "point you started your journey from.")
    public static void dismount(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        Player player = (Player) sender;
        DragonTravel.getInstance().getDragonManager().dismount(player, false);
    }

    @SuppressWarnings("deprecation")
	@Console
    @Command(aliases = {"ptoggle"},
            desc = "Toggle whether you can recieve player dragon travels",
            usage = "/dt ptoggle [-y|-n]",
            min = 0, max = 1,
            flags = "yn",
            help = "Toggles whether you allow/don't allow\n player-travels to you.")
    //@CommandPermissions({"dt.ptoggle", "dt.ptoggle.other"})
    public static void ptoggle(CommandContext args, CommandSender sender) throws CommandException {
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
            playerId = p.getUniqueId().toString();
        } else if (sender instanceof Player) {
            playerId = ((Player) sender).getUniqueId().toString();
        } else {
            Bukkit.getLogger().log(Level.SEVERE, "[DragonTravel] The console must provide a player for this command!");
            return;
        }

        if (args.hasFlag('y')) {
            // Allow
            DragonTravel.getInstance().getDragonManager().getPlayerToggles().put(UUID.fromString(playerId), true);
        } else if (args.hasFlag('n')) {
            // Disallow
            DragonTravel.getInstance().getDragonManager().getPlayerToggles().put(UUID.fromString(playerId), false);
        } else {
            if (DragonTravel.getInstance().getDragonManager().getPlayerToggles().containsKey(UUID.fromString(playerId))) {
            	if(DragonTravel.getInstance().getDragonManager().getPlayerToggles().get(UUID.fromString(playerId))) {
            		// Disallow
            		DragonTravel.getInstance().getDragonManager().getPlayerToggles().put(UUID.fromString(playerId), false);
            	} else {
            		// Allow
            		DragonTravel.getInstance().getDragonManager().getPlayerToggles().put(UUID.fromString(playerId), true);
            	}
            }
            else {
            	DragonTravel.getInstance().getDragonManager().getPlayerToggles().put(UUID.fromString(playerId), false);
            }
        }
        // Fancy message sending with the ternary operator
        sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage(DragonTravel.getInstance().getDragonManager().getPlayerToggles().get(UUID.fromString(playerId)) ? "Messages.General.Successful.ToggledPTravelOn" : "Messages.General.Successful.ToggledPTravelOff"));
    }

    @Command(aliases = {"sethome"},
            desc = "Set your DragonTravel home",
            usage = "/dt sethome",
            help = "Sets your DragonTravel home.")
    //@CommandPermissions({"dt.sethome"})
    public static void setHome(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        Player player = (Player) sender;
        
        if (!player.hasPermission("dt.sethome")) {
        	sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
            throw new CommandPermissionsException();
        }
        
        if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.SETHOME, player))
            return;
        Home home = new Home(player.getLocation());
        DragonTravel.getInstance().getDbHomesHandler().saveHome(player.getUniqueId().toString(), home);
        sender.sendMessage(ChatColor.GREEN + "Home set!");
    }
    
    @SuppressWarnings("deprecation")
	@Command(aliases = {"travel"},
            desc = "Travel to another station",
            usage = "/dt travel <station name> [player=you]",
            min = 1, max = 2,
            help = "Brings you (or the given player) to the specified station")
    public static void startMoveStationTravel(CommandContext args, CommandSender sender) throws CommandException {

        String stationname = args.getString(0);
        
        Player player = null;        
        switch (args.argsLength()) {
	        case 1:
	        	// Do not allow this command from console
	        	if (!(sender instanceof Player)) {
	                sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
	                return;
        		}
	        	
	        	// Player does not have the permission to use this command
		        if (!PermissionsHandler.hasTravelPermission(sender, "travel", stationname)) {
		            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
		            return;
		        }
		        
		        player = (Player) sender;
		        sender = null;
		        
		        // Check if player is already riding a dragon
                if (DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player)) {
                	player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.AlreadyMounted"));
                    return;
                }
		        
	        	break;
	        case 2:
	        	// Only allow admins or the console to use this command
	        	if (!sender.hasPermission("dt.*")) {
	        		sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
		            return;
		        }
	        	
		        player = Bukkit.getServer().getPlayer(args.getString(1));
		        
		        // Player to send does not exist
		        if (player == null) {
                    sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Error.CouldNotfindPlayerToSend").replace("{playername}", args.getString(1)));
                    return;
                }
		        
		        // Check if player is already riding a dragon
                if (DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player)) {
                	sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.PlayerAlreadyMounted"));
                    return;
                }
		        
	        	break;
        }	   
        
        // Destination is a random location
        if (stationname.equalsIgnoreCase((DragonTravel.getInstance().getConfig().getString("RandomDest.Name")))) {
        	
        	// Only check for required item and charge player if he was not sent by an admin
        	if (sender == null) {
        		
        		// Check for mounting limit
                if (DragonTravel.getInstance().getConfig().getBoolean("MountingLimit.EnableForTravels") && !player.hasPermission("dt.ignoreusestations.travels")) {

                	// Player is not at a station
                    if (!DragonTravel.getInstance().getDbStationsHandler().checkForStation(player)) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.NotAtAStation"));
                        return;
                    }
                }
        		
                // Check if "RequireItem" is enabled
                if (DragonTravel.getInstance().getConfigHandler().isRequireItemTravelRandom()) {
                	
                	// Check if player has required item
                    if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                        return;
                    }
                }
                
                // Charge player
        		if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.TRAVEL_TORANDOM, player))
        			return;
        	}
        	
            try {
                DTMovement movement = DTMovement.fromRandom(player);
                DragonManager.getDragonManager().getMovementEngine().startMovement(player, movement);
            } catch (DragonException e) {}
        }
        // Destination is a normal station
        else {
        	Station station = DragonTravel.getInstance().getDbStationsHandler().getStation(stationname);
        	
        	// Station does not exist
        	if(station == null) {
        		player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.StationDoesNotExist").replace("{stationname}", stationname));
        		return;
        	}
        	
        	// Only check for required item and charge player if he was not sent by an admin
        	if (sender == null) {
        		
                // Check if "RequireItem" is enabled
                if (DragonTravel.getInstance().getConfigHandler().isRequireItemTravelStation()) {
                	
                	// Check if player has required item
                    if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                        return;
                    }
                }
                
                // Charge player
        		if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.TRAVEL_TOSTATION, player))
        			return;        		
        	}
        		
            try {
            	DTMovement movement = DTMovement.fromStation(player, station);
            	DragonManager.getDragonManager().getMovementEngine().startMovement(player, movement);
            } catch (DragonException e) {}
        }
    }
    
    @SuppressWarnings("deprecation")
	@Command(aliases = {"ptravel", "player"},
            desc = "Travel to another player",
            usage = "/dt ptravel <player>",
            min = 1, max = 1,
            help = "Brings you to the specified player")
    public static void startMovePlayerTravel(CommandContext args, CommandSender sender) throws CommandException {
    	// Do not allow this command from console
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        
        Player player = (Player) sender;
        
        // Player does not have the permission to use this command
        if(!player.hasPermission("dt.ptravel")) {
        	sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
			return;
        }
        
        // Check if player is already riding a dragon
        if (DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player)) {
        	player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.AlreadyMounted"));
            return;
        }
        
        Player targetPlayer = Bukkit.getPlayer(args.getString(0));

        // Target player does not exist
        if (targetPlayer == null) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Error.PlayerNotOnline").replace("{playername}", args.getString(0)));
            return;
        }
        
        // It does not make sense to travel to yourself
        if (targetPlayer == sender) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Error.CannotTravelToYourself"));
            return;
        }
                
        // Check if target player allows travels to him, do not check for admins trying to travel
        if(!player.hasPermission("dt.*")) {
	        if (!DragonTravel.getInstance().getDragonManager().getPlayerToggles().get(targetPlayer.getUniqueId())) {
	            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Error.TargetPlayerDoesnotAllowPTravel").replace("{playername}", args.getString(0)));
	            return;
	        }
        }
        
        // Check for mounting limit
        if (DragonTravel.getInstance().getConfig().getBoolean("MountingLimit.EnableForTravels") && !player.hasPermission("dt.ignoreusestations.travels")) {

        	// Player is not at a station
            if (!DragonTravel.getInstance().getDbStationsHandler().checkForStation(player)) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.NotAtAStation"));
                return;
            }
        }
        
        // Check if "RequireItem" is enabled
        if (DragonTravel.getInstance().getConfigHandler().isRequireItemTravelPlayer()) {
        	
        	// Check if player has required item
            if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                return;
            }
        }
        
        // Charge player
        if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.TRAVEL_TOPLAYER, player))
            return;
        
        try {
        	DTMovement movement = DTMovement.fromPlayer(player, targetPlayer);
        	DragonManager.getDragonManager().getMovementEngine().startMovement(player, movement);
        } catch (DragonException e) {}
    }

    @Command(aliases = {"ctravel", "coord", "coords"},
            desc = "Travel to some coordinates",
            usage = "/dt ctravel x y z [world]",
            min = 3, max = 4,
            help = "Brings you to the specified location")
    public static void startMoveCoordsTravel(CommandContext args, CommandSender sender) throws CommandException {
    	// Do not allow this command from console
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        
        Player player = (Player) sender;

        // Player does not have the permission to use this command
        if(!player.hasPermission("dt.ctravel")) {
        	sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
			return;
        }
        
        // Check if player is already riding a dragon
        if (DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player)) {
        	player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.AlreadyMounted"));
            return;
        }
        
        int x = 0;
        int y = 0;
        int z = 0;
        String worldname = null;
        
        try {
            x = args.getInteger(0);
            y = args.getInteger(1);
            z = args.getInteger(2);
            worldname = args.getString(3, null);
        } catch (NumberFormatException ex) {
        	// Coordinates were no valid numbers
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Error.InvalidCoordinates"));
        }

        World world = null;
        
        // No world specified, using player's current world
        if(worldname == null) {
        	world = player.getLocation().getWorld();
        	DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToCoordinatesSameWorld");
        }
        else {           
        	world = Bukkit.getServer().getWorld(worldname);
        	
        	// Specified world does not exist
        	if(world == null) {
        		player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Error.WorldNotFound"));
        		return;
        	}
        	
        	DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToCoordinatesOtherWorld");
        }
        
        Location loc = new Location(world, x, y, z);
        
        // Check for mounting limit
        if (DragonTravel.getInstance().getConfig().getBoolean("MountingLimit.EnableForTravels") && !player.hasPermission("dt.ignoreusestations.travels")) {

        	// Player is not at a station
            if (!DragonTravel.getInstance().getDbStationsHandler().checkForStation(player)) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.NotAtAStation"));
                return;
            }
        }
        
        // Check if "RequireItem" is enabled
        if (DragonTravel.getInstance().getConfigHandler().isRequireItemTravelCoordinates()) {
        	
        	// Check if player has required item
            if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                return;
            }
        }
        
        // Charge player
        if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.TRAVEL_TOCOORDINATES, player))
            return;
            
        try {  
            DTMovement movement = DTMovement.fromLocation(loc);
            DragonManager.getDragonManager().getMovementEngine().startMovement(player, movement);      
        } catch (DragonException e) {}
    }
    
    @Command(aliases = {"home"},
            desc = "Travel to your home",
            usage = "/dt home",
            help = "Brings you to your home")
    public static void startMoveHomeTravel(CommandContext args, CommandSender sender) throws CommandException {
    	// Do not allow this command from console
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        
        Player player = (Player) sender;
        
        // Player does not have the permission to use this command
        if(!player.hasPermission("dt.travelhome")) {
        	sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
			return;
        }
        
        // Check if player is already riding a dragon
        if (DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player)) {
        	player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.AlreadyMounted"));
            return;
        }
        
        // Check for mounting limit
        if (DragonTravel.getInstance().getConfig().getBoolean("MountingLimit.EnableForTravels") && !player.hasPermission("dt.ignoreusestations.travels")) {

        	// Player is not at a station
            if (!DragonTravel.getInstance().getDbStationsHandler().checkForStation(player)) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.NotAtAStation"));
                return;
            }
        }
        
        // Check if "RequireItem" is enabled
        if (DragonTravel.getInstance().getConfigHandler().isRequireItemTravelHome()) {
        	// Check if player has required item
            if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                return;
            }
        }
        
        // Check if player has a home    	
	    if (DragonTravel.getInstance().getDbHomesHandler().getHome(player.getUniqueId().toString()) == null) {
	        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Error.NoHomeSet"));
	        return;
	    }

        // Charge player
        if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.TRAVEL_TOHOME, player))
        	return;

        try {
            DTMovement movement = DTMovement.fromHome(player);
            DragonManager.getDragonManager().getMovementEngine().startMovement(player, movement);
        } catch (DragonException e) {}
    }
    
    @Command(aliases = {"fhome"},
            desc = "Travel to your faction home",
            usage = "/dt fhome")
    public static void startMoveFactionHomeTravel(CommandContext args, CommandSender sender) throws CommandException {
    	// Do not allow this command from console
    	if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        
        Player player = (Player) sender;
        
        // Check if Factions is available
    	if (Bukkit.getPluginManager().getPlugin("Factions") == null) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.FactionsNotInstalled"));
            return;
        }
        
    	// Player does not have the permission to use this command
        if(!player.hasPermission("dt.fhome")) {
        	sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
			return;
        }
        
        // Check if player is already riding a dragon
        if (DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player)) {
        	player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.AlreadyMounted"));
            return;
        }
        
        // Check for mounting limit
        if (DragonTravel.getInstance().getConfig().getBoolean("MountingLimit.EnableForTravels") && !player.hasPermission("dt.ignoreusestations.travels")) {

        	// Player is not at a station
            if (!DragonTravel.getInstance().getDbStationsHandler().checkForStation(player)) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.NotAtAStation"));
                return;
            }
        }
        
        // Check if "RequireItem" is enabled
        if (DragonTravel.getInstance().getConfigHandler().isRequireItemTravelFactionhome()) {
        	
        	// Check if player has required item
            if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                return;
            }
        }
        
        // Charge player
        if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.TRAVEL_TOFACTIONHOME, player))
            return;
        
        try {
            DTMovement movement = DTMovement.fromFaction(player);
            DragonManager.getDragonManager().getMovementEngine().startMovement(player, movement);
        } catch (DragonException e) {}
    }
    
    @Command(aliases = {"tspawn"},
            desc = "Travel to your town spawn",
            usage = "/dt tspawn")
    //@CommandPermissions({"dt.start.tspawn.command"})
    public static void startMoveTownSpawnTravel(CommandContext args, CommandSender sender) throws CommandException {
    	// Do not allow this command from console
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        
        Player player = (Player) sender;

        // Check if Towny is available
        if (Bukkit.getPluginManager().getPlugin("Towny") == null) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Towny.Error.TownyNotInstalled"));
            return;
        }
        
        // Player does not have the permission to use this command
        if(!player.hasPermission("dt.tspawn")) {
        	sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
			return;
        }

        // Check if player is already riding a dragon
        if (DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player)) {
        	player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.AlreadyMounted"));
            return;
        }
        
        // Check for mounting limit
        if (DragonTravel.getInstance().getConfig().getBoolean("MountingLimit.EnableForTravels") && !player.hasPermission("dt.ignoreusestations.travels")) {

        	// Player is not at a station
            if (!DragonTravel.getInstance().getDbStationsHandler().checkForStation(player)) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.NotAtAStation"));
                return;
            }
        }
        
        // Check if "RequireItem" is enabled
        if (DragonTravel.getInstance().getConfigHandler().isRequireItemTravelTownSpawn()) {
        	
        	// Check if player has required item
            if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                return;
            }
        }
        
        // Charge player
        if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.TRAVEL_TOTOWNSPAWN, player)) 
            return;
        
        try {
        	DTMovement movement = DTMovement.fromTown(player);
            DragonManager.getDragonManager().getMovementEngine().startMovement(player, movement);
        } catch (DragonException e) {}
    }
    
    @SuppressWarnings("deprecation")
	@Console
    @Command(aliases = {"flight"},
            desc = "Start a Flight",
            usage = "/dt flight <flight name> [player=you]",
            min = 1, max = 2,
            help = "Starts the specified flight.")
    public static void startMoveFlight(CommandContext args, CommandSender sender) throws CommandException {
        String flightname = args.getString(0);
        
        Flight flight = DragonTravel.getInstance().getDbFlightsHandler().getFlight(flightname);
               
        // Flight does not exist
        if (flight == null) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Error.FlightDoesNotExist"));
            return;
        }
        
        Player player = null;
        
    	DTMovement movement = DTMovement.fromFlight(flight);
        
        switch (args.argsLength()) {
        
        	// Player starting flight (normal case)
            case 1:
            	// Do not allow this command from console
                if (!(sender instanceof Player)) {
                    sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
                    return;
                }

                player = (Player) sender;
     
                // Player does not have the permission to use this command
                if (!PermissionsHandler.hasFlightPermission(sender, flightname)) {
                	sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
                    throw new CommandPermissionsException();
                }
                
                // Check if player is already riding a dragon
                if (DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player)) {
                	player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.AlreadyMounted"));
                    return;
                }
                
                // Check for mounting limit
                if (DragonTravel.getInstance().getConfig().getBoolean("MountingLimit.EnableForFlights") && !player.hasPermission("dt.ignoreusestations.flights")) {

                	// Player is not at a station
                    if (!DragonTravel.getInstance().getDbStationsHandler().checkForStation(player)) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.NotAtAStation"));
                        return;
                    }
                }

                // Check if "RequireItem" is enabled
                if (DragonTravel.getInstance().getConfigHandler().isRequireItemFlight()) { 
                	
                	// Check if player has required item
                    if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                        return;
                    }
                }
                
                // Charge player
                if (!DragonTravel.getInstance().getPaymentManager().chargePlayer(ChargeType.FLIGHT, player))
                    return;
                
                try {
                	DragonManager.getDragonManager().getMovementEngine().startMovement(player, movement);
                }
                catch (DragonException e) {}
                
                break;

            // Admin sending player on a flight
            case 2:            	
            	// Player does not have the permission to use this command
            	if (!sender.hasPermission("dt.*")) {
            		sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
            		throw new CommandPermissionsException();
            	}

            	// Get target player
                player = Bukkit.getPlayer(args.getString(1));
                
                // Target player does not exist
                if (player == null) {
                    sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Flights.Error.CouldNotfindPlayerToSend").replace("{playername}", args.getString(1)));
                    return;
                }
                
                // Check if player is already riding a dragon
                if (DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player)) {
                	sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.PlayerAlreadyMounted"));
                    return;
                }
                
                try {
                    DragonManager.getDragonManager().getMovementEngine().startMovement(player, movement);
                } catch (DragonException e) {}
                
                break;               
        }       
    }
    
    @Command(aliases = {"createflight", "newflight"},
            desc = "Create a new Flight",
            usage = "/dt createflight",
            min = 1, max = 1,
            help = "Creates a new flight and puts you into the flight-creation mode.\n\n"
                    + "You MUST NOT be in Flight Editing mode when you use this command.")
    //@CommandPermissions({"dt.edit.flights", "dt.edit.*"})
    public static void newFlight(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        
        if(!sender.hasPermission("dt.admin.flights")) {
        	sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
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
    //@CommandPermissions({"dt.edit.flights", "dt.edit.*"})
    public static void removeFlight(CommandContext args, CommandSender sender) throws CommandException {

    	if(!sender.hasPermission("dt.admin.flights")) {
        	sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
			return;
        }
    	
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
    //@CommandPermissions({"dt.edit.flights", "dt.edit.*"})
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
    //@CommandPermissions({"dt.edit.flights", "dt.edit.*"})
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
    //@CommandPermissions({"dt.edit.flights", "dt.edit.*"})
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
    //@CommandPermissions({"dt.edit.stations", "dt.edit.*"})
    public static void setStation(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        
        if(!sender.hasPermission("dt.admin.stations")) {
        	sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
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
    //@CommandPermissions({"dt.edit.stations", "dt.edit.*"})
    public static void removeStation(CommandContext args, CommandSender sender) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoConsole"));
            return;
        }
        
        if(!sender.hasPermission("dt.admin.stations")) {
        	sender.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NoPermission"));
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