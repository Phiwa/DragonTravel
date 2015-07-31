package eu.phiwa.dragontravel.core.movement;

import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.hooks.anticheat.CheatProtectionHandler;
import eu.phiwa.dragontravel.core.hooks.server.IRyeDragon;
import eu.phiwa.dragontravel.core.movement.stationary.StationaryDragon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DragonManager {

    private HashMap<UUID, Long> damageReceipts = new HashMap<>();
    private HashMap<UUID, Boolean> playerToggles = new HashMap<>();
    private ConcurrentHashMap<Player, IRyeDragon> riderDragons = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Player, Location> riderStartPoints = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, StationaryDragon> stationaryDragons = new ConcurrentHashMap<>();

    public void dismount(Player player, Boolean isMultiWorld) {
        if (!riderDragons.containsKey(player)) {
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.NotMounted"));
            return;
        }

        IRyeDragon dragon = riderDragons.get(player);
        if (isMultiWorld)
            removeRiderAndDragon(dragon.getEntity(), (Boolean) null);
        else
            removeRiderAndDragon(dragon.getEntity(), false);
    }

    /**
     * Removes the given Dragon-Entity and its rider from the list of riders,
     * teleports the rider to a safe location on the ground and removes the dragon from the world.
     *
     * @param entity                    Entity to remove
     * @param dismountAtCurrentLocation If set to true, the player is dismounted at his current location.
     *                                  If set to false, he is teleported back to the point he started his travel/flight
     */
    public void removeRiderAndDragon(Entity entity, Boolean dismountAtCurrentLocation) {

        Player player = (Player) entity.getPassenger();

        if (player == null)
            player = getRiderByEntity(entity);

        riderDragons.remove(player);

        // Interworld (dismount before teleport)
        if (dismountAtCurrentLocation == null) {
            Location startLoc = riderStartPoints.get(player);
            entity.eject();
            entity.remove();
            player.teleport(startLoc);
            return;
        }
        // Normal absteigen
        else if (dismountAtCurrentLocation || !DragonTravel.getInstance().getConfig().getBoolean("TeleportToStartOnDismount")) {
            // Teleport player to a safe location
            Location saveTeleportLoc = getSafeLandingLoc(player.getLocation());

            // Eject player and remove dragon from world
            entity.eject();
            entity.remove();

            // Teleport player to safe location
            saveTeleportLoc.setY(saveTeleportLoc.getY() + 1.2);
            player.teleport(saveTeleportLoc);
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Successful.DismountedHere"));
        }
        // Back to start of travel
        else {
            Location startLoc = riderStartPoints.get(player);
            entity.eject();
            entity.remove();
            player.teleport(startLoc);
            player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Successful.DismountedToStart"));
        }

        CheatProtectionHandler.unexemptPlayerFromCheatChecks(player);
    }

    private Player getRiderByEntity(Entity entity) {
        Player player = null;
        for (Entry<Player, IRyeDragon> entry : riderDragons.entrySet()) {
            if (entry.getValue().getEntity() == entity) {
                player = entry.getKey();
                break;
            }
        }
        return player;
    }

    private Location getSafeLandingLoc(Location loc) {
        if (DragonTravel.getInstance().getConfigHandler().isDismountAtExactLocation())
            return loc;
        Location tempLoc = loc.clone();
        int offset = 1;
        boolean reachedFloor = false;

        while (true) {
            if (tempLoc.getY() <= 0) {
                if (reachedFloor)
                    return loc;
                tempLoc.setY(256);
                reachedFloor = true;
            }
            if (!tempLoc.getBlock().isEmpty())
                break;
            tempLoc.setY(tempLoc.getY() - offset);
        }
        return tempLoc;
    }

    public boolean mount(Player player, boolean asNew) {
        // Remove current dragon if the player is already mounted
        if (riderDragons.containsKey(player)) {
            IRyeDragon dragon = riderDragons.get(player);
            removeRiderAndDragon(dragon.getEntity(), true);
        }

        // Check if dragon limit is exceeded
        if (DragonTravel.getInstance().getConfigHandler().getDragonLimit() != -1) {
            if (!player.hasPermission("dt.ignoredragonlimit")) {
                if (riderDragons.size() >= DragonTravel.getInstance().getConfigHandler().getDragonLimit()) {
                    player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.ReachedDragonLimit"));
                    return false;
                }
            }
        }

        // Check if player is below minimum height required to mount a dragon
        if (DragonTravel.getInstance().getConfigHandler().getMinMountHeight() != -1) {
            if (!player.hasPermission("dt.ignoreminheight")) {
                if (player.getLocation().getY() < DragonTravel.getInstance().getConfigHandler().getMinMountHeight()) {
                    player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.BelowMinMountHeight").replace("{minheight}", "" + DragonTravel.getInstance().getConfigHandler().getMinMountHeight()));
                    return false;
                }
            }
        }

        // Check if player received damage within last x seconds
        if (DragonTravel.getInstance().getConfigHandler().getDmgCooldown() != -1) {
            if (!player.hasPermission("dt.ignoredamagerestriction")) {
                if (damageReceipts.containsKey(player.getUniqueId())) {
                    long timeSinceDmgReceived = System.currentTimeMillis() - damageReceipts.get(player.getUniqueId());
                    if (timeSinceDmgReceived < DragonTravel.getInstance().getConfigHandler().getDmgCooldown()) {
                        int waittime = (int) ((DragonTravel.getInstance().getConfigHandler().getDmgCooldown() - timeSinceDmgReceived) / 1000);
                        player.sendMessage(DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.General.Error.DamageCooldown").replace("{seconds}", "" + waittime));
                        return false;
                    } else
                        damageReceipts.remove(player.getUniqueId());
                }
            }
        }

        IRyeDragon ryeDragon = DragonTravel.getInstance().getNmsHandler().getRyeDragon(player.getLocation());
        CheatProtectionHandler.exemptPlayerFromCheatChecks(player);
        ryeDragon.getEntity().setPassenger(player);
        ryeDragon.fixWings();
        riderDragons.put(player, ryeDragon);
        if (asNew)
            riderStartPoints.put(player, player.getLocation());
        return true;
    }

    /**
     * Removes all enderdragons from the specified world
     * which do not have players as passengers
     *
     * @param world World to delete all dragons from
     * @return Success message
     */
    public String removeDragons(org.bukkit.World world, boolean includeStationaryDragons) {
        int passed = 0;

        entity_check:
        for (Entity entity : world.getEntities()) {
            // Check if EnderDragon
            if (!(entity instanceof EnderDragon))
                continue;

            if (!includeStationaryDragons) {
                for (StationaryDragon sDragon : stationaryDragons.values()) {
                    if (sDragon.getDragon().getCustomName().equals(entity.getCustomName()))
                        continue entity_check;
                }
            }
            System.out.println("-----");


            // Check if EnderDragon has a player as passenger
            if (entity.getPassenger() instanceof LivingEntity)
                continue;

            // Remove entity/dragon
            entity.remove();
            passed++;
        }

        return String.format("Removed %d dragons in world '%s'.", passed, world.getName());
    }

    /**
     * Removes the given Dragon-Entity and its rider from the list of riders,
     * teleports the rider to a safe location on the ground below the specified location and removes the dragon from the world.
     *
     * @param entity            Entity to remove
     * @param customDismountLoc A custom location to teleport the player to.
     *                          Player is teleported to a safe location on the groundbelow this location.
     */
    public void removeRiderAndDragon(Entity entity, Location customDismountLoc) {
        Player player = (Player) entity.getPassenger();
        if (player == null)
            player = getRiderByEntity(entity);

        riderDragons.remove(player);
        entity.eject();
        entity.remove();

        player.teleport(customDismountLoc);
        CheatProtectionHandler.unexemptPlayerFromCheatChecks(player);
    }

    public HashMap<UUID, Long> getDamageReceipts() {
        return damageReceipts;
    }

    public void setDamageReceipts(HashMap<UUID, Long> damageReceipts) {
        this.damageReceipts = damageReceipts;
    }

    public HashMap<UUID, Boolean> getPlayerToggles() {
        return playerToggles;
    }

    public void setPlayerToggles(HashMap<UUID, Boolean> playerToggles) {
        this.playerToggles = playerToggles;
    }

    public ConcurrentHashMap<Player, IRyeDragon> getRiderDragons() {
        return riderDragons;
    }

    public void setRiderDragons(ConcurrentHashMap<Player, IRyeDragon> riderDragons) {
        this.riderDragons = riderDragons;
    }

    public ConcurrentHashMap<Player, Location> getRiderStartPoints() {
        return riderStartPoints;
    }

    public void setRiderStartPoints(ConcurrentHashMap<Player, Location> riderStartPoints) {
        this.riderStartPoints = riderStartPoints;
    }

    public ConcurrentHashMap<String, StationaryDragon> getStationaryDragons() {
        return stationaryDragons;
    }

    public void setStationaryDragons(ConcurrentHashMap<String, StationaryDragon> stationaryDragons) {
        this.stationaryDragons = stationaryDragons;
    }
}