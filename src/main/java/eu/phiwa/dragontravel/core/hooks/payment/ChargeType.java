package eu.phiwa.dragontravel.core.hooks.payment;

import org.bukkit.entity.Player;

public enum ChargeType {
    TRAVEL_TOSTATION("dt.nocost.travel"),
    TRAVEL_TORANDOM("dt.nocost.randomtravel"),
    TRAVEL_TOPLAYER("dt.nocost.ptravel"),
    TRAVEL_TOCOORDINATES("dt.nocost.ctravel"),
    TRAVEL_TOHOME("dt.nocost.home"),
    TRAVEL_TOFACTIONHOME("dt.nocost.fhome"),
    FLIGHT("dt.nocost.flight"),
    SETHOME("dt.nocost.home.set"),;

    private final String noCostPermission;

    ChargeType(String noCostPermission) {
        this.noCostPermission = noCostPermission;
    }

    public boolean hasNoCostPermission(Player player) {
        return player.hasPermission(noCostPermission);
    }
}