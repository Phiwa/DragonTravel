package main.java.eu.phiwa.dt.modules;

import java.util.HashMap;
import java.util.Map.Entry;

import main.java.eu.phiwa.dt.DragonTravelMain;
import main.java.eu.phiwa.dt.RyeDragon;

import org.bukkit.entity.Player;

public class MountingScheduler implements Runnable {

	@Override
	public void run() {
		
		HashMap<Player, RyeDragon> dragonRiders = DragonTravelMain.listofDragonriders;
		
		for(Entry<Player, RyeDragon> entry : dragonRiders.entrySet()){
			try{
				entry.getValue().getBukkitEntity().setPassenger(entry.getKey());
			} 
			catch (Exception ex) {}
		}
	}

}
