package eu.phiwa.dragontravel.nms;

import org.bukkit.Location;

public interface IEntityRegister {

    boolean registerEntity();

    void spawnEntity(Location loc, IRyeDragon dragon);
}
