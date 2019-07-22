package eu.phiwa.dragontravel.core.hooks.server;

import eu.phiwa.dragontravel.core.movement.DragonType;
import eu.phiwa.dragontravel.core.movement.flight.Flight;
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

    String getCustomDragonName();

    void setCustomDragonName(String name);

    void fixWings();

    void setCustomNameVisible(boolean b);
}
