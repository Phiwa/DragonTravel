package eu.phiwa.dragontravel.api.events;

import eu.phiwa.dragontravel.core.hooks.server.IRyeDragon;
import eu.phiwa.dragontravel.core.movement.DragonType;
import org.bukkit.entity.Player;

public class DragonPrePlayerMountEvent extends DragonCancellableEvent {

    private Player player;
    private IRyeDragon dragon;
    private DragonType dragonType;

    public DragonPrePlayerMountEvent(Player player, IRyeDragon dragon, DragonType dragonType) {
        this.player = player;
        this.dragon = dragon;
        this.dragonType = dragonType;
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

    public DragonType getDragonType() {
        return dragonType;
    }

    public void setDragonType(DragonType dragonType) {
        this.dragonType = dragonType;
    }
}
