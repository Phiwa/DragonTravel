package eu.phiwa.dt.modules;

import eu.phiwa.dt.DragonTravelMain;
import eu.phiwa.dt.RyeDragon;
import eu.phiwa.dt.anticheatplugins.CheatProtectionHandler;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEnderDragon;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map.Entry;

public class DragonManagement {

	/**
	 * Dismounts the given player from his dragon if he is a dragonrider and removes the dragon.
	 * 
	 * @param player
	 * 			Player to dismount from his dragon.
	 * @return
	 */
	public static void dismount(Player player, Boolean interworldtravel) {

		if (!DragonTravelMain.listofDragonriders.containsKey(player)) {
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.NotMounted"));
			return;
		}

		RyeDragon dragon = DragonTravelMain.listofDragonriders.get(player);
		if(interworldtravel)
			removeRiderandDragon(dragon.getEntity(), (Boolean)null);
		else
			removeRiderandDragon(dragon.getEntity(), false);	}

	/** If player got dismounted because of water/SHIFT-clicking and hasn't been mounted again by the scheduler yet
	 * 
	 * @param entity
	 * 		Entity of the dragon the Player is sitting on
	 * @return
	 * 		Player riding the specified entity
	 */
	private static Player getRiderByEntity(Entity entity) {
		
		Player player = null;
		
		// If player got dismounted because of water/SHIFT-clicking
		// and hasn't been mounted again yet by the scheduler
		// => Get the player using the "scheduler-method"
		for(Entry<Player, RyeDragon> entry : DragonTravelMain.listofDragonriders.entrySet()){
			if(entry.getValue().getEntity() == entity) {
				player = entry.getKey();
				break;
			}
		}
		
		return player;
	}

	/** Find a safe Location to dismount a player with specified X- and Z-value
	 * 
	 * @param loc
	 * 		Location to find a safe Y-value for
	 * @return
	 * 		Location with safe Y-value
	 */
	private static Location getSaveTeleportLocation(Location loc) {
		
		if(DragonTravelMain.dismountAtExactLocation)
			return loc;
		
		Location clone = loc;
		
		int offset = 1;

		boolean reachedFloor = false;
		
		while(true) {
			
			// If floor is reached
			if(clone.getY() <= 0) {
				
				// If floor has been already reached before, just drop the player
				if(reachedFloor)
					return loc;
				
				// If floor hasn't been reached before, start from the top
				clone.setY(256);
				reachedFloor = true;
			}
			
			// If a non-empty block has been found you have a valid dismount location
			if(!clone.getBlock().isEmpty())
				break;
			
			// Go down one block and restart loop
			clone.setY(clone.getY() - offset);
		}
		
		// Return location the player can be dismounted to
		return clone;
	}

