package eu.phiwa.dragontravel.core.movement.newmovement;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import eu.phiwa.dragontravel.api.DragonException;
import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.hooks.server.IRyeDragon;
import eu.phiwa.dragontravel.core.movement.DragonType;

public class MovementEngine {

	public void startMovement(Player player, DTMovement movement) throws DragonException {

		// TODO: Switch to movements for all RyeDragon before CB 1.12 as well
		// TODO: Check wing-flapping workaround (water), should not spawn water all the time
		// TODO: Check dismount location (distance to waypoint for dist), is currently to large
		
     	if (!DragonTravel.getInstance().getDragonManager().mount(player, true, DragonType.MOVEMENT)) {
     		System.out.println("Could not mount player...");
            return;
     	}
     	
     	String message = DragonTravel.getInstance().getMessagesHandler().getMessage("Messages.Travels.Successful.TravellingToStation").replace("{stationname}", movement.getDestinationName());
     	
     	IRyeDragon dragon = DragonTravel.getInstance().getDragonManager().getRiderDragons().get(player);
        dragon.setCustomName(ChatColor.translateAlternateColorCodes('&', message));
        dragon.startMovement(movement);
	}

	
}
