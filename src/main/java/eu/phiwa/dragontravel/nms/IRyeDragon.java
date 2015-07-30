package eu.phiwa.dragontravel.nms;

import eu.phiwa.dragontravel.core.objects.Flight;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface IRyeDragon {

    void flight();

    void setMoveFlight();

    void startFlight(Flight flight);

    void travel();

    void setMoveTravel();

    void startTravel(Location destLoc, boolean interworld);

    MovementType getMovementType();

    Entity getEntity();

    String getCustomName();

    void setCustomName(String name);

    void fixWings();

    void setCustomNameVisible(boolean b);
}
