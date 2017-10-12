package eu.phiwa.dragontravel.api;

import eu.phiwa.dragontravel.core.DragonManager;
import eu.phiwa.dragontravel.core.movement.flight.Waypoint;
import eu.phiwa.dragontravel.core.movement.newmovement.DTMovement;
import eu.phiwa.dragontravel.core.movement.stationary.StationaryDragon;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * The DragonTravel API
 */
public class RyeDragonAPI {

    private static RyeDragonAPI instance;

    private RyeDragonAPI() {
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
    	
    	if(player == null)
    		throw new DragonException("Player does not exist!");
    	
    	if(loc == null)
    		throw new DragonException("Invalid target location!");
    	
    	DTMovement movement = DTMovement.fromLocation(loc);
    	DragonManager.getDragonManager().getMovementEngine().startMovement(player, movement);
    }
    
    /**
     * Send a player to a specified location.
     *
     * @param player The player to send.
     * @param loc    The location to send them to.
     */
    public void sendOnFlight(Player player, List<Waypoint> waypoints) throws DragonException {
    	
    	if(player == null)
    		throw new DragonException("Player does not exist!");
    	
    	if(waypoints == null || waypoints.size() == 0)
    		throw new DragonException("Not enough waypoints, at least one waypoint is needed!");
    	    	
    	DTMovement movement = DTMovement.fromWaypoints("SomeMovement", waypoints);
    	DragonManager.getDragonManager().getMovementEngine().startMovement(player, movement);
    }

    /**
     * Recommended only for advanced users. Use in conjunction with source code to ensure correct functionality.
     *
     * @return The core dragon manager.
     */
    public DragonManager getDragonManager() {
        return DragonManager.getDragonManager();
    }


    public static RyeDragonAPI getAPI() {
        if (instance == null) {
            return new RyeDragonAPI();
        } else {
            return instance;
        }
    }

}
