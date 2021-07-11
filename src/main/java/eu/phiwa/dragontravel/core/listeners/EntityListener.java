package eu.phiwa.dragontravel.core.listeners;

import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.hooks.server.IRyeDragon;
import eu.phiwa.dragontravel.nms.v1_8_R3.RyeDragon;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

public class EntityListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) {

		if (!event.getEntity().getType().toString().equals("ENDER_DRAGON"))
			return;

		if (!event.isCancelled())
			return;

		if (DragonTravel.getInstance().getConfigHandler().isIgnoreAntiMobspawnAreas())
			event.setCancelled(false);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEnderDragonExplode(EntityExplodeEvent event) {
		if(event.getEntity() instanceof EnderDragon){
			if (DragonTravel.getInstance().getConfigHandler().isOnlydragontraveldragons() && event.getEntity() instanceof RyeDragon)
				event.setCancelled(true);
			else if (DragonTravel.getInstance().getConfigHandler().isAlldragons() && event.getEntity() instanceof EnderDragon)
				event.setCancelled(true);
		}		
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDamage(EntityDamageEvent event) {

		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();
		if (DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player))
			if (!DragonTravel.getInstance().getConfig().getBoolean("VulnerableRiders"))
				event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(EntityDeathEvent event) {

		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();

		if (!DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player))
			return;

		IRyeDragon dragon = DragonTravel.getInstance().getDragonManager().getRiderDragons().get(player);
		dragon.getEntity().remove();
		DragonTravel.getInstance().getDragonManager().getRiderDragons().remove(player);
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerUnmount(EntityDismountEvent event) {

		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();

		if (!DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(player))
			return;
		event.setCancelled(true);
	}
}
