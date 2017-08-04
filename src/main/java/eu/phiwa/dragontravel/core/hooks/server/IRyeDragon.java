package eu.phiwa.dragontravel.core.hooks.server;

import eu.phiwa.dragontravel.core.movement.DragonType;
import eu.phiwa.dragontravel.core.movement.newmovement.DTMovement;

import org.bukkit.entity.Entity;

public interface IRyeDragon {

    DragonType getDragonType();

    Entity getEntity();

    String getCustomName();

    void setCustomName(String name);

    void fixWings();

    void setCustomNameVisible(boolean b);
    
	void setMovementMove();

	void startMovement(DTMovement movement);

	void movement();
}