	/**
	 * Spawns a dragon and places the given player on it.
	 * Afterwards it adds both, the player and his dragon, to the list of dragonriders.
	 * 
	 * @param player
	 * 			The player to mount onto the dragon
	 */
	public static boolean mount(Player player, boolean setNewStartingPoint) {

		// Remove current dragon if the player is already mounted
		if (DragonTravelMain.listofDragonriders.containsKey(player)) {
			RyeDragon dragon = DragonTravelMain.listofDragonriders.get(player);		
			removeRiderandDragon(dragon.getEntity(), true);
		}
		
		// Check if dragon limit is exceeded
		if(DragonTravelMain.dragonLimit != -1) {
			if(!player.hasPermission("dt.ignoredragonlimit")) {
				if(DragonTravelMain.listofDragonriders.size() >= DragonTravelMain.dragonLimit) {			
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.ReachedDragonLimit"));
					return false;
				}
			}
		}

		// Check if player is below minimum height required to mount a dragon
		if(DragonTravelMain.minMountHeight != -1) {				
			if(!player.hasPermission("dt.ignoreminheight")) {
				if(player.getLocation().getY() < DragonTravelMain.minMountHeight) {
					player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.BelowMinMountHeight").replace("{minheight}", ""+DragonTravelMain.minMountHeight));
					return false;
				}
			}					
		}
	
		// Check if player received damage within last x seconds
		if(DragonTravelMain.dmgCooldown != -1) {				
			if(!player.hasPermission("dt.ignoredamagerestriction")) {				
				if(DragonTravelMain.dmgReceivers.containsKey(player.getUniqueId())) {		
					
					long timeSinceDmgReceived = System.currentTimeMillis() - DragonTravelMain.dmgReceivers.get(player.getUniqueId());
				
					if(timeSinceDmgReceived  < DragonTravelMain.dmgCooldown ) {		
						
						int waittime = (int) ((DragonTravelMain.dmgCooldown - timeSinceDmgReceived) / 1000);
						
						player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Error.DamageCooldown").replace("{seconds}", ""+waittime));
						return false;
					}
					else
						DragonTravelMain.dmgReceivers.remove(player.getUniqueId());
				}			
			}					
		}

		// Spawn RyeDragon
		World craftWorld = ((CraftWorld) player.getWorld()).getHandle();
		RyeDragon ryeDragon = new RyeDragon(player.getLocation(), craftWorld);
		craftWorld.addEntity(ryeDragon, SpawnReason.CUSTOM);
		final LivingEntity dragon = (LivingEntity) ryeDragon.getEntity();
		
		CheatProtectionHandler.exemptPlayerFromCheatChecks(player);	
		dragon.setPassenger(player);
		dragon.damage(2, dragon.getPassenger());
		DragonTravelMain.listofDragonriders.put(player, ryeDragon);
		
		if(setNewStartingPoint)
			DragonTravelMain.listofDragonsridersStartingpoints.put(player, player.getLocation());

		Bukkit.getScheduler().runTaskLater(DragonTravelMain.plugin, new Runnable(){
			@Override
			public void run() {
				dragon.damage(2, dragon.getPassenger());
				dragon.setHealth(dragon.getMaxHealth());
				dragon.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1, false));
			}
		}, 2L);
		return true;
	}

	/**Removes all enderdragons from all worlds
	 * which do not have players as passengers
	 * 
	 * @return
	 * 		Success message
	 */
	public static String removeDragons() {
		
		String output = "Removing riderless dragons from all worlds:";
		
		for(org.bukkit.World world: Bukkit.getWorlds())
			output += "\n  - " + removeDragons(world);
		
		return output;	
	}
	
	/**Removes all enderdragons from the specified world
	 * which do not have players as passengers
	 * 
	 * @param world
	 * 			World to delete all dragons from
	 * @return
	 * 		Success message
	 */
	public static String removeDragons(org.bukkit.World world) {

		int passed = 0;

		for (Entity entity : world.getEntitiesByClass(EnderDragon.class)) {

			// Check if EnderDragon
			if (!(entity instanceof CraftEnderDragon))
				continue;

			if(entity instanceof RyeDragon){
				if(DragonTravelMain.listofStatDragons.values().contains((RyeDragon)entity))
					continue;
				else
					System.out.println("-----");
			}

			
			// Check if EnderDragon has a player as passenger
			if (entity.getPassenger() instanceof Player)
				continue;

			// Remove entity/dragon
			entity.remove();
			passed++;
		}

		String returnMessage = String.format("Removed %d dragons in world '%s'.", passed, world.getName());
		return returnMessage;
	}

	/**
	 * Removes the given Dragon-Entity and its rider from the list of riders, 
	 * teleports the rider to a safe location on the ground and removes the dragon from the world.
	 * 
	 * @param entity
	 * 			Entity to remove
	 * @param dismountAtcurrentLocation
	 * 			If set to true, the player is dismounted at his current location.
	 * 			If set to false, he is teleported back to the point he started his travel/flight
	 */
	public static void removeRiderandDragon(Entity entity, Boolean dismountAtcurrentLocation) {

		Player player = (Player) entity.getPassenger();

		if(player == null)
			player = getRiderByEntity(entity);	
		
		DragonTravelMain.listofDragonriders.remove(player);

		// Interworld (dismount before teleport)
		if(dismountAtcurrentLocation == null){
			Location startLoc = DragonTravelMain.listofDragonsridersStartingpoints.get(player);
			entity.eject();
			entity.remove();
			player.teleport(startLoc);
			return;
		}
		// Normal absteigen
		else if(dismountAtcurrentLocation || !DragonTravelMain.config.getBoolean("TeleportToStartOnDismount")) {
						
			// Teleport player to a safe location
			Location saveTeleportLoc = getSaveTeleportLocation(player.getLocation());
		
			// Eject player and remove dragon from world
			entity.eject();
			entity.remove();

			// Teleport player to safe location
			saveTeleportLoc.setY(saveTeleportLoc.getY() + 1.2);
			player.teleport(saveTeleportLoc);
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Successful.DismountedHere"));
		}
		// Back to start of travel
		else {
			Location startLoc = DragonTravelMain.listofDragonsridersStartingpoints.get(player);
			entity.eject();
			entity.remove();
			player.teleport(startLoc);
			player.sendMessage(DragonTravelMain.messagesHandler.getMessage("Messages.General.Successful.DismountedToStart"));
		}

		CheatProtectionHandler.unexemptPlayerFromCheatChecks(player);
	}
	
	/**
	 *  Removes the given Dragon-Entity and its rider from the list of riders, 
	 *  teleports the rider to a safe location on the ground below the specified location and removes the dragon from the world.
	 *  
	 * @param entity
	 * 			Entity to remove
	 * @param customDismountLoc
	 * 			A custom location to teleport the player to.
	 * 			Player is teleported to a safe location on the groundbelow this location.
	 */
	public static void removeRiderandDragon(Entity entity, Location customDismountLoc) {	
		
		Player player = (Player) entity.getPassenger();
		
		// If player got dismounted because of water/SHIFT-clicking
		// and hasn't been mounted again yet by the scheduler
		// => Get the player using the "scheduler-method"
		if(player == null)
			player = getRiderByEntity(entity);	
		
		DragonTravelMain.listofDragonriders.remove(player);
		entity.eject();
		entity.remove();
		
		player.teleport(customDismountLoc);
		CheatProtectionHandler.unexemptPlayerFromCheatChecks(player);
		return;
	}

}
