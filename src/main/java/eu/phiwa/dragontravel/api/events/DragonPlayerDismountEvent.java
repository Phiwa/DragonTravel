package eu.phiwa.dragontravel.api.events;

import eu.phiwa.dragontravel.core.hooks.server.IRyeDragon;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DragonPlayerDismountEvent extends DragonEvent {

    private Player player;
    private IRyeDragon dragon;
    private Location dismountLoc;

    public DragonPlayerDismountEvent(Player player, IRyeDragon dragon, Location dismountLoc){
        this.player = player;
        this.dragon = dragon;
        this.dismountLoc = dismountLoc;
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

    public Location getDismountLoc() {
        return dismountLoc;
    }

    public void setDismountLoc(Location dismountLoc) {
        this.dismountLoc = dismountLoc;
    }
}
