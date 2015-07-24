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

    Entity getEntity();

    float getCorrectYaw(double targetx, double targetz);
    float getCorrectPitch(double targetx, double targety, double targetz);

    void setCustomName(String name);

    double getCoveredDist();
    double getTotalDist();
    void setCoveredDist(double dist);
    void setTotalDist(double dist);

}
