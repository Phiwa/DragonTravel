package eu.phiwa.dt.modules;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.RyeDragon;

public class MountingScheduler implements Runnable {

	@Override
	public void run() {

		HashMap<Player, RyeDragon> dragonRiders = DragonTravelMain.listofDragonriders;

		for (Entry<Player, RyeDragon> entry : dragonRiders.entrySet()) {
			try {
				entry.getValue().getBukkitEntity().setPassenger(entry.getKey());
			} catch (Exception ex) {
			}
		}
	}
}
