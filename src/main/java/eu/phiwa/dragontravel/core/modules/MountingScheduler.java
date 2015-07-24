package eu.phiwa.dragontravel.core.modules;

import eu.phiwa.dragontravel.core.DragonTravelMain;
import eu.phiwa.dragontravel.nms.IRyeDragon;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map.Entry;

public class MountingScheduler implements Runnable {

	@Override
	public void run() {

		HashMap<Player, IRyeDragon> dragonRiders = DragonTravelMain.listofDragonriders;

		for (Entry<Player, IRyeDragon> entry : dragonRiders.entrySet()) {
			try {
				entry.getValue().getEntity().setPassenger(entry.getKey());
			} catch (Exception ex) {
			}
		}
	}

}
