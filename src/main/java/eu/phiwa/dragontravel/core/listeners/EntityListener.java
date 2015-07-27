package eu.phiwa.dragontravel.core.listeners;

import eu.phiwa.dragontravel.core.DragonTravelMain;
import eu.phiwa.dragontravel.nms.IRyeDragon;
import eu.phiwa.dragontravel.nms.v1_8_R3.RyeDragon;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() != SpawnReason.CUSTOM)
			return;

        if (!event.getEntity().getType().toString().equals("RyeDragon"))
            return;

		if (!event.isCancelled())
			return;

        if (!DragonTravelMain.getInstance().getConfigHandler().isIgnoreAntiMobspawnAreas())
            event.setCancelled(false);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEnderDragonExplode(EntityExplodeEvent event) {

		if (DragonTravelMain.getInstance().getConfigHandler().isOnlydragontraveldragons() && event.getEntity() instanceof RyeDragon)
			event.setCancelled(true);
		else if (DragonTravelMain.getInstance().getConfigHandler().isAlldragons() && event.getEntity() instanceof EnderDragon)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDeath(EntityDeathEvent event) {

		if (event.getEntity() instanceof RyeDragon)
			return;

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDamage(EntityDamageEvent event) {

		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();
		if (DragonTravelMain.listofDragonriders.containsKey(player))
			if (!DragonTravelMain.getInstance().getConfig().getBoolean("VulnerableRiders"))
				event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(EntityDeathEvent event) {

		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();

		if (!DragonTravelMain.listofDragonriders.containsKey(player))
			return;

		IRyeDragon dragon = DragonTravelMain.listofDragonriders.get(player);
		dragon.getEntity().remove();
		DragonTravelMain.listofDragonriders.remove(player);
	}
}
