package eu.phiwa.dragontravel.api.events;

import eu.phiwa.dragontravel.core.hooks.server.IRyeDragon;
import eu.phiwa.dragontravel.core.movement.MovementType;
import org.bukkit.entity.Player;

public class DragonPrePlayerMountEvent extends DragonCancellableEvent {

    private Player player;
    private IRyeDragon dragon;
    private MovementType movementType;

    public DragonPrePlayerMountEvent(Player player, IRyeDragon dragon, MovementType movementType){
        this.player = player;
        this.dragon = dragon;
        this.movementType = movementType;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public IRyeDragon getDragon() {
        return dragon;
    }

    public void setDragon(IRyeDragon dragon) {
        this.dragon = dragon;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

}
