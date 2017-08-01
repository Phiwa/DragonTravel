package eu.phiwa.dragontravel.core.hooks.server;

import eu.phiwa.dragontravel.core.movement.DragonType;
import eu.phiwa.dragontravel.core.movement.flight.Flight;
import eu.phiwa.dragontravel.core.movement.newmovement.DTMovement;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface IRyeDragon {

    void flight();

    void setMoveFlight();

    void startFlight(Flight flight, DragonType dragonType);

    void travel();

    void setMoveTravel();

    void startTravel(Location destLoc, boolean interWorld, DragonType dragonType);

    DragonType getDragonType();

    Entity getEntity();

    String getCustomName();

    void setCustomName(String name);

    void fixWings();

    void setCustomNameVisible(boolean b);

    
	void setMovementMove();

	void startMovement(DTMovement movement);
}
