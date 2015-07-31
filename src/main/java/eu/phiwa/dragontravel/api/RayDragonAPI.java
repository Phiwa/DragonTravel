package eu.phiwa.dragontravel.api;

import eu.phiwa.dragontravel.core.DragonManager;
import eu.phiwa.dragontravel.core.movement.stationary.StationaryDragon;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * The DragonTravel API
 */
public class RayDragonAPI {

    private static RayDragonAPI instance;

    private RayDragonAPI() {
        instance = this;
    }

    /**
     * Create a new stationary dragon, registered with the plugin, with the given parameters.
     *
     * @param loc         The location to spawn the dragon.
     * @param name        The dragon's identity name.
     * @param displayName The dragon's display name for the Boss Bar. Must be formatted externally.
     * @param owner       The owner ID, in case of player owned dragons. This must be UUID.
     * @throws DragonException If name is already taken.
     */
    public void makeStationaryDragon(Location loc, String name, String displayName, UUID owner) throws DragonException {
        if (DragonManager.getDragonManager().getStationaryDragons().containsKey(name.toLowerCase())) {
            throw new DragonException(String.format("Stationary Dragon name %s is already taken.", name.toLowerCase()));
        }
        new StationaryDragon(name, displayName, loc, owner.toString(), true);
    }

    /**
     * Remove an existing stationary dragon.
     *
     * @param name The identity name of the dragon to remove.
     * @throws DragonException If the dragon name is not recognised.
     */
    public void removeStationaryDragon(String name) throws DragonException {
        if (!DragonManager.getDragonManager().getStationaryDragons().containsKey(name.toLowerCase())) {
            throw new DragonException(String.format("Stationary Dragon name %s is not recognised.", name.toLowerCase()));
        }
        DragonManager.getDragonManager().getStationaryDragons().get(name.toLowerCase()).removeDragon(true);
        DragonManager.getDragonManager().getStationaryDragons().remove(name.toLowerCase());
    }

    /**
     * Send a player to a specified location.
     *
     * @param player The player to send.
     * @param loc    The location to send them to.
     */
    public void sendOnTravel(Player player, Location loc) throws DragonException {
        DragonManager.getDragonManager().getTravelEngine().toCoordinates(player, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(), false);
    }

    /**
     * Recommended only for advanced users. Use in conjunction with source code to ensure correct functionality.
     *
     * @return The core dragon manager.
     */
    public DragonManager getDragonManager() {
        return DragonManager.getDragonManager();
    }


    public static RayDragonAPI getAPI() {
        if (instance == null) {
            return new RayDragonAPI();
        } else {
            return instance;
        }
    }

}
