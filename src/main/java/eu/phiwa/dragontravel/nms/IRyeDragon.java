package eu.phiwa.dragontravel.nms;

import eu.phiwa.dragontravel.core.objects.Flight;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface IRyeDragon {

    double coveredDist = 0;
    double totalDist = 0;

    void flight();

    void setMoveFlight();

    void startFlight(Flight flight);

    void travel();

    void setMoveTravel();

    void startTravel(Location destLoc, boolean interworld);

    MovementType getMovementType();

    Entity getEntity();

    float getCorrectYaw(Location toLoc);

    String getCustomName();

    void setCustomName(String name);

    double getCoveredDist();

    void setCoveredDist(double dist);

    double getTotalDist();

    void setTotalDist(double dist);

    void fixWings();

    void setCustomNameVisible(boolean b);
}
