package eu.phiwa.dt.movement;

import eu.phiwa.dt.DragonTravelMain;

public enum TravelType {
    TOSTATION(),
    TORANDOM(),
    TOPLAYER(),
    TOCOORDINATES(),
    TOHOME(),
    TOFACTIONHOME(),
    FLIGHT(),
    SETHOME();

    private final double cost;

    TravelType() {
        cost = 0;
    }

    public double getCost(TravelType type) {
        return cost;
    }
}
