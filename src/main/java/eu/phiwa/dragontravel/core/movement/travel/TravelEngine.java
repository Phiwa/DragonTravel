package eu.phiwa.dragontravel.core.movement.travel;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import eu.phiwa.dragontravel.api.DragonException;
import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.hooks.server.IRyeDragon;
import eu.phiwa.dragontravel.core.movement.DragonType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;

public class TravelEngine {

    /**
     * Travel to specified coordinates
     *
     * @param worldName       If "null", the player's current world is used.
     * @param checkForStation Whether or not DragonTravel should check
     *                        if the player is at a station and return if not.
     *                        If the admin disabled the station-check globally,
     *                        this has no function.
     */
    public void toCoordinates(Player player, int x, int y, int z, String worldName, Boolean checkForStation) throws DragonException {

        World world;

        if (worldName == null) {
            //No world was used in the command, using player's current world
            world = player.getWorld();
        } else {
            // Trying to find the world with the name the player used in the command
            world = Bukkit.getWorld(worldName);

            // If the world cannot be found, send an error-message to the player
            if (world == null) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Error.WorldNotFound"));
                return;
            }
        }

        if (DragonTravel.getInstance().getConfigHandler().isRequireItemTravelCoordinates()) {
            if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                return;
            }
        }

        Location loc = new Location(world, x, y, z);
        String message;
        if (Objects.equals(world.getName(), player.getWorld().getName())) {
            message = DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToCoordinatesSameWorld");
            message = message.replace("{x}", "%d");
            message = String.format(message, x);
            message = message.replace("{y}", "%d");
            message = String.format(message, y);
            message = message.replace("{z}", "%d");
            message = String.format(message, z);
            player.sendMessage(message);
        } else {
            message = DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToCoordinatesOtherWorld");
            message = message.replace("{x}", "%d");
            message = String.format(message, x);
            message = message.replace("{y}", "%d");
            message = String.format(message, y);
            message = message.replace("{z}", "%d");
            message = String.format(message, z);
            message = message.replace("{worldname}", "%s");
            message = String.format(message, world.getName());
            player.sendMessage(message);
        }

        travel(player, loc, checkForStation, message, DragonType.LOC_TRAVEL);

    }

    /**
     * Core-method of this class, handles the travel itself (e.g. the difference between normal and interworld-travels
     *
     * @param checkForStation Whether or not DragonTravel should check
     *                        if the player is at a station and return if not.
     *                        If the admin disabled the station-check globally,
     *                        this has no function.
     */
    public void travel(Player player, Location destination, Boolean checkForStation, String destName, DragonType dragonType) throws DragonException {

        if (DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player))
            return;

        // Check for station
        if (checkForStation && DragonTravel.getInstance().getConfig().getBoolean("MountingLimit.EnableForTravels") && !player.hasPermission("dt.ignoreusestations.travels")) {
            if (!DragonTravel.getInstance().getDbStationsHandler().checkForStation(player)) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.NotAtAStation"));
                throw new DragonException("Player is not near a station.");
            }
        }

        Location temploc = player.getLocation();

        if (!player.hasPermission("dt.bypassrequireskylight") && (temploc.getWorld().getHighestBlockYAt(temploc) < temploc.getY() || destination.getWorld().getHighestBlockYAt(destination) < destination.getY())) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequireSkyLight"));
            return;
        }

        // Check if max distance to target is exceeded
        int maxdist = DragonTravel.getInstance().getConfig().getInt("MaxTravelDistance");

        if (maxdist != -1) {
            if (temploc.distance(destination) >= maxdist) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Error.MaxTravelDistanceExceeded"));
                throw new DragonException("Player cannot travel this far in one journey.");
            }
        }

        if (Objects.equals(destination.getWorld().getName(), player.getWorld().getName())) {
            temploc.setYaw(getCorrectYawForPlayer(player, destination));
            player.teleport(temploc);
        } else {
            Location tempLoc2 = new Location(player.getWorld(), player.getLocation().getX() + 1, player.getLocation().getY() + 1, player.getLocation().getZ() + 1);
            temploc.setYaw(getCorrectYawForPlayer(player, tempLoc2));
            player.teleport(temploc);
        }

        if (!DragonTravel.getInstance().getDragonManager().mount(player, true, dragonType))
            return;

        IRyeDragon dragon = DragonTravel.getInstance().getDragonManager().getRiderDragons().get(player);
        dragon.setCustomName(ChatColor.translateAlternateColorCodes('&', destName));
        if (Objects.equals(destination.getWorld().getName(), player.getWorld().getName()))
            dragon.startTravel(destination, false, dragonType);
        else
            dragon.startTravel(destination, true, dragonType);

    }

    private float getCorrectYawForPlayer(Player player, Location destination) {

        if (player.getLocation().getBlockZ() > destination.getBlockZ())
            return (float) (-Math.toDegrees(Math.atan((player.getLocation().getBlockX() - destination.getBlockX()) / (player.getLocation().getBlockZ() - destination.getBlockZ())))) + 180.0F;
        else if (player.getLocation().getBlockZ() < destination.getBlockZ())
            return (float) (-Math.toDegrees(Math.atan((player.getLocation().getBlockX() - destination.getBlockX()) / (player.getLocation().getBlockZ() - destination.getBlockZ()))));
        else
            return player.getLocation().getYaw();
    }

    /**
     * Travel to the specified player's faction's home
     *
     * @param player          Player to bring to his faction's home
     * @param checkForStation Whether or not DragonTravel should check
     *                        if the player is at a station and return if not.
     *                        If the admin disabled the station-check globally,
     *                        this has no function.
     */
    public void toFactionHome(Player player, Boolean checkForStation) throws DragonException {

        if (Bukkit.getPluginManager().getPlugin("Factions") == null) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.FactionsNotInstalled"));
            return;
        }

        if (DragonTravel.getInstance().getConfigHandler().isRequireItemTravelFactionhome()) {
            if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                return;
            }
        }

        Faction faction = UPlayer.get(player).getFaction();


        if (faction.isNone()) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.NoFactionMember"));
            return;
        }

        if (!faction.hasHome()) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Factions.Error.FactionHasNoHome"));
        } else
            travel(player, faction.getHome().asBukkitLocation(), checkForStation, DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToFactionHome"), DragonType.FACTION_TRAVEL);
    }

    /**
     * Travel to the specified player's home
     *
     * @param player          Player to bring to his home
     * @param checkForStation Whether or not DragonTravel should check
     *                        if the player is at a station and return if not.
     *                        If the admin disabled the station-check globally,
     *                        this has no function.
     */
    public void toHome(Player player, Boolean checkForStation) throws DragonException {

        Home home = DragonTravel.getInstance().getDbHomesHandler().getHome(player.getUniqueId().toString());

        if (home == null) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Error.NoHomeSet"));
            return;
        }

        if (DragonTravel.getInstance().getConfigHandler().isRequireItemTravelHome()) {
            if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                return;
            }
        }
        travel(player, home.toLocation(), checkForStation, DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToHome"), DragonType.HOME_TRAVEL);
        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToHome"));
    }

    /**
     * Travel to a specified player
     *
     * @param player          Player to bring to the other player
     * @param targetplayer    Player to bring the traveling player to
     * @param checkForStation Whether or not DragonTravel should check
     *                        if the player is at a station and return if not.
     *                        If the admin disabled the station-check globally,
     *                        this has no function.
     */
    public void toPlayer(Player player, Player targetplayer, Boolean checkForStation) throws DragonException {

        if (DragonTravel.getInstance().getConfigHandler().isRequireItemTravelPlayer()) {
            if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                return;
            }
        }

        Location targetLoc = targetplayer.getLocation();
        travel(player, targetLoc, checkForStation, DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToPlayer").replace("{playername}", targetplayer.getDisplayName()), DragonType.PLAYER_TRAVEL);

    }

    /**
     * Travel to a random destination within the borders set in the config
     *
     * @param player          Player to bring to a random destination
     * @param checkForStation Whether or not DragonTravel should check
     *                        if the player is at a station and return if not
     */
    public void toRandomDest(Player player, Boolean checkForStation) throws DragonException {

        if (DragonTravel.getInstance().getConfigHandler().isRequireItemTravelRandom()) {
            if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                return;
            }
        }

        int minX = DragonTravel.getInstance().getConfig().getInt("RandomDest.Limits.X-Axis.Min");
        int maxX = DragonTravel.getInstance().getConfig().getInt("RandomDest.Limits.X-Axis.Max");
        int minZ = DragonTravel.getInstance().getConfig().getInt("RandomDest.Limits.Z-Axis.Min");
        int maxZ = DragonTravel.getInstance().getConfig().getInt("RandomDest.Limits.Z-Axis.Max");

        double x = minX + (Math.random() * (maxX - 1));
        double z = minZ + (Math.random() * (maxZ - 1));

        Location randomLoc = new Location(player.getWorld(), x, 10, z);
        randomLoc.setY(randomLoc.getWorld().getHighestBlockAt(randomLoc).getY());

        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToRandomLocation"));

        travel(player, randomLoc, checkForStation, DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToRandomLocation"), DragonType.LOC_TRAVEL);
    }

    /**
     * Travel to a specified station
     *
     * @param checkForStation Whether or not DragonTravel should check
     *                        if the player is at a station and return if not.
     *                        If the admin disabled the station-check globally,
     *                        this has no function.
     */
    public void toStation(Player player, String stationName, Boolean checkForStation) throws DragonException {

        Station destination = DragonTravel.getInstance().getDbStationsHandler().getStation(stationName);

        if (destination == null) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Stations.Error.StationDoesNotExist").replace("{stationname}", stationName));
            return;
        }

        if (DragonTravel.getInstance().getConfigHandler().isRequireItemTravelStation()) {
            if (!player.getInventory().contains(DragonTravel.getInstance().getConfigHandler().getRequiredItem()) && !player.hasPermission("dt.notrequireitem.travel")) {
                player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.RequiredItemMissing"));
                return;
            }
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToStation").replace("{stationname}", destination.getDisplayName())));

        travel(player, destination.toLocation(), checkForStation, DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToStation").replace("{stationname}", destination.getDisplayName()), DragonType.STATION_TRAVEL);
    }
}
